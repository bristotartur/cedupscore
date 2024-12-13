package com.bristotartur.cedupscore_api.dtos.request;

import com.bristotartur.cedupscore_api.enums.ExtraType;
import com.bristotartur.cedupscore_api.enums.Modality;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskEventRequestDto extends EventRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private ExtraType taskType;

    @NotBlank
    private String description;

    public TaskEventRequestDto(String name, ParticipantType allowedParticipantType, Modality modality, Integer minParticipantsPerTeam, Integer maxParticipantsPerTeam, Long responsibleUserId, Long editionId, ExtraType taskType, String description) {
        super(allowedParticipantType, modality, minParticipantsPerTeam, maxParticipantsPerTeam, responsibleUserId, editionId);
        this.name = name;
        this.taskType = taskType;
        this.description = description;
    }

    @Override
    public String toString() {
        return "TaskEventRequestDto{" +
                "name='" + name + '\'' +
                ", taskType='" + taskType + '\'' +
                ", description='" + description + '\'' +
                ", parent=" + super.toString() +
                '}';
    }

}
