package com.bristotartur.cedupscore_api.mappers;

import com.bristotartur.cedupscore_api.domain.Participant;
import com.bristotartur.cedupscore_api.dtos.request.ParticipantRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.EditionRegistrationResponseDto;
import com.bristotartur.cedupscore_api.dtos.response.ParticipantResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParticipantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Participant toNewParticipant(ParticipantRequestDto dto);

    Participant toExistingParticipant(Long id, ParticipantRequestDto dto, Boolean isActive);

    default ParticipantResponseDto toParticipantResponseDto(Participant participant, List<EditionRegistrationResponseDto> editionRegistrations) {
        return ParticipantResponseDto.builder()
                .id(participant.getId())
                .name(participant.getName())
                .cpf(participant.getCpf())
                .type(participant.getType())
                .gender(participant.getGender())
                .isActive(participant.getIsActive())
                .editionRegistrations(editionRegistrations)
                .build();
    }

}
