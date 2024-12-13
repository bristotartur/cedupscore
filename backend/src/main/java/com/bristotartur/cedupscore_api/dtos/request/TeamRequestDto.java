package com.bristotartur.cedupscore_api.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TeamRequestDto(@NotBlank String name, @NotNull String logoUrl) {
}
