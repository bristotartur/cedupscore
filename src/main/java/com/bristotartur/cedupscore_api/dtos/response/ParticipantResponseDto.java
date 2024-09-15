package com.bristotartur.cedupscore_api.dtos.response;

import com.bristotartur.cedupscore_api.enums.Gender;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@RequiredArgsConstructor
@Builder
public class ParticipantResponseDto extends RepresentationModel<ParticipantResponseDto> {

    public final Long id;

    public final String name;

    public final ParticipantType type;

    public final Gender gender;

    public final Boolean isActive;

    public final List<EditionRegistrationResponseDto> editionRegistrations;

}
