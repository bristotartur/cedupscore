package com.bristotartur.cedupscore_api.mappers;

import com.bristotartur.cedupscore_api.domain.Edition;
import com.bristotartur.cedupscore_api.domain.Event;
import com.bristotartur.cedupscore_api.domain.User;
import com.bristotartur.cedupscore_api.dtos.request.SportEventRequestDto;
import com.bristotartur.cedupscore_api.dtos.request.TaskEventRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.EventScoreResponseDto;
import com.bristotartur.cedupscore_api.dtos.response.SportEventResponseDto;
import com.bristotartur.cedupscore_api.dtos.response.TaskEventResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "scores", ignore = true)
    @Mapping(target = "registrations", ignore = true)
    @Mapping(target = "matches", ignore = true)
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "extraType", source = "dto.taskType")
    @Mapping(target = "type", expression = "java(com.bristotartur.cedupscore_api.enums.EventType.TASK)")
    @Mapping(target = "status", expression = "java(com.bristotartur.cedupscore_api.enums.Status.SCHEDULED)")
    @Mapping(target = "startedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "endedAt", expression = "java(java.time.LocalDateTime.now())")
    Event toNewTaskEvent(TaskEventRequestDto dto, Edition edition, User responsibleUser);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "scores", ignore = true)
    @Mapping(target = "registrations", ignore = true)
    @Mapping(target = "matches", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "extraType", source = "dto.sportType")
    @Mapping(target = "type", expression = "java(com.bristotartur.cedupscore_api.enums.EventType.SPORT)")
    @Mapping(target = "status", expression = "java(com.bristotartur.cedupscore_api.enums.Status.SCHEDULED)")
    @Mapping(target = "startedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "endedAt", expression = "java(java.time.LocalDateTime.now())")
    Event toNewSportEvent(SportEventRequestDto dto, String name, Edition edition, User responsibleUser);

    @Mapping(target = "matches", ignore = true)
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "extraType", source = "dto.taskType")
    @Mapping(target = "modality", source = "dto.modality")
    @Mapping(target = "allowedParticipantType", source = "dto.allowedParticipantType")
    @Mapping(target = "minParticipantsPerTeam", source = "dto.minParticipantsPerTeam")
    @Mapping(target = "maxParticipantsPerTeam", source = "dto.maxParticipantsPerTeam")
    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "status", source = "event.status")
    @Mapping(target = "startedAt", source = "event.startedAt")
    @Mapping(target = "endedAt", source = "event.endedAt")
    @Mapping(target = "scores", source = "event.scores")
    @Mapping(target = "registrations", source = "event.registrations")
    @Mapping(target = "edition", source = "edition")
    @Mapping(target = "responsibleUser", source = "responsibleUser")
    Event toExistingTaskEvent(TaskEventRequestDto dto, Event event, Edition edition, User responsibleUser);

    @Mapping(target = "description", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "extraType", source = "dto.sportType")
    @Mapping(target = "modality", source = "dto.modality")
    @Mapping(target = "allowedParticipantType", source = "dto.allowedParticipantType")
    @Mapping(target = "minParticipantsPerTeam", source = "dto.minParticipantsPerTeam")
    @Mapping(target = "maxParticipantsPerTeam", source = "dto.maxParticipantsPerTeam")
    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "status", source = "event.status")
    @Mapping(target = "startedAt", source = "event.startedAt")
    @Mapping(target = "endedAt", source = "event.endedAt")
    @Mapping(target = "scores", source = "event.scores")
    @Mapping(target = "registrations", source = "event.registrations")
    @Mapping(target = "matches", source = "event.matches")
    @Mapping(target = "edition", source = "edition")
    @Mapping(target = "responsibleUser", source = "responsibleUser")
    Event toExistingSportEvent(SportEventRequestDto dto, Event event, String name, Edition edition, User responsibleUser);

    @Mapping(target = "taskType", source = "event.extraType")
    @Mapping(target = "editionId", source = "event.edition.id")
    @Mapping(target = "responsibleUserId", source = "event.responsibleUser.id")
    TaskEventResponseDto toTaskEventResponseDto(Event event, List<EventScoreResponseDto> scores);

    @Mapping(target = "sportType", source = "event.extraType")
    @Mapping(target = "editionId", source = "event.edition.id")
    @Mapping(target = "responsibleUserId", source = "event.responsibleUser.id")
    SportEventResponseDto toSportEventResponseDto(Event event, List<EventScoreResponseDto> scores);

}
