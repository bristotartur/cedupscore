package com.bristotartur.cedupscore_api.mappers;

import com.bristotartur.cedupscore_api.domain.people.Team;
import com.bristotartur.cedupscore_api.dtos.request.TeamRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.TeamResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Team toNewTeam(TeamRequestDto dto);

    Team toExistingTeam(Long id, TeamRequestDto dto, Boolean isActive);

    TeamResponseDto toTeamResponseDto(Team team);

}
