package com.bristotartur.cedupscore_api.services;

import com.bristotartur.cedupscore_api.domain.Edition;
import com.bristotartur.cedupscore_api.domain.EditionRegistration;
import com.bristotartur.cedupscore_api.domain.Participant;
import com.bristotartur.cedupscore_api.dtos.request.ParticipantRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.ParticipantCSVUploadResponseDto;
import com.bristotartur.cedupscore_api.dtos.response.ParticipantResponseDto;
import com.bristotartur.cedupscore_api.dtos.response.RejectedParticipantResponseDto;
import com.bristotartur.cedupscore_api.enums.Gender;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import com.bristotartur.cedupscore_api.enums.Status;
import com.bristotartur.cedupscore_api.exceptions.*;
import com.bristotartur.cedupscore_api.mappers.ParticipantMapper;
import com.bristotartur.cedupscore_api.mappers.RegistrationMapper;
import com.bristotartur.cedupscore_api.repositories.EditionRegistrationRepository;
import com.bristotartur.cedupscore_api.repositories.EventRegistrationRepository;
import com.bristotartur.cedupscore_api.repositories.ParticipantRepository;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.bristotartur.cedupscore_api.repositories.ParticipantSpecifications.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final EditionRegistrationRepository editionRegistrationRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final ParticipantMapper participantMapper;
    private final RegistrationMapper registrationMapper;
    private final TeamService teamService;
    private final EditionService editionService;
    private final EventService eventService;
    private final ParticipantValidationService participantValidator;

    public Page<Participant> findAllParticipants(String name, Long editionId, Long teamId, Gender gender, ParticipantType type, String status, String order, Pageable pageable) {
        var sort = switch (order != null ? order : "") {
            case "a-z" -> Sort.by("name").ascending();
            case "z-a" -> Sort.by("name").descending();
            default -> Sort.by("id").descending();
        };
        var spec = Specification.where(hasName(name)
                .and(fromEdition(editionId))
                .and(fromTeam(teamId, editionId))
                .and(hasGender(gender))
                .and(hasType(type))
                .and(hasStatus(status))
        );
        return participantRepository.findAll(spec, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort));
    }

    public Page<Participant> findParticipantsFromEvent(Long eventId, Pageable pageable) {
        var event = eventService.findEventById(eventId);
        return participantRepository.findByEvent(event, pageable);
    }

    public Page<Participant> findParticipantsFromEventByTeam(Long teamId, Long eventId, Pageable pageable) {
        var team = teamService.findTeamById(teamId);
        var event = eventService.findEventById(eventId);

        return participantRepository.findByTeamAndEvent(team, event, pageable);
    }

    public Participant findParticipantById(Long id) {

        return participantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Participante não encontrado."));
    }

    public Participant findParticipantByCpf(String cpf) {
        participantValidator.validateCpf(cpf);
        return participantRepository.findByCpf(cpf)
                .orElseThrow(() -> new NotFoundException("Participante não encontrado."));
    }

    public ParticipantResponseDto createParticipantResponseDto(Participant participant) {
        var registrations = participant.getEditionRegistrations()
                .stream()
                .map(registration -> {
                    var teamDto = teamService.createTeamResponseDto(registration.getTeam());
                    return registrationMapper.toEditionRegistrationResponseDto(registration, teamDto);
                }).toList();
        return participantMapper.toParticipantResponseDto(participant, registrations);
    }

    public Participant saveParticipant(ParticipantRequestDto dto) {
        var participant = validateAndPrepareParticipant(dto);
        var currentEdition = this.getCurrentEdition();

        var savedParticipant = participantRepository.save(participant);
        return this.registerParticipantInEdition(savedParticipant, currentEdition.getId(), dto.getTeamId());
    }

    public ParticipantCSVUploadResponseDto uploadParticipantsCSVFile(MultipartFile file) {
        var dtos = parseCSV(file);
        var currentEdition = this.getCurrentEdition();
        var rejectedParticipants = new HashSet<RejectedParticipantResponseDto>();
        var teamsIds = new ArrayList<Long>();
        var existingParticipantsMap = this.getExistingParticipantsByCpf(dtos.stream().toList(), teamsIds, rejectedParticipants);

        var savedParticipants = participantRepository.saveAll(
                dtos.stream()
                        .map(dto -> {
                            if (existingParticipantsMap.containsKey(dto.getCpf())) return null;
                            try {
                                var participant = this.validateAndPrepareParticipant(dto);
                                teamsIds.add(dto.getTeamId());
                                return participant;
                            } catch (BadRequestException e) {
                                var participant = participantMapper.toNewParticipant(dto);
                                rejectedParticipants.add(participantMapper.toRejectedParticipantResponseDto(participant, dto.getTeamId(), e.getMessage()));
                                return null;
                            }
                        })
                        .filter(Objects::nonNull).toList()
        );
        var added = savedParticipants.size();
        savedParticipants.addAll(existingParticipantsMap.values());
        var rejected = rejectedParticipants.size();
        var registeredParticipants = this.registerAllParticipantsInEdition(savedParticipants, currentEdition, teamsIds, rejectedParticipants);
        var problems = rejectedParticipants.size();
        var notRegistered = problems - rejected;

        return participantMapper.toParticipantCSVUploadResponseDto(
                dtos.size(), added, existingParticipantsMap.size(), registeredParticipants.size(), problems, rejected, notRegistered, new ArrayList<>(rejectedParticipants)
        );
    }

    private Participant validateAndPrepareParticipant(ParticipantRequestDto dto) {
        participantValidator.validateCpf(dto.getCpf());

        var participant = participantMapper.toNewParticipant(dto);
        participant.setName(participant.getName().toUpperCase(Locale.ROOT));

        return participant;
    }

    private Edition getCurrentEdition() {

        return editionService.findEditionByStatus(Status.SCHEDULED)
                .stream()
                .findFirst()
                .orElseThrow(() -> new UnprocessableEntityException(
                        "No momento nenhum participante pode ser inscrito, pois não há nenhuma edição agendada."
                ));
    }

    private Set<ParticipantRequestDto> parseCSV(MultipartFile file) {
        try (
                final var reader = new BufferedReader(new InputStreamReader((file.getInputStream())))
        ) {
            var strategy = new HeaderColumnNameMappingStrategy<ParticipantRequestDto>();
            strategy.setType(ParticipantRequestDto.class);

            var csvToBean = new CsvToBeanBuilder<ParticipantRequestDto>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return new HashSet<>(csvToBean.parse());
        } catch (IOException e) {
            throw new InternalServerErrorException(e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new UnprocessableEntityException(e.getMessage(), e);
        }
    }

    private Map<String, Participant> getExistingParticipantsByCpf(List<ParticipantRequestDto> dtos, List<Long> teamsIds, Set<RejectedParticipantResponseDto> rejected) {
        var cpfs = dtos.stream()
                .map(dto -> {
                    teamsIds.add(dto.getTeamId());
                    return dto.getCpf();
                })
                .collect(Collectors.toSet());
        var existingParticipants = participantRepository.findByCpfIn(cpfs);
        var existingParticipantsMap = existingParticipants.stream()
                .collect(Collectors.toMap(Participant::getCpf, Function.identity()));

        for (var dto : dtos) {
            var participant = existingParticipantsMap.get(dto.getCpf());
            dto.setName(dto.getName().toUpperCase(Locale.ROOT));

            if (participant != null && !participant.compareTo(dto)) {
                var message = "O participante portador do CPF %s possui dados diferentes dos informados.".formatted(dto.getCpf());
                var rejectedParticipant = participantMapper.toRejectedParticipantResponseDto(
                        participantMapper.toNewParticipant(dto), dto.getTeamId(), message);

                rejected.add(rejectedParticipant);
            }
        }
        return existingParticipantsMap;
    }

    private List<EditionRegistration> registerAllParticipantsInEdition(List<Participant> participants, Edition edition, List<Long> teamsIds, Set<RejectedParticipantResponseDto> rejected) {
        var teams = teamService.findAllTeams();
        var registrations = new ArrayList<EditionRegistration>();

        for (int i = 0; i < participants.size(); i++) {
            var participant = participants.get(i);
            var teamId = teamsIds.get(i);
            var teamExists = teams.stream()
                    .anyMatch(team -> team.getId().equals(teamId));

            if (!teamExists) {
                rejected.add(participantMapper.toRejectedParticipantResponseDto(participant, teamId, "Equipe inválida ou inexistente."));
                continue;
            }
            try {
                var team = teams.stream()
                        .filter(t -> t.getId().equals(teamId))
                        .findFirst()
                        .orElseThrow();

                participantValidator.validateParticipantAndTeamActive(participant, team);
                participantValidator.validateParticipantForEdition(participant, edition);

                registrations.add(registrationMapper.toNewEditionRegistration(participant, edition, team));
            } catch (ConflictException | UnprocessableEntityException e) {
                if (findRejectedParticipantByCpf(participant.getCpf(), rejected).isEmpty()) {
                    rejected.add(participantMapper.toRejectedParticipantResponseDto(participant, teamId, e.getMessage()));
                }
            } catch (RuntimeException e) {
                rejected.add(participantMapper.toRejectedParticipantResponseDto(participant, teamId, "Equipe inválida ou inexistente."));
            }
        }
        return editionRegistrationRepository.saveAll(registrations);
    }

    private Optional<RejectedParticipantResponseDto> findRejectedParticipantByCpf(String cpf, Set<RejectedParticipantResponseDto> rejected) {
        return rejected.stream()
                .filter(p -> p.cpf().equals(cpf))
                .findFirst();
    }

    public Participant registerParticipantInEdition(Participant participant, Long editionId, Long teamId) {
        var edition = editionService.findEditionById(editionId);
        var team = teamService.findTeamById(teamId);

        participantValidator.validateParticipantAndTeamActive(participant, team);
        participantValidator.validateParticipantForEdition(participant, edition);

        var registration = editionRegistrationRepository
                .save(registrationMapper.toNewEditionRegistration(participant, edition, team));

        participant.getEditionRegistrations().add(registration);
        return participant;
    }

    public Participant registerParticipantInEvent(Participant participant, Long eventId, Long teamId) {
        var event = eventService.findEventById(eventId);
        var team = teamService.findTeamById(teamId);

        participantValidator.validateParticipantAndTeamActive(participant, team);
        participantValidator.validateParticipantTeamForEvent(participant, team, event);

        var registeredParticipants = participantRepository.findByTeamAndEvent(team, event).size();
        participantValidator.validateParticipantForEvent(participant, event, registeredParticipants);

        var registration = eventRegistrationRepository
                .save(registrationMapper.toNewEventRegistration(participant, event, team));

        participant.getEventRegistrations().add(registration);
        return participant;
    }

    public void deleteParticipant(Long id) {
        var participant = this.findParticipantById(id);

        if (!participant.getEditionRegistrations().isEmpty()) {
            throw new UnprocessableEntityException("O participante não pode ser removido.");
        }
        participantRepository.delete(participant);
    }

    public void deleteEditionRegistration(Long id, Long registrationId) {
        var participant = this.findParticipantById(id);
        var registration = editionRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new NotFoundException("Inscrição não encontrada."));

        participantValidator.validateEditionRegistrationToRemove(participant, registration);

        participant.getEditionRegistrations().remove(registration);
        editionRegistrationRepository.delete(registration);
    }

    public void deleteEventRegistration(Long id, Long registrationId) {
        var participant = this.findParticipantById(id);
        var registration = eventRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new NotFoundException("Inscrição não encontrada."));

        participantValidator.validateEventRegistrationToRemove(participant, registration);

        participant.getEventRegistrations().remove(registration);
        eventRegistrationRepository.delete(registration);
    }

    public Participant replaceParticipant(Long id, ParticipantRequestDto dto) {
        var participant = this.findParticipantById(id);
        var isActive = participant.getIsActive();

        participantValidator.validateCpf(dto.getCpf());

        var newParticipant = participantMapper.toExistingParticipant(id, dto, isActive);
        newParticipant.setName(newParticipant.getName().toUpperCase(Locale.ROOT));

        return participantRepository.save(newParticipant);
    }

    public Participant setParticipantActive(Long id, Boolean isActive) {
        var participant = this.findParticipantById(id);

        if (isActive == participant.getIsActive()) return participant;

        participant.getEventRegistrations()
                .stream()
                .map(registration -> registration.getEvent().getStatus())
                .filter(status -> status.equals(Status.IN_PROGRESS) || status.equals(Status.STOPPED))
                .findFirst()
                .ifPresent(status -> {
                    throw new UnprocessableEntityException("O participante não pode ser desativado.");
                });
        participant.setIsActive(isActive);
        return participantRepository.save(participant);
    }

}
