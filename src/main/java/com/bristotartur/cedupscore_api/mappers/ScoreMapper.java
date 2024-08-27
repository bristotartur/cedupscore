package com.bristotartur.cedupscore_api.mappers;

import com.bristotartur.cedupscore_api.domain.Edition;
import com.bristotartur.cedupscore_api.domain.Team;
import com.bristotartur.cedupscore_api.domain.TeamScore;
import com.bristotartur.cedupscore_api.dtos.response.ScoreResponseDto;
import com.bristotartur.cedupscore_api.dtos.response.TeamResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScoreMapper {

    @Mapping(target = "id", ignore = true)
    TeamScore toNewTeamScore(Integer score, Edition edition, Team team);

    @Mapping(target = "id", source = "score.id")
    @Mapping(target = "team", source = "teamDto")
    ScoreResponseDto toTeamScoreResponseDto(TeamScore score, TeamResponseDto teamDto);

}
