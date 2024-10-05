package com.bristotartur.cedupscore_api.mappers;

import com.bristotartur.cedupscore_api.domain.Participant;
import com.bristotartur.cedupscore_api.dtos.request.ParticipantCSVDto;
import com.bristotartur.cedupscore_api.dtos.request.ParticipantRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.EditionRegistrationResponseDto;
import com.bristotartur.cedupscore_api.dtos.response.ParticipantInactivationReport;
import com.bristotartur.cedupscore_api.dtos.response.ParticipantRegistrationReport;
import com.bristotartur.cedupscore_api.dtos.response.ParticipantResponseDto;
import com.bristotartur.cedupscore_api.enums.Gender;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParticipantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Participant toNewParticipant(ParticipantRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "isActive", constant = "true")
    Participant toNewParticipant(ParticipantCSVDto dto, Gender gender, ParticipantType type);

    Participant toExistingParticipant(Long id, ParticipantRequestDto dto, Boolean isActive);

    default ParticipantResponseDto toParticipantResponseDto(Participant participant, List<EditionRegistrationResponseDto> editionRegistrations) {
        return ParticipantResponseDto.builder()
                .id(participant.getId())
                .name(participant.getName())
                .type(participant.getType())
                .gender(participant.getGender())
                .isActive(participant.getIsActive())
                .editionRegistrations(editionRegistrations)
                .build();
    }

    @Mapping(target = "gender", source = "participant.gender.value")
    @Mapping(target = "type", source = "participant.type.value")
    @Mapping(target = "teamId", ignore = true)
    ParticipantCSVDto toParticipantCSVDto(Participant participant, String teamName, String message);

    ParticipantRegistrationReport toParticipantRegistrationReport(Integer total, Integer added, Integer notAdded, Integer registered, Integer problems, Integer rejected, Integer notRegistered, List<ParticipantCSVDto> participantsWithProblems);

    ParticipantInactivationReport toParticipantInactivationReportDto(Integer total, Integer inactivated, Integer notInactivated, Integer problems, List<ParticipantCSVDto> participantsWithProblems);

}
