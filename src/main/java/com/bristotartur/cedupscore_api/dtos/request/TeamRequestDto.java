package com.bristotartur.cedupscore_api.dtos.request;

import com.bristotartur.cedupscore_api.enums.TeamLogo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TeamRequestDto(@NotBlank String name, @NotNull TeamLogo logo) {
}
