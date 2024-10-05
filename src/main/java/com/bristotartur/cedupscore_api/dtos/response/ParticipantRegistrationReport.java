package com.bristotartur.cedupscore_api.dtos.response;

import com.bristotartur.cedupscore_api.dtos.request.ParticipantCSVDto;

import java.util.List;

public record ParticipantRegistrationReport(
        Integer total,
        Integer added,
        Integer notAdded,
        Integer registered,
        Integer problems,
        Integer rejected,
        Integer notRegistered,
        List<ParticipantCSVDto> participantsWithProblems
) {
}
