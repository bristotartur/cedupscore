package com.bristotartur.cedupscore_api.services;

import com.bristotartur.cedupscore_api.domain.people.Participant;
import com.bristotartur.cedupscore_api.dtos.request.ParticipantRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.EditionRegistrationResponseDto;
import com.bristotartur.cedupscore_api.dtos.response.ParticipantResponseDto;
import com.bristotartur.cedupscore_api.enums.Status;
import com.bristotartur.cedupscore_api.exceptions.ConflictException;
import com.bristotartur.cedupscore_api.exceptions.NotFoundException;
import com.bristotartur.cedupscore_api.exceptions.UnprocessableEntityException;
import com.bristotartur.cedupscore_api.mappers.ParticipantMapper;
import com.bristotartur.cedupscore_api.mappers.RegistrationMapper;
import com.bristotartur.cedupscore_api.repositories.people.ParticipantRepository;
import com.bristotartur.cedupscore_api.repositories.registrations.EditionRegistrationRepository;
import com.bristotartur.cedupscore_api.repositories.registrations.EventRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

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

    public Page<Participant> findAllParticipants(Pageable pageable) {
        return participantRepository.findAll(pageable);
    }

    public Page<Participant> findParticipantsByName(String name, Pageable pageable) {
        return participantRepository.findByNameLike(name.toUpperCase(Locale.ROOT), pageable);
    }

    public Page<Participant> findParticipantsFromEdition(Long editionId, Pageable pageable) {
        var edition = editionService.findEditionById(editionId);
        return participantRepository.findByEdition(edition, pageable);
    }

    public Page<Participant> findParticipantsFromTeam(Long teamId, Long editionId, Pageable pageable) {
        var team = teamService.findTeamById(teamId);
        var edition = editionService.findEditionById(editionId);

        return participantRepository.findByTeamAndEdition(team, edition, pageable);
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
        participantValidator.validateCpf(dto.cpf());

        var participant = participantMapper.toNewParticipant(dto);
        participant.setName(participant.getName().toUpperCase(Locale.ROOT));

        return participantRepository.save(participant);
    }

    public Participant registerParticipantInEdition(Long id, Long editionId, Long teamId) {
        var participant = this.findParticipantById(id);
        var edition = editionService.findEditionById(editionId);
        var team = teamService.findTeamById(teamId);

        participantValidator.validateParticipantAndTeamActive(participant, team);
        participantValidator.validateParticipantForEdition(participant, edition);

        var registration = editionRegistrationRepository
                .save(registrationMapper.toNewEditionRegistration(participant, edition, team));

        participant.getEditionRegistrations().add(registration);
        return participant;
    }

    public Participant registerParticipantInEvent(Long id, Long eventId, Long teamId) {
        var participant = this.findParticipantById(id);
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

        participantValidator.validateCpf(dto.cpf());

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