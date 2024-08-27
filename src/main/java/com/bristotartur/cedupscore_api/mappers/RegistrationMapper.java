package com.bristotartur.cedupscore_api.mappers;

import com.bristotartur.cedupscore_api.domain.Edition;
import com.bristotartur.cedupscore_api.domain.Event;
import com.bristotartur.cedupscore_api.domain.Participant;
import com.bristotartur.cedupscore_api.domain.Team;
import com.bristotartur.cedupscore_api.domain.EditionRegistration;
import com.bristotartur.cedupscore_api.domain.EventRegistration;
import com.bristotartur.cedupscore_api.dtos.response.EditionRegistrationResponseDto;
import com.bristotartur.cedupscore_api.dtos.response.TeamResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {

    @Mapping(target = "id", ignore = true)
    EditionRegistration toNewEditionRegistration(Participant participant, Edition edition, Team team);

    @Mapping(target = "id", source = "registration.id")
    @Mapping(target = "editionId", source = "registration.edition.id")
    @Mapping(target = "team", source = "teamDto")
    EditionRegistrationResponseDto toEditionRegistrationResponseDto(EditionRegistration registration, TeamResponseDto teamDto);

    @Mapping(target = "id", ignore = true)
    EventRegistration toNewEventRegistration(Participant participant, Event event, Team team);

}
