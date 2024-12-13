package com.bristotartur.cedupscore_api.dtos.response;

import java.time.LocalDateTime;

public record EditionRegistrationResponseDto(Long id, Long editionId, LocalDateTime createdAt, TeamResponseDto team) {
}
