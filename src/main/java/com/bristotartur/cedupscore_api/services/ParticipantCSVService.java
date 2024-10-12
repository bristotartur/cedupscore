package com.bristotartur.cedupscore_api.services;

import com.bristotartur.cedupscore_api.domain.*;
import com.bristotartur.cedupscore_api.dtos.request.ParticipantCSVDto;
import com.bristotartur.cedupscore_api.dtos.response.ParticipantInactivationReport;
import com.bristotartur.cedupscore_api.dtos.response.ParticipantRegistrationReport;
import com.bristotartur.cedupscore_api.enums.Gender;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import com.bristotartur.cedupscore_api.enums.Status;
import com.bristotartur.cedupscore_api.exceptions.BadRequestException;
import com.bristotartur.cedupscore_api.exceptions.ConflictException;
import com.bristotartur.cedupscore_api.exceptions.InternalServerErrorException;
import com.bristotartur.cedupscore_api.exceptions.UnprocessableEntityException;
import com.bristotartur.cedupscore_api.mappers.ParticipantMapper;
import com.bristotartur.cedupscore_api.mappers.RegistrationMapper;
import com.bristotartur.cedupscore_api.repositories.EditionRegistrationRepository;
import com.bristotartur.cedupscore_api.repositories.ParticipantRepository;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvBadConverterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantCSVService {

    private final ParticipantRepository participantRepository;
    private final EditionRegistrationRepository editionRegistrationRepository;
    private final ParticipantValidationService participantValidator;
    private final EditionService editionService;
    private final ParticipantMapper participantMapper;
    private final RegistrationMapper registrationMapper;

    public byte[] generateParticipantsCSV(String type, List<ParticipantCSVDto> dtos) {
        var csvContent = new StringBuilder();

        return switch (type) {
            case "registration" -> {
                csvContent.append("nome,cpf,gênero,tipo,equipe,mensagem\n");
                dtos.forEach(dto ->
                        csvContent.append(formatCsvLine(
                                dto.getName(), dto.getCpf(), dto.getGender(), dto.getType(), dto.getTeamName(), dto.getMessage()
                        ))
                );
                yield csvContent.toString().getBytes(StandardCharsets.UTF_8);
            }
            case "inactivation" -> {
                csvContent.append("nome,cpf,mensagem\n");
                dtos.forEach(dto ->
                        csvContent.append(formatCsvLine(dto.getName(), dto.getCpf(), dto.getMessage()))
                );
                yield csvContent.toString().getBytes(StandardCharsets.UTF_8);
            }
            default -> throw new BadRequestException("O tipo '%s' não é válido para geração de CSV.".formatted(type));
        };
    }

    private String formatCsvLine(String... values) {
        return Arrays.stream(values)
            .map(value -> value == null ? "N/A" : value)
            .collect(Collectors.joining(",")) + "\n";
    }

    public ParticipantRegistrationReport handleParticipantsRegistrationCSVFile(MultipartFile file) {
        var participantsWithProblems = new HashSet<ParticipantCSVDto>();
        var currentEdition = this.getCurrentEdition();
        var dtos = new ArrayList<>(this.parseCSV(file).stream().toList());
        var total = dtos.size();

        this.filterParticipantsByTeam(dtos, currentEdition, participantsWithProblems);
        this.filterParticipantsByCpf(dtos, participantsWithProblems);

        var teamsIdsByCpfMap = new HashMap<String, Long>();
        var existingParticipantsMap = this.findAndValidateExistingParticipantsByCpf(dtos, teamsIdsByCpfMap, participantsWithProblems);

        var savedParticipants = participantRepository.saveAll(dtos.stream()
                .map(dto -> this.processParticipantForCreation(dto, existingParticipantsMap, participantsWithProblems))
                .filter(Objects::nonNull).toList()
        );
        var added = savedParticipants.size();
        var notAdded = existingParticipantsMap.size();
        savedParticipants.addAll(existingParticipantsMap.values());

        var rejected = participantsWithProblems.size();
        var registered = this.registerAllParticipantsInEdition(savedParticipants, currentEdition, teamsIdsByCpfMap, participantsWithProblems).size();
        var problems = participantsWithProblems.size();
        var notRegistered = problems - rejected;

        return participantMapper.toParticipantRegistrationReport(
                total, added, notAdded, registered, problems, rejected, notRegistered, new ArrayList<>(participantsWithProblems)
        );
    }

    public ParticipantInactivationReport handleParticipantsInactivationCSVFile(MultipartFile file) {
        var participantsWithProblems = new HashSet<ParticipantCSVDto>();
        var dtos = new ArrayList<>(this.parseCSV(file).stream().toList());
        var total = dtos.size();

        var existingParticipants = this.filterExistingParticipantsByCpf(dtos, participantsWithProblems);

        var inactivatedParticipants = participantRepository.saveAll(existingParticipants.stream()
                .filter(Participant::getIsActive)
                .map(participant -> this.processParticipantForInactivation(participant, participantsWithProblems))
                .filter(Objects::nonNull).toList()
        );
        var inactivated = inactivatedParticipants.size();
        var notInactivated = total - inactivated;
        var problems = participantsWithProblems.size();

        return participantMapper.toParticipantInactivationReportDto(
                total, inactivated, notInactivated, problems, new ArrayList<>(participantsWithProblems)
        );
    }

    private Set<ParticipantCSVDto> parseCSV(MultipartFile file) {
        try (
                final var reader = new BufferedReader(new InputStreamReader((file.getInputStream())))
        ) {
            var strategy = new HeaderColumnNameMappingStrategy<ParticipantCSVDto>();
            strategy.setType(ParticipantCSVDto.class);

            var csvToBean = new CsvToBeanBuilder<ParticipantCSVDto>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return new HashSet<>(csvToBean.parse());
        } catch (IOException e) {
            throw new InternalServerErrorException(e.getMessage(), e);
        } catch (IllegalStateException | CsvBadConverterException  e) {
            throw new UnprocessableEntityException(e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new UnprocessableEntityException("A formatação do arquivo parece não estar correta.");
        }
    }

    private Edition getCurrentEdition() {
        return editionService.findByStatusDifferentThen(Status.ENDED, Status.CANCELED)
                .stream()
                .findFirst()
                .orElseThrow(() -> new UnprocessableEntityException(
                        "No momento nenhum participante pode ser inscrito, pois não há nenhuma edição agendada ou em andamento."
                ));
    }

    private void filterParticipantsByTeam(List<ParticipantCSVDto> dtos, Edition currentEdition, Set<ParticipantCSVDto> rejectedParticipants) {
        var teamNameToIdMap = currentEdition.getTeamScores()
                .stream()
                .collect(Collectors.toMap(
                        teamScore -> teamScore.getTeam().getName(),
                        teamScore -> teamScore.getTeam().getId()
                ));
        var validDtos = new ArrayList<ParticipantCSVDto>();

        dtos.forEach(dto -> {
            var teamId = teamNameToIdMap.get(dto.getTeamName());
            if (teamId == null) {
                rejectedParticipants.add(this.setParticipantWithProblem(dto, "O campo 'equipe' está vazio ou é inválido"));
                return;
            }
            dto.setTeamId(teamId);
            validDtos.add(dto);
        });
        dtos.clear();
        dtos.addAll(validDtos);
    }

    private void filterParticipantsByCpf(List<ParticipantCSVDto> dtos, Set<ParticipantCSVDto> rejectedParticipants) {
        var validDtos = new ArrayList<ParticipantCSVDto>();

        dtos.forEach(dto -> {
            try {
                participantValidator.validateCpf(dto.getCpf());
                validDtos.add(dto);
            } catch (RuntimeException e) {
                var message = "O campo 'cpf' está vazio, possui um cabeçalho inválido ou não está no formato correto.";
                rejectedParticipants.add(this.setParticipantWithProblem(dto, message));
            }
        });
        dtos.clear();
        dtos.addAll(validDtos);
    }

    private Map<String, Participant> findAndValidateExistingParticipantsByCpf(List<ParticipantCSVDto> dtos, HashMap<String, Long> teamsIdsByCpfMap, Set<ParticipantCSVDto> rejectedParticipants) {
        var cpfs = dtos.stream()
                .map(dto -> {
                    teamsIdsByCpfMap.put(dto.getCpf(), dto.getTeamId());
                    return dto.getCpf();
                })
                .collect(Collectors.toSet());
        var existingParticipants = participantRepository.findByCpfIn(cpfs);
        var existingParticipantsMap = existingParticipants.stream()
                .collect(Collectors.toMap(Participant::getCpf, Function.identity()));

        dtos.forEach(dto -> {
            try {
                dto.setName(dto.getName().toUpperCase(Locale.ROOT));
                var existingParticipant = existingParticipantsMap.get(dto.getCpf());

                if (existingParticipant != null && !existingParticipant.compareTo(dto)) {
                    throw new BadRequestException("O participante portador do CPF %s possui dados diferentes dos informados.".formatted(dto.getCpf()));
                }
            } catch (NullPointerException e) {
                rejectedParticipants.add(this.setParticipantWithProblem(dto, "O campo 'nome' está vazio ou possui um cabeçalho inválido."));
            } catch (BadRequestException e) {
                rejectedParticipants.add(this.setParticipantWithProblem(dto, e.getMessage()));
            }
        });
        return existingParticipantsMap;
    }

    private Participant processParticipantForCreation(ParticipantCSVDto dto, Map<String, Participant> participantsMap, Set<ParticipantCSVDto> rejectedParticipants) {
        if (participantsMap.containsKey(dto.getCpf())) return null;

        try {
            return this.validateAndPrepareParticipant(dto);
        } catch (RuntimeException e) {
            rejectedParticipants.add(this.setParticipantWithProblem(dto, e.getMessage()));
            return null;
        }
    }

    private Participant validateAndPrepareParticipant(ParticipantCSVDto dto) throws BadRequestException, IllegalArgumentException, NullPointerException {
        var message = "O campo '%s' está vazio ou possui um cabeçalho inválido.";
        
        if (dto.getName() == null) throw new BadRequestException(message.formatted("nome"));
        if (dto.getGender() == null) throw new BadRequestException(message.formatted("gênero"));
        if (dto.getType() == null) throw new BadRequestException(message.formatted("tipo"));

        var gender = Gender.findGenderLike(dto.getGender());
        var type = ParticipantType.findTypeLike(dto.getType());

        var participant = participantMapper.toNewParticipant(dto, gender, type);
        participant.setName(participant.getName().toUpperCase(Locale.ROOT));

        return participant;
    }

    private List<EditionRegistration> registerAllParticipantsInEdition(List<Participant> participants, Edition currentEdition, HashMap<String, Long> teamsIdsByCpfMap, Set<ParticipantCSVDto> problematicParticipants) {
        var teams = this.getTeams(currentEdition);
        var registrations = new ArrayList<EditionRegistration>();

        participants.forEach(participant -> {
            var teamId = teamsIdsByCpfMap.get(participant.getCpf());

            try {
                var team = this.getTeamById(teams, teamId);

                participantValidator.validateParticipantAndTeamActive(participant, team);
                participantValidator.validateParticipantForEdition(participant, currentEdition)
                    .ifPresent(r -> {
                        throw new ConflictException("O participante já está inscrito na edição.");
                    });
                registrations.add(registrationMapper.toNewEditionRegistration(participant, currentEdition, team));
            } catch (NoSuchElementException e) {
                var team = this.getTeamById(teams, teamId);
                problematicParticipants.add(participantMapper.toParticipantCSVDto(participant, team.getName(), "Equipe inválida ou inexistente."));
            } catch (ConflictException | UnprocessableEntityException e) {
                var team = this.getTeamById(teams, teamId);
                if (findRejectedParticipantByCpf(participant.getCpf(), problematicParticipants).isEmpty()) {
                    problematicParticipants.add(participantMapper.toParticipantCSVDto(participant, team.getName(), e.getMessage()));
                }
            }
        });
        return editionRegistrationRepository.saveAll(registrations);
    }

    private List<Team> getTeams(Edition edition) {
        return edition.getTeamScores()
                .stream()
                .map(TeamScore::getTeam).toList();
    }

    private Team getTeamById(List<Team> teams, Long id) throws NoSuchElementException {
        return teams.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }

    private Optional<ParticipantCSVDto> findRejectedParticipantByCpf(String cpf, Set<ParticipantCSVDto> problematicParticipants) {
        return problematicParticipants.stream()
                .filter(participants -> participants.getCpf().equals(cpf))
                .findFirst();
    }

    private List<Participant> filterExistingParticipantsByCpf(List<ParticipantCSVDto> dtos, Set<ParticipantCSVDto> rejectedParticipants) {
        var cpfs = dtos.stream()
                .map(ParticipantCSVDto::getCpf)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        var existingCpfMap = participantRepository.findByCpfIn(cpfs)
                .stream()
                .collect(Collectors.toMap(Participant::getCpf, Function.identity()));

        dtos.forEach(dto -> {
            if (!existingCpfMap.containsKey(dto.getCpf())) {
                var message = "Participante portador do CPF %s não foi encontrado.".formatted(dto.getCpf());
                rejectedParticipants.add(this.setParticipantWithProblem(dto, message));
            }
        });
        return existingCpfMap.values().stream().toList();
    }

    private ParticipantCSVDto setParticipantWithProblem(ParticipantCSVDto dto, String message) {
        dto.setTeamId(null);
        dto.setMessage(message);

        if (dto.getName() != null) {
            dto.setName(dto.getName().toUpperCase(Locale.ROOT));
        }
        return dto;
    }

    private Participant processParticipantForInactivation(Participant participant, Set<ParticipantCSVDto> problematicParticipants) {
        if (!participant.getIsActive()) return null;

        try {
            participantValidator.validateParticipantToChangeStatus(participant);
            participant.setIsActive(false);
            return participant;
        } catch (UnprocessableEntityException e) {
            var teamName = this.getCurrentTeamName(participant);
            problematicParticipants.add(
                    this.participantMapper.toParticipantCSVDto(participant, teamName, "O participante não pode ser desativado.")
            );
            return null;
        }
    }

    private String getCurrentTeamName(Participant participant) {
        var registrations = participant.getEditionRegistrations().stream().toList();
        var lastRegistration = registrations.stream()
                .max(Comparator.comparing(EditionRegistration::getId))
                .stream().findFirst();

        return lastRegistration.map(registration -> registration.getTeam().getName())
                .orElse("Sem equipe.");
    }

}
