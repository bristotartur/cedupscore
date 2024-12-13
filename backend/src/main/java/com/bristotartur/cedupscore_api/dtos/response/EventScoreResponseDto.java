package com.bristotartur.cedupscore_api.dtos.response;

public record EventScoreResponseDto(
        Long id,
        Integer score,
        TeamResponseDto team
) {
}
