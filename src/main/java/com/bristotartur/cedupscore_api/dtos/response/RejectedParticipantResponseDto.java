package com.bristotartur.cedupscore_api.dtos.response;

import com.bristotartur.cedupscore_api.enums.Gender;
import com.bristotartur.cedupscore_api.enums.ParticipantType;

public record RejectedParticipantResponseDto(
        String name,
        String cpf,
        Gender gender,
        ParticipantType type,
        Long team,
        String message
) {
}
