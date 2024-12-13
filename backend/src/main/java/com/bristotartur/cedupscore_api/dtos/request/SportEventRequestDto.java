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
public class SportEventRequestDto extends EventRequestDto {

    @NotBlank
    private ExtraType sportType;

    public SportEventRequestDto(String name, ParticipantType allowedParticipantType, Modality modality, Integer minParticipantsPerTeam, Integer maxParticipantsPerTeam, Long responsibleUserId, Long editionId, ExtraType sportType) {
        super(allowedParticipantType, modality, minParticipantsPerTeam, maxParticipantsPerTeam, responsibleUserId, editionId);
        this.sportType = sportType;
    }

    @Override
    public String toString() {
        return "SportEventRequestDto{" +
                "sportType='" +  sportType + '\'' +
                ", parent=" + super.toString() +
                '}';
    }

}
