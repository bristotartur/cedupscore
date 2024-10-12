package com.bristotartur.cedupscore_api.services;

import com.bristotartur.cedupscore_api.domain.Participant;
import com.bristotartur.cedupscore_api.dtos.request.ParticipantRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.ParticipantResponseDto;
import com.bristotartur.cedupscore_api.enums.Gender;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import com.bristotartur.cedupscore_api.enums.Status;
import com.bristotartur.cedupscore_api.exceptions.NotFoundException;
import com.bristotartur.cedupscore_api.exceptions.UnprocessableEntityException;
import com.bristotartur.cedupscore_api.mappers.ParticipantMapper;
import com.bristotartur.cedupscore_api.mappers.RegistrationMapper;
import com.bristotartur.cedupscore_api.repositories.EditionRegistrationRepository;
import com.bristotartur.cedupscore_api.repositories.EventRegistrationRepository;
import com.bristotartur.cedupscore_api.repositories.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

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

    public ParticipantResponseDto createParticipantResponseDto(Participant participant, Boolean hasCpf) {
        var registrations = participant.getEditionRegistrations()
                .stream()
                .map(registration -> {
                    var teamDto = teamService.createTeamResponseDto(registration.getTeam());
                    return registrationMapper.toEditionRegistrationResponseDto(registration, teamDto);
                }).toList();
        return participantMapper.toParticipantResponseDto(participant, registrations, hasCpf);
    }

    public Participant saveParticipant(ParticipantRequestDto dto) {
        participantValidator.validateCpf(dto.cpf());

        participantRepository.findByCpf(dto.cpf()).stream()
                .findFirst()
                .ifPresent(p -> {
                    throw new UnprocessableEntityException("O CPF fornecido já está em uso.");
                });
        var participant = participantMapper.toNewParticipant(dto);
        participant.setName(participant.getName().toUpperCase(Locale.ROOT));

        var currentEdition = editionService.findByStatusDifferentThen(Status.ENDED, Status.CANCELED)
                .stream()
                .findFirst()
                .orElseThrow(() -> new UnprocessableEntityException(
                        "No momento nenhum participante pode ser inscrito, pois não há nenhuma edição agendada."
                ));
        var savedParticipant = participantRepository.save(participant);
        return this.registerParticipantInEdition(savedParticipant, currentEdition.getId(), dto.teamId());
    }

    public Participant registerParticipantInEdition(Participant participant, Long editionId, Long teamId) {
        var edition = editionService.findEditionById(editionId);
        var team = teamService.findTeamById(teamId);

        participantValidator.validateParticipantAndTeamActive(participant, team);
        var registrationOptional = participantValidator.validateParticipantForEdition(participant, edition);

        if (registrationOptional.isPresent()) {
            var existingRegistration = registrationOptional.get();

            participant.getEditionRegistrations().remove(existingRegistration);
            editionRegistrationRepository.delete(existingRegistration);
        }
        var registration = editionRegistrationRepository.save(
                registrationMapper.toNewEditionRegistration(participant, edition, team)
        );
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
        var registrations = participant.getEditionRegistrations();

        if (registrations.size() >= 2) {
            throw new UnprocessableEntityException("O participante não pode ser removido.");
        }
        var edition = registrations.iterator().next().getEdition();

        if (!edition.getStatus().equals(Status.SCHEDULED)) {
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

        participantValidator.validateCpf(dto.cpf());
        participantRepository.findByCpf(dto.cpf()).stream()
                .findFirst()
                .ifPresent(p -> {
                    if (!p.equals(participant)) {
                        throw new UnprocessableEntityException("O CPF fornecido já está em uso.");
                    }
                });
        var newParticipant = participantMapper.toExistingParticipant(id, dto, isActive);
        newParticipant.setName(newParticipant.getName().toUpperCase(Locale.ROOT));

        return participantRepository.save(newParticipant);
    }

    public Participant setParticipantStatus(Long id, Boolean status) {
        var participant = this.findParticipantById(id);

        if (status == participant.getIsActive()) return participant;

        this.participantValidator.validateParticipantToChangeStatus(participant);
        participant.setIsActive(status);

        return participantRepository.save(participant);
    }

}
