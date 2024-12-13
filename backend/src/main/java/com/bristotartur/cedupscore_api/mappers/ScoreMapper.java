package com.bristotartur.cedupscore_api.mappers;

import com.bristotartur.cedupscore_api.domain.*;
import com.bristotartur.cedupscore_api.dtos.request.EventScoreRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.EventScoreResponseDto;
import com.bristotartur.cedupscore_api.dtos.response.TeamResponseDto;
import com.bristotartur.cedupscore_api.dtos.response.TeamScoreResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScoreMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasksWon", constant = "0")
    @Mapping(target = "sportsWon", constant = "0")
    TeamScore toNewTeamScore(Integer score, Edition edition, Team team);

    @Mapping(target = "id", source = "score.id")
    @Mapping(target = "team", source = "teamDto")
    TeamScoreResponseDto toTeamScoreResponseDto(TeamScore score, TeamResponseDto teamDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target= "score", constant = "0")
    EventScore toNewEventScore(Event event, Team team);

    @Mapping(target = "id", source = "score.id")
    @Mapping(target= "score", source = "dto.score")
    EventScore toExistingEventScore(EventScoreRequestDto dto, EventScore score);

    @Mapping(target = "id", source = "score.id")
    @Mapping(target = "team", source = "teamDto")
    EventScoreResponseDto toEventScoreResponseDto(EventScore score, TeamResponseDto teamDto);

}
