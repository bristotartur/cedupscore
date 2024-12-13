package com.bristotartur.cedupscore_api.services;

import com.bristotartur.cedupscore_api.domain.*;
import com.bristotartur.cedupscore_api.dtos.request.EventRequestDto;
import com.bristotartur.cedupscore_api.dtos.request.EventScoreRequestDto;
import com.bristotartur.cedupscore_api.dtos.request.SportEventRequestDto;
import com.bristotartur.cedupscore_api.enums.EventType;
import com.bristotartur.cedupscore_api.enums.ExtraType;
import com.bristotartur.cedupscore_api.enums.Status;
import com.bristotartur.cedupscore_api.exceptions.BadRequestException;
import com.bristotartur.cedupscore_api.exceptions.ConflictException;
import com.bristotartur.cedupscore_api.exceptions.UnprocessableEntityException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.bristotartur.cedupscore_api.enums.EventType.SPORT;
import static com.bristotartur.cedupscore_api.enums.EventType.TASK;
import static com.bristotartur.cedupscore_api.enums.ExtraType.CULTURAL;
import static com.bristotartur.cedupscore_api.enums.ExtraType.getTaskTypePlural;
import static com.bristotartur.cedupscore_api.enums.ParticipantType.STUDENT;
import static com.bristotartur.cedupscore_api.enums.RoleType.EVENT_ADMIN;
import static com.bristotartur.cedupscore_api.enums.RoleType.SUPER_ADMIN;
import static com.bristotartur.cedupscore_api.enums.Status.*;

@Component
public class EventValidationService {

    public void checkUser(User user) throws UnprocessableEntityException {
        var role = user.getRole();

        if (!role.equals(EVENT_ADMIN) && !role.equals(SUPER_ADMIN)) {
            throw new UnprocessableEntityException("O usuário fornecido não possui permissão para manipular eventos.");
        }
    }

    public void checkExtraType(EventType type, ExtraType extraType) throws BadRequestException, UnprocessableEntityException {
        switch (extraType) {
            case NORMAL, COMPLETION, CULTURAL -> {
                if (!type.equals(TASK)) {
                    var message = "O tipo %s é inválido para eventos esportivos.";
                    throw new  UnprocessableEntityException(message.formatted(extraType.value.toLowerCase(Locale.ROOT)));
                }
            }
            case BASKETBALL, CHESS, FUTSAL, HANDBALL, TABLE_TENNIS, VOLLEYBALL -> {
                if (!type.equals(SPORT)) {
                    var message = "O tipo %s é inválido para tarefas.";
                    throw new  UnprocessableEntityException(message.formatted(extraType.value.toLowerCase(Locale.ROOT)));
                }
            }
            default -> throw new BadRequestException("O campo 'tipo' não pode ser vazio.");
        }
    }

    public void checkEventForUpdate(EventRequestDto dto, Event event) throws ConflictException {
        this.checkMinAndMaxParticipantsPerTeam(dto, event);

        var isAllowedParticipantTypeEqual = event.getAllowedParticipantType().equals(dto.getAllowedParticipantType());
        var isModalityEqual = event.getModality().equals(dto.getModality());

        if ((!isAllowedParticipantTypeEqual || !isModalityEqual) && !event.getRegistrations().isEmpty()) {
            throw new ConflictException(
                    "A modalidade e/ou tipo de participante permitido do evento não pode ser alterada pois há inscrições relacionadas a ele."
            );
        }
    }

    public void checkSportEvent(SportEventRequestDto dto, Edition edition, Long eventId) throws ConflictException {
        var allowedParticipantType = dto.getAllowedParticipantType();

        if (!allowedParticipantType.equals(STUDENT)) {
            throw new UnprocessableEntityException("Eventos esportivos devem ser permitidos apenas a alunos.");
        }
        var type = dto.getSportType();
        var modality = dto.getModality();

        edition.getEvents().stream()
                .filter(event -> event.getType().equals(SPORT))
                .filter(event -> event.getExtraType().equals(type)
                        && event.getModality().equals(modality))
                .findFirst()
                .ifPresent(event -> {
                    if (event.getId().equals(eventId)) return;

                    var message = "Já existe um evento do tipo %s e modalidade %s na edição atual.";
                    throw new ConflictException(
                            message.formatted(type.name().toLowerCase(Locale.ROOT), modality.value.toLowerCase(Locale.ROOT))
                    );
                });
    }

