package com.bristotartur.cedupscore_api.dtos.response;

public record TeamScoreResponseDto(
        Long id,
        Integer score,
        Integer tasksWon,
        Integer sportsWon,
        TeamResponseDto team
) {
}
