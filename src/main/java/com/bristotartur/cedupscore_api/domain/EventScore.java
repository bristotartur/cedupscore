package com.bristotartur.cedupscore_api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Entity
@Table(name = "TB_EVENT_SCORE")
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class EventScore extends Score {

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public EventScore(Long id, Integer score, Team team, Event event) {
        super(id, score);
        this.team = team;
        this.event = event;
    }

    @Override
    public String toString() {
        return "SportScore{" +
                "id=" + getId() +
                ", score=" + getScore() +
                ", team=" + team +
                ", event=" + event +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventScore eventScore = (EventScore) o;
        return Objects.equals(getId(), eventScore.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
