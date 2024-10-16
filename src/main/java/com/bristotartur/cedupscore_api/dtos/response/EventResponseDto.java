package com.bristotartur.cedupscore_api.dtos.response;

import com.bristotartur.cedupscore_api.enums.Modality;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import com.bristotartur.cedupscore_api.enums.Status;

import java.time.LocalDateTime;

public abstract class EventResponseDto {

    public final Long id;

    public final String name;

    public final Status status;

    public final ParticipantType allowedParticipantType;

    public final Modality modality;

    public final Integer minParticipantsPerTeam;

    public final Integer maxParticipantsPerTeam;

    public final LocalDateTime startedAt;

    public final LocalDateTime endedAt;

    public EventResponseDto(Long id, String name, Status status, ParticipantType allowedParticipantType, Modality modality, Integer minParticipantsPerTeam, Integer maxParticipantsPerTeam, LocalDateTime startedAt, LocalDateTime endedAt) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.allowedParticipantType = allowedParticipantType;
        this.modality = modality;
        this.minParticipantsPerTeam = minParticipantsPerTeam;
        this.maxParticipantsPerTeam = maxParticipantsPerTeam;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

}
