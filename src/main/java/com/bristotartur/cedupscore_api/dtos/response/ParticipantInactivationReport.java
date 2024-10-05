package com.bristotartur.cedupscore_api.dtos.response;

import com.bristotartur.cedupscore_api.dtos.request.ParticipantCSVDto;

import java.util.List;

public record ParticipantInactivationReport(
        Integer total,
        Integer inactivated,
        Integer notInactivated,
        Integer problems,
        List<ParticipantCSVDto> participantsWithProblems
) {
}
