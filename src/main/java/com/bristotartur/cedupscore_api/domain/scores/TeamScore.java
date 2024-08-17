package com.bristotartur.cedupscore_api.domain.scores;

import com.bristotartur.cedupscore_api.domain.events.Edition;
import com.bristotartur.cedupscore_api.domain.people.Team;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Entity
@Table(name = "TB_TEAM_SCORE")
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class TeamScore extends Score {

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "edition_id", nullable = false)
    private Edition edition;

    public TeamScore(Long id, Integer score, Team team, Edition edition) {
        super(id, score);
        this.team = team;
        this.edition = edition;
    }

    @Override
    public String toString() {
        return "TeamScore{" +
                "id=" + getId() +
                ", score=" + getScore() +
                ", team=" + team +
                ", edition=" + edition +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamScore teamScore = (TeamScore) o;
        return Objects.equals(getId(), teamScore.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

}
