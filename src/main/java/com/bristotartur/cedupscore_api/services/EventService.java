package com.bristotartur.cedupscore_api.services;

import com.bristotartur.cedupscore_api.domain.Event;
import com.bristotartur.cedupscore_api.dtos.request.EventRequestDto;
import com.bristotartur.cedupscore_api.dtos.request.EventScoreRequestDto;
import com.bristotartur.cedupscore_api.dtos.request.SportEventRequestDto;
import com.bristotartur.cedupscore_api.dtos.request.TaskEventRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.EventResponseDto;
import com.bristotartur.cedupscore_api.enums.EventType;
import com.bristotartur.cedupscore_api.enums.ExtraType;
import com.bristotartur.cedupscore_api.enums.Modality;
import com.bristotartur.cedupscore_api.enums.Status;
import com.bristotartur.cedupscore_api.exceptions.BadRequestException;
import com.bristotartur.cedupscore_api.exceptions.ConflictException;
import com.bristotartur.cedupscore_api.exceptions.NotFoundException;
import com.bristotartur.cedupscore_api.exceptions.UnprocessableEntityException;
import com.bristotartur.cedupscore_api.mappers.EventMapper;
import com.bristotartur.cedupscore_api.mappers.ScoreMapper;
import com.bristotartur.cedupscore_api.repositories.EventRepository;
import com.bristotartur.cedupscore_api.repositories.EventScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bristotartur.cedupscore_api.repositories.EventSpecifications.*;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final EventValidationService eventValidator;
    private final EventMapper eventMapper;
    private final ScoreMapper scoreMapper;
    private final EditionService editionService;
    private final UserService userService;
    private final TeamService teamService;
    private final EventScoreRepository eventScoreRepository;

    public Page<Event> findAllEvents(String type, Long editionId, Long userId, Pageable pageable) {
        var eventType = (type != null) 
                ? EventType.findEventTypeLike(type)
                : null;

        var spec = Specification.where(hasType(eventType)
                .and(fromEdition(editionId))
                .and(fromUser(userId))
        );
        return eventRepository.findAll(spec, PageRequest.of(
                pageable.getPageNumber(), pageable.getPageSize(), Sort.by("startedAt").descending()
        ));
    }

    public Event findEventById(Long id) throws NotFoundException {

        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado."));
    }

    public Event findEventById(Long id, EventType type) throws NotFoundException {

        return eventRepository.findEventByIdAndType(id, type)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado."));
    }

    public EventResponseDto createEventResponseDto(Event event) {
        var eventScores = event.getScores()
                .stream()
                .map(score -> {
                    var teamDto = teamService.createTeamResponseDto(score.getTeam());
                    return scoreMapper.toEventScoreResponseDto(score, teamDto);
                }).toList();

        return switch (event.getType()) {
            case TASK -> eventMapper.toTaskEventResponseDto(event, eventScores);
            case SPORT -> eventMapper.toSportEventResponseDto(event, eventScores);
        };
    }

    public Event saveEvent(EventRequestDto dto) throws BadRequestException, NotFoundException, ConflictException, UnprocessableEntityException {
        var edition = editionService.findEditionById(dto.getEditionId());
        var user = userService.findUserById(dto.getResponsibleUserId());

        eventValidator.checkEdition(edition, false);
        eventValidator.checkUser(user);
        eventValidator.checkMinAndMaxParticipantsPerTeam(dto);

        var event = switch (dto) {
            case TaskEventRequestDto taskDto -> {
                eventValidator.checkExtraType(EventType.TASK, taskDto.getTaskType());
                yield eventMapper.toNewTaskEvent(taskDto, edition, user);
            }
            case SportEventRequestDto sportDto -> {
                eventValidator.checkExtraType(EventType.SPORT, sportDto.getSportType());
                eventValidator.checkSportEvent(sportDto, edition, null);

                var name = this.createSportEventName(sportDto.getSportType(), sportDto.getModality());
                yield eventMapper.toNewSportEvent(sportDto, name, edition, user);
            }
            default -> throw new BadRequestException("DTO inválido para geração de eventos.");
        };
        this.generateNewEventScores(event);
        return eventRepository.save(event);
    }

    private void generateNewEventScores(Event event) {
        var teams = teamService.findAllActiveTeams();

        teams.forEach(team -> {
            var score = scoreMapper.toNewEventScore(event, team);

            eventScoreRepository.save(score);
            event.getScores().add(score);
        });
    }

    public void deleteEvent(Long id) throws NotFoundException, UnprocessableEntityException {
        var event = this.findEventById(id);

        if (!event.getStatus().equals(Status.SCHEDULED)) {
            throw new UnprocessableEntityException("Eventos só podem ser removidos quando estão agendados.");
        }
        eventRepository.delete(event);
    }

    public Event replaceEvent(Long id, EventRequestDto dto) throws BadRequestException, NotFoundException, ConflictException, UnprocessableEntityException {
        var event = this.findEventById(id);
        var edition = editionService.findEditionById(dto.getEditionId());
        var user = userService.findUserById(dto.getResponsibleUserId());

        eventValidator.checkEventStatusForUpdate(event.getStatus());
        eventValidator.checkEdition(edition, true);
        eventValidator.checkUser(user);
        eventValidator.checkEventForUpdate(dto, event);

        return switch (dto) {
            case TaskEventRequestDto taskDto -> {
                eventValidator.checkExtraType(EventType.TASK, taskDto.getTaskType());
                yield eventRepository.save(eventMapper.toExistingTaskEvent(taskDto, event, edition, user));
            }
            case SportEventRequestDto sportDto -> {
                eventValidator.checkExtraType(EventType.SPORT, sportDto.getSportType());

                var name = this.createSportEventName(sportDto.getSportType(), sportDto.getModality());
                yield eventRepository.save(eventMapper.toExistingSportEvent(sportDto, event, name, edition, user));
            }
            default -> throw new BadRequestException("DTO inválido para atualização de eventos.");
        };
    }

    private String createSportEventName(ExtraType sportType, Modality modality) {
        var type = sportType.value;
        return "%s %s".formatted(type, modality.value.toLowerCase(Locale.ROOT));
    }

    public Event updateEventStatus(Long id, Status status) throws NotFoundException, ConflictException {
        var event = this.findEventById(id);

        if (event.getStatus().equals(status)) return event;

        eventValidator.validateEventToChangeStatus(event, status, false);

        event.setStatus(status);
        event.setStartedAt(LocalDateTime.now());
        event.setEndedAt(LocalDateTime.now());
        
        return eventRepository.save(event);
    }

    public Event closeEvent(Long id, Set<EventScoreRequestDto> scoresDtos) throws BadRequestException, NotFoundException, ConflictException, UnprocessableEntityException {
        var event = this.findEventById(id);
        var status = Status.ENDED;
        
        if (scoresDtos.isEmpty()) throw new BadRequestException("As pontuações das equipes devem ser enviadas.");
        
        eventValidator.validateEventToClose(event);
        eventValidator.validateEventToChangeStatus(event, status, true);
        
        var idToScoreMap = eventValidator.validateEventScoresAndReturnScoresMap(event, scoresDtos);

	    event.setStatus(status);
	    event.setEndedAt(LocalDateTime.now());
        this.updateScores(event, idToScoreMap);
        
        return eventRepository.save(event);
    }

    private void updateScores(Event event, Map<Long, Integer> idToScoreMap) {
        var type = event.getType();
        var edition = event.getEdition();
        var maxScore = idToScoreMap.values()
                .stream()
                .max(Integer::compareTo).orElse(0);

        event.getScores().forEach(score -> {
            var id = score.getId();
            var team = score.getTeam();

            score.setScore(idToScoreMap.get(id));
            edition.getTeamScores().stream()
                    .filter(teamScore -> teamScore.getTeam().equals(team))
                    .findFirst()
                    .ifPresent(teamScore -> {
                        var currentScore = teamScore.getScore();
                        teamScore.setScore(currentScore + score.getScore());

                        if (!currentScore.equals(maxScore)) return;

                        switch (type) {
                            case EventType.TASK -> teamScore.setTasksWon(teamScore.getTasksWon() + 1);
                            case EventType.SPORT ->  teamScore.setSportsWon(teamScore.getSportsWon() + 1);
                        }
                    });
        });
    }

}
