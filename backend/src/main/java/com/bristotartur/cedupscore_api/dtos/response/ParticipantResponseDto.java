package com.bristotartur.cedupscore_api.dtos.response;

import com.bristotartur.cedupscore_api.enums.Gender;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
public class ParticipantResponseDto {

    public final Long id;

    public final String name;

    public final String cpf;

    public final ParticipantType type;

    public final Gender gender;

    public final Boolean isActive;

    public final List<EditionRegistrationResponseDto> editionRegistrations;

    public ParticipantResponseDto(Long id, String name, String cpf, ParticipantType type, Gender gender, Boolean isActive, List<EditionRegistrationResponseDto> editionRegistrations) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.type = type;
        this.gender = gender;
        this.isActive = isActive;
        this.editionRegistrations = editionRegistrations;
    }

}
