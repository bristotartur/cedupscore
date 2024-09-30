package com.bristotartur.cedupscore_api.dtos.response;

import java.util.List;

public record ParticipantCSVUploadResponseDto(
        Integer total,
        Integer added,
        Integer notAdded,
        Integer registered,
        Integer problems,
        Integer rejected,
        Integer notRegistered,
        List<RejectedParticipantResponseDto> participantsWithProblems
) {
}
