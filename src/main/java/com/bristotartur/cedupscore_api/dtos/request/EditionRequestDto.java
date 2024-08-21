package com.bristotartur.cedupscore_api.dtos.request;

import com.bristotartur.cedupscore_api.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.time.LocalDate;

public record EditionRequestDto(
        @NotNull Status status,
        @NonNull LocalDate startDate,
        @NotNull LocalDate closingDate
) {
}
