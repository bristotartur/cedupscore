package com.bristotartur.cedupscore_api.dtos.request;

import com.bristotartur.cedupscore_api.enums.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RequestUserDto(
        @NotBlank String name,
        @NotBlank String email,
        @NotBlank String password,
        @NotNull Set<RoleType> roles
) {
}
