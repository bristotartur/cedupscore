package com.bristotartur.cedupscore_api.dtos.request;

import com.bristotartur.cedupscore_api.enums.Gender;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ParticipantRequestDto(
    @NotBlank String name,
    @NotBlank String cpf,
    @NotNull Gender gender,
    @NotNull ParticipantType type,
    @NotNull Long teamId
) {
}
