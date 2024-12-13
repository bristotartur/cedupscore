package com.bristotartur.cedupscore_api.dtos.request;

import jakarta.validation.constraints.NotNull;

public record EventRegistrationRequestDto(
        @NotNull Long participantId,
        @NotNull Long teamId
) {
}
