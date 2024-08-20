package com.bristotartur.cedupscore_api.dtos.response;

import com.bristotartur.cedupscore_api.enums.TeamLogo;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@RequiredArgsConstructor
public class TeamResponseDto extends RepresentationModel<TeamResponseDto> {

    public final Long id;

    public final String name;

    public final TeamLogo logo;

    public final Boolean isActive;
}