    public void checkMinAndMaxParticipantsPerTeam(EventRequestDto dto, Event event) throws ConflictException, UnprocessableEntityException {
        this.compareAllowedQuantities(dto, event);
        this.checkMinAndMaxParticipantsPerTeam(dto);

        var teamToRegistrations = event.getRegistrations()
                .stream()
                .collect(Collectors.groupingBy(EventRegistration::getTeam));

        if (teamToRegistrations.isEmpty()) return;

        teamToRegistrations.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > dto.getMaxParticipantsPerTeam())
                .findFirst()
                .ifPresent(entry -> {
                    var team = entry.getKey().getName();
                    var message = "A quantidade de participantes da equipe %s inscritos no evento excede o novo limite estabelecido.";

                    throw new ConflictException(message.formatted(team));
                });
    }

    public void checkMinAndMaxParticipantsPerTeam(EventRequestDto dto) throws UnprocessableEntityException {
        var min = dto.getMinParticipantsPerTeam();
        var max = dto.getMaxParticipantsPerTeam();

        var message = "A quantidade %s de participantes não pode ser negativa.";

        if (min < 0) throw new UnprocessableEntityException(message.formatted("mínima"));
        if (max < 0) throw new UnprocessableEntityException(message.formatted("máxima"));

        if (min > max) {
            throw new UnprocessableEntityException(
                    "A quantidade mínima de participantes por equipe não pode ser maior que a quantidade máxima."
            );
        }
    }

    private void compareAllowedQuantities(EventRequestDto dto, Event event) throws UnprocessableEntityException {
        var originalMin = event.getMinParticipantsPerTeam();
        var originalMax = event.getMaxParticipantsPerTeam();
        var newMin = dto.getMinParticipantsPerTeam();
        var newMax = dto.getMaxParticipantsPerTeam();

        if (originalMin.equals(newMin) && originalMax.equals(newMax)) return;

        if (!event.getStatus().equals(SCHEDULED)) {
            throw new UnprocessableEntityException(
                    "A quantidade de participantes permitida não pode ser alterada pois o evento não está agendado."
            );
        }
    }

    public void checkEventStatusForUpdate(Status eventStatus) throws UnprocessableEntityException {
        if (eventStatus.equals(ENDED) || eventStatus.equals(CANCELED)) {
            throw new UnprocessableEntityException("O evento não pode ser atualizado.");
        }
    }

    public void validateEventToChangeStatus(Event event, Status newStatus, Boolean isForClosing) throws ConflictException, UnprocessableEntityException {
        var edition = event.getEdition();
        this.checkEdition(edition, true);

        Status.checkStatus(event.getStatus(), newStatus);

        if (event.getStatus().equals(SCHEDULED) && newStatus.equals(IN_PROGRESS)) {
            this.checkRegistrationsToStartEvent(event);
        }
        if (!isForClosing && newStatus.equals(ENDED)) {
            var message = "O status de um evento não pode ser diretamente definido como 'encerrado'.";
            throw new UnprocessableEntityException(message);
        }
        if (isForClosing && (newStatus.equals(SCHEDULED) || newStatus.equals(CANCELED))) {
            throw new UnprocessableEntityException("O evento não pode ser encerrado.");
        }
    }

    public void checkEdition(Edition edition, Boolean isForUpdate) throws ConflictException {
        var status = edition.getStatus();
        var message = "Eventos não podem mais ser %s na edição fornecida.";
        var action = (isForUpdate) ? "atualizados" : "adicionados";

        switch (status) {
            case ENDED, CANCELED -> throw new ConflictException(message.formatted(action));
            case OPEN_FOR_EDITS -> {
                if (!isForUpdate) throw new ConflictException(message.formatted(action));
            }
        }
    }

    private void checkRegistrationsToStartEvent(Event event) throws UnprocessableEntityException {
        var min = event.getMinParticipantsPerTeam();
        var teamsCount = event.getScores().size();
        var teamToRegistrations = event.getRegistrations()
                .stream()
                .collect(Collectors.groupingBy(EventRegistration::getTeam));

        if (teamToRegistrations.isEmpty() || teamToRegistrations.size() < teamsCount) {
            var message = "É necessário que todas as equipes possuam participantes inscritos para que o evento possa ser iniciado.";
            throw new UnprocessableEntityException(message);
        }
        teamToRegistrations.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() < min)
                .findFirst()
                .ifPresent(entry -> {
                    var team = entry.getKey().getName();
                    var message = "O evento não pode ser iniciado pois a equipe %s possui uma quantidade de participantes inscritos inferior ao necessário.";

                    throw new UnprocessableEntityException(message.formatted(team));
                });
    }
    
    public void validateEventToClose(Event event) throws UnprocessableEntityException {
        var status = event.getStatus();

    	if (status.equals(ENDED) || status.equals(CANCELED) || status.equals(OPEN_FOR_EDITS)) {
            throw new UnprocessableEntityException("O evento já foi encerrado.");
        }
    }

    public Map<Long, Integer> validateEventScoresAndReturnScoresMap(Event event, Set<EventScoreRequestDto> scoresDtos) throws ConflictException, UnprocessableEntityException {
        var idToScoreMap = scoresDtos.stream()
                .collect(Collectors.toMap(
                        EventScoreRequestDto::id,
                        EventScoreRequestDto::score
                ));
        var teamToNewScoreMap = event.getScores()
                .stream()
                .filter(eventScore -> idToScoreMap.containsKey(eventScore.getId()))
                .collect(Collectors.toMap(
                        EventScore::getTeam,
                        eventScore -> idToScoreMap.get(eventScore.getId())
                ));

        if (teamToNewScoreMap.size() != scoresDtos.size()) {
            throw new UnprocessableEntityException("Todas as equipes precisam ter sua pontuação definida.");
        }
        this.checkScores(event, teamToNewScoreMap);
        return idToScoreMap;
    }

    private void checkScores(Event event, Map<Team, Integer> teamToNewScoreMap) throws ConflictException, UnprocessableEntityException {
        var type = event.getType();
        var extraType = event.getExtraType();
        var scoresUpTo50 = List.of(50, 40, 30, 20, 10);
        var scoresUpTo100 = List.of(100, 90, 80, 70, 50);
        var possibleScores = new ArrayList<Integer>();

        teamToNewScoreMap.values()
                .stream()
                .filter(score -> {
                    if (type.equals(SPORT) || extraType.equals(CULTURAL)) {
                        possibleScores.addAll(scoresUpTo100);
                        return !scoresUpTo100.contains(score);
                    }
                    possibleScores.addAll(scoresUpTo50);
                    return !scoresUpTo50.contains(score);
                })
                .findFirst()
                .ifPresent(score -> {
                    var message = "%d é uma pontuação inválida para %s.";
                    var complement = (type.equals(SPORT))
                            ? "eventos esportivos"
                            : "tarefas %s".formatted(getTaskTypePlural(extraType));

                    throw new UnprocessableEntityException(message.formatted(score, complement));
                });
        this.checkTiedScores(type, possibleScores, teamToNewScoreMap);
    }

    private void checkTiedScores(EventType type, List<Integer> possibleScores, Map<Team, Integer> teamToNewScoreMap) throws ConflictException {
        var sortedTeams = teamToNewScoreMap.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .toList();
        var currentScoreIndex = 0;

        for (int i = 0; i < sortedTeams.size(); i++) {
            var currentScore = sortedTeams.get(i).getValue();
            var isNextTeamTied = i < sortedTeams.size() - 1 && currentScore.equals(sortedTeams.get(i + 1).getValue());

            if (type.equals(SPORT) && isNextTeamTied) {
                throw new ConflictException("Equipes não podem empatar em eventos esportivos.");
            }
            if (!possibleScores.get(currentScoreIndex).equals(currentScore)) {
                var message = "A equipe %s possui uma pontuação inválida.";
                throw new ConflictException(message.formatted(sortedTeams.get(i).getKey().getName()));
            }
            currentScoreIndex++;

            if (!isNextTeamTied) continue;
            do {
                i++;
                currentScoreIndex++;
            } while (i < sortedTeams.size() - 1 && currentScore.equals(sortedTeams.get(i + 1).getValue()));
        }
    }

}
