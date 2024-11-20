package com.bristotartur.cedupscore_api.dtos.response;

import com.bristotartur.cedupscore_api.enums.Gender;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
public class ParticipantWithEventRegistrationDto extends ParticipantResponseDto {

    public final EventRegistrationResponseDto eventRegistration;

    public ParticipantWithEventRegistrationDto(Long id, String name, String cpf, ParticipantType type, Gender gender, Boolean isActive, List<EditionRegistrationResponseDto> editionRegistrations, EventRegistrationResponseDto eventRegistration) {
        super(id, name, cpf, type, gender, isActive, editionRegistrations);
        this.eventRegistration = eventRegistration;
    }
}
