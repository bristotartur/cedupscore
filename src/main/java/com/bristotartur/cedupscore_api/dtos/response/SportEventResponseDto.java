package com.bristotartur.cedupscore_api.dtos.response;

import com.bristotartur.cedupscore_api.enums.ExtraType;
import com.bristotartur.cedupscore_api.enums.Modality;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import com.bristotartur.cedupscore_api.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public class SportEventResponseDto extends EventResponseDto {

    public final ExtraType sportType;

    public final List<EventScoreResponseDto> scores;

    public SportEventResponseDto(Long id, String name, Status status, ParticipantType allowedParticipantType, Modality modality, Integer minParticipantsPerTeam, Integer maxParticipantsPerTeam, LocalDateTime startedAt, LocalDateTime endedAt, Long editionId, Long responsibleUserId, ExtraType sportType, List<EventScoreResponseDto> scores) {
        super(id, name, status, allowedParticipantType, modality, minParticipantsPerTeam, maxParticipantsPerTeam, startedAt, endedAt, editionId, responsibleUserId);
        this.sportType = sportType;
        this.scores = scores;
    }

}
