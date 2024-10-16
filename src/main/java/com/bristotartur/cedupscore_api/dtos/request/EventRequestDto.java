package com.bristotartur.cedupscore_api.dtos.request;

import com.bristotartur.cedupscore_api.enums.Modality;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TaskEventRequestDto.class, name = "task"),
        @JsonSubTypes.Type(value = SportEventRequestDto.class, name = "sport")
})
@Getter
@Setter
@NoArgsConstructor
public abstract class EventRequestDto {

    @NotBlank
    private ParticipantType allowedParticipantType;

    @NotBlank
    private Modality modality;

    @NotNull
    private Integer minParticipantsPerTeam;

    @NotNull
    private Integer maxParticipantsPerTeam;

    @NotNull
    private Long responsibleUserId;

    @NotNull
    private Long editionId;

    public EventRequestDto(ParticipantType allowedParticipantType, Modality modality, Integer minParticipantsPerTeam, Integer maxParticipantsPerTeam, Long responsibleUserId, Long editionId) {
        this.allowedParticipantType = allowedParticipantType;
        this.modality = modality;
        this.minParticipantsPerTeam = minParticipantsPerTeam;
        this.maxParticipantsPerTeam = maxParticipantsPerTeam;
        this.responsibleUserId = responsibleUserId;
        this.editionId = editionId;
    }

    @Override
    public String toString() {
        return "EventRequestDto{" +
                "allowedParticipantType=" + allowedParticipantType +
                ", modality=" + modality +
                ", minParticipantsPerTeam=" + minParticipantsPerTeam +
                ", maxParticipantsPerTeam=" + maxParticipantsPerTeam +
                ", responsibleUserId=" + responsibleUserId +
                ", editionId=" + editionId +
                '}';
    }

}
