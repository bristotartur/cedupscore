package com.bristotartur.cedupscore_api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "TB_MATCH_TEAM")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MatchTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Character teamInMatch;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Override
    public String toString() {
        return "MatchTeam{" +
                "id=" + id +
                ", teamInMatch=" + teamInMatch +
                ", team=" + team +
                ", match=" + match +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchTeam matchTeam = (MatchTeam) o;
        return Objects.equals(id, matchTeam.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
