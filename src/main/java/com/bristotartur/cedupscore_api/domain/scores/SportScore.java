package com.bristotartur.cedupscore_api.domain.scores;

import com.bristotartur.cedupscore_api.domain.events.SportEvent;
import com.bristotartur.cedupscore_api.domain.people.Team;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Entity
@Table(name = "TB_SPORT_SCORE")
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class SportScore extends Score {

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sport_event_id", nullable = false)
    private SportEvent sportEvent;

    public SportScore(Long id, Integer score, Team team, SportEvent sportEvent) {
        super(id, score);
        this.team = team;
        this.sportEvent = sportEvent;
    }

    @Override
    public String toString() {
        return "SportScore{" +
                "id=" + getId() +
                ", score=" + getScore() +
                ", team=" + team +
                ", sportEvent=" + sportEvent +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SportScore sportScore = (SportScore) o;
        return Objects.equals(getId(), sportScore.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
