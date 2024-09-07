package com.bristotartur.cedupscore_api.dtos.response;

import com.bristotartur.cedupscore_api.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class EditionResponseDto extends RepresentationModel<EditionResponseDto> {

    public final Long id;

    public final Status status;

    public final LocalDate startDate;

    public final LocalDate closingDate;

    public final List<TeamScoreResponseDto> teamsScores;

}
