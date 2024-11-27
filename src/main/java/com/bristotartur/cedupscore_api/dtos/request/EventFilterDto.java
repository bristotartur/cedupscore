package com.bristotartur.cedupscore_api.dtos.request;

public record EventFilterDto(
        String type,
        Long participant,
        Long edition,
        Long user
) {
    public EventFilterDto(String type, Long participant, Long edition, Long user) {
        this.type = type;
        this.participant = participant;
        this.edition = edition;
        this.user = user;
    }

    public EventFilterDto withUpdatedUser(Long user) {

        return new EventFilterDto(
                this.type, this.participant, this.edition, user
        );
    }
}
