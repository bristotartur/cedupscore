package com.bristotartur.cedupscore_api.dtos.response;

import com.bristotartur.cedupscore_api.enums.ExtraType;
import com.bristotartur.cedupscore_api.enums.Modality;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import com.bristotartur.cedupscore_api.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public class TaskEventResponseDto extends EventResponseDto {

    public final ExtraType taskType;

    public final String description;

    public final List<EventScoreResponseDto> scores;

    public TaskEventResponseDto(Long id, String name, Status status, ParticipantType allowedParticipantType, Modality modality, Integer minParticipantsPerTeam, Integer maxParticipantsPerTeam, LocalDateTime startedAt, LocalDateTime endedAt, ExtraType taskType, String description, List<EventScoreResponseDto> scores) {
        super(id, name, status, allowedParticipantType, modality, minParticipantsPerTeam, maxParticipantsPerTeam, startedAt, endedAt);
        this.taskType = taskType;
        this.description = description;
        this.scores = scores;
    }

}
