package com.bristotartur.cedupscore_api.dtos.request;

import com.bristotartur.cedupscore_api.enums.Gender;
import com.bristotartur.cedupscore_api.enums.ParticipantType;

public record ParticipantFilterDto(
        String name,
        Long edition,
        Long event,
        Long team,
        Gender gender,
        ParticipantType type,
        String status,
        String order
) {
}
