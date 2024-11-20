package com.bristotartur.cedupscore_api.dtos.request;

import com.bristotartur.cedupscore_api.enums.Gender;
import com.bristotartur.cedupscore_api.enums.ParticipantType;

public record ParticipantFilterDto(
        String name,
        Long edition,
        Long event,
        Long notInEvent,
        Long team,
        Gender gender,
        ParticipantType type,
        String status,
        String order
) {
    public ParticipantFilterDto(String name, Long edition, Long event, Long notInEvent, Long team, Gender gender, ParticipantType type, String status, String order) {
        this.name = name;
        this.edition = edition;
        this.team = team;
        this.gender = gender;
        this.type = type;
        this.status = status;
        this.order = order;
        this.event = (event != null && event.equals(notInEvent)) ? null : event;
        this.notInEvent = (event != null && event.equals(notInEvent)) ? null : notInEvent;
    }

    public ParticipantFilterDto withUpdatedNotInEvent(Long notInEvent) {

        return new ParticipantFilterDto(
                this.name, this.edition, this.event, notInEvent, this.team, this.gender, this.type, this.status, this.order
        );
    }
}
