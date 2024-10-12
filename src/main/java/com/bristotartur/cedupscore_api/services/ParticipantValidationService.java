package com.bristotartur.cedupscore_api.services;

import com.bristotartur.cedupscore_api.domain.*;
import com.bristotartur.cedupscore_api.enums.Modality;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import com.bristotartur.cedupscore_api.enums.Patterns;
import com.bristotartur.cedupscore_api.enums.Status;
import com.bristotartur.cedupscore_api.exceptions.BadRequestException;
import com.bristotartur.cedupscore_api.exceptions.ConflictException;
import com.bristotartur.cedupscore_api.exceptions.UnprocessableEntityException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ParticipantValidationService {

    private static final String INVALID_CPF_MSG = "CPF inválido.";
    private static final String PARTICIPANT_INACTIVE_MSG = "O participante está inativo.";
    private static final String TEAM_INACTIVE_MSG = "A equipe está inativa.";
    private static final String EDITION_REGISTRATION_NOT_ALLOWED_MSG = "O participante não pode ser inscrito na edição informada.";
    private static final String STUDENT_REGISTRATION_NOT_ALLOWED_MSG = "Alunos só podem ser inscritos em edições agendadas.";
    private static final String NOT_REGISTERED_IN_EDITION_MSG = "O participante não está inscrito na edição do evento.";
    private static final String TEAM_MISMATCH_MSG = "O participante não está inscrito na equipe informada.";
    private static final String EVENT_REGISTRATION_NOT_ALLOWED_MSG = "O participante não pode ser inscrito no evento informado.";
    private static final String ALREADY_REGISTERED_IN_EVENT_MSG = "O participante já está inscrito no evento informado.";
    private static final String EDITION_MISMATCH_MSG = "O participante não está inscrito na edição informada.";
    private static final String EVENT_MISMATCH_MSG = "O participante não está inscrito no evento informado.";
    private static final String CANNOT_REMOVE_REGISTRATION = "O participante não pode ser desinscrito.";

    public void validateCpf(String cpf) throws BadRequestException {
        if (!Patterns.validateCpf(cpf)) throw new BadRequestException(INVALID_CPF_MSG);
    }

    public void validateParticipantAndTeamActive(Participant participant, Team team) throws UnprocessableEntityException {
        if (!participant.getIsActive()) {
            throw new UnprocessableEntityException(PARTICIPANT_INACTIVE_MSG);
        }
        if (!team.getIsActive()) {
            throw new UnprocessableEntityException(TEAM_INACTIVE_MSG);
        }
    }

    public Optional<EditionRegistration> validateParticipantForEdition(Participant participant, Edition edition) throws UnprocessableEntityException {
        var type = participant.getType();
        var status = edition.getStatus();

        if (status.equals(Status.ENDED) || status.equals(Status.CANCELED)) {
            throw new UnprocessableEntityException(EDITION_REGISTRATION_NOT_ALLOWED_MSG);
        }
        if (type.equals(ParticipantType.STUDENT) && !status.equals(Status.SCHEDULED)) {
            throw new UnprocessableEntityException(STUDENT_REGISTRATION_NOT_ALLOWED_MSG);
        }
        return participant.getEditionRegistrations()
                .stream()
                .filter(registration -> registration.getEdition().equals(edition))
                .findFirst();
    }

    public void validateParticipantTeamForEvent(Participant participant, Team team, Event event) throws ConflictException, UnprocessableEntityException {
        var eventEdition = event.getEdition();

        var registrationInEdition = participant.getEditionRegistrations()
                .stream()
                .filter(registration -> registration.getEdition().equals(eventEdition))
                .findFirst()
                .orElseThrow(() -> new ConflictException(NOT_REGISTERED_IN_EDITION_MSG));

        if (!registrationInEdition.getTeam().equals(team)) {
            throw new UnprocessableEntityException(TEAM_MISMATCH_MSG);
        }
    }

    public void validateParticipantForEvent(Participant participant, Event event, Integer registeredParticipants) throws ConflictException, UnprocessableEntityException {
        var max = event.getMaxParticipantsPerTeam();

        if (!event.getStatus().equals(Status.SCHEDULED) ||
            !(registeredParticipants <= max) ||
            !Modality.compareCategory(event.getModality(), participant.getGender()) ||
            !ParticipantType.compareTypes(event.getAllowedParticipantType(), participant.getType())
        ) {
            throw new UnprocessableEntityException(EVENT_REGISTRATION_NOT_ALLOWED_MSG);
        }
        participant.getEventRegistrations()
                .stream()
                .filter(registration -> registration.getEvent().equals(event))
                .findFirst()
                .ifPresent(e -> {
                    throw new ConflictException(ALREADY_REGISTERED_IN_EVENT_MSG);
                });
    }

    public void validateEditionRegistrationToRemove(Participant participant, EditionRegistration registration) throws ConflictException, UnprocessableEntityException {
        participant.getEditionRegistrations()
                .stream()
                .filter(r -> r.equals(registration))
                .findFirst()
                .orElseThrow(() -> new ConflictException(EDITION_MISMATCH_MSG));

        var edition = registration.getEdition();
        var status = edition.getStatus();

        if (status.equals(Status.ENDED) || status.equals(Status.CANCELED)) {
            throw new UnprocessableEntityException(CANNOT_REMOVE_REGISTRATION);
        }
        participant.getEventRegistrations()
                .stream()
                .filter(r -> r.getEvent().getEdition().equals(edition))
                .findFirst()
                .ifPresent(r -> {
                    throw new UnprocessableEntityException(CANNOT_REMOVE_REGISTRATION);
                });
    }

    public void validateEventRegistrationToRemove(Participant participant, EventRegistration registration) throws ConflictException, UnprocessableEntityException {
        participant.getEventRegistrations()
                .stream()
                .filter(r -> r.equals(registration))
                .findFirst()
                .orElseThrow(() -> new ConflictException(EVENT_MISMATCH_MSG));

        var status = registration.getEvent().getStatus();

        if (!status.equals(Status.SCHEDULED)) throw new UnprocessableEntityException(CANNOT_REMOVE_REGISTRATION);
    }

    public void validateParticipantToChangeStatus(Participant participant) throws UnprocessableEntityException {
        participant.getEventRegistrations()
                .stream()
                .map(registration -> registration.getEvent().getStatus())
                .filter(status -> status.equals(Status.IN_PROGRESS) || status.equals(Status.STOPPED))
                .findFirst()
                .ifPresent(status -> {
                    throw new UnprocessableEntityException("O participante não pode ser desativado.");
                });
    }

}
