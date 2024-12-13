package com.bristotartur.cedupscore_api.dtos.request;

import jakarta.validation.constraints.NotNull;

public record EventScoreRequestDto(@NotNull Long id, @NotNull Integer score) {
}
