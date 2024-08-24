package com.bristotartur.cedupscore_api.dtos.response;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@RequiredArgsConstructor
public class TeamResponseDto extends RepresentationModel<TeamResponseDto> {

    public final Long id;

    public final String name;

    public final String logoUrl;

    public final Boolean isActive;
}
