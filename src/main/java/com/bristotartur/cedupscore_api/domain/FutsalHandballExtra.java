package com.bristotartur.cedupscore_api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

//@Entity
//@Table(name = "TB_FUTSAL_HANDBALL_EXTRA")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FutsalHandballExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_a_penalty_score")
    private Integer teamPenaltyScoreA;

    @Column(name = "team_b_penalty_score")
    private Integer teamPenaltyScoreB;

    @Column(name = "team_a_yellow_cards")
    private Integer teamYellowCardsA;

    @Column(name = "team_b_yellow_cards")
    private Integer teamYellowCardsB;

    @Column(name = "team_a_red_cards")
    private Integer teamRedCardsA;

    @Column(name = "team_b_red_cards")
    private Integer teamRedCardsB;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Override
    public String toString() {
        return "FutsalHandballExtra{" +
                "id=" + id +
                ", teamPenaltyScoreA=" + teamPenaltyScoreA +
                ", teamPenaltyScoreB=" + teamPenaltyScoreB +
                ", teamYellowCardsA=" + teamYellowCardsA +
                ", teamYellowCardsB=" + teamYellowCardsB +
                ", teamRedCardsA=" + teamRedCardsA +
                ", teamRedCardsB=" + teamRedCardsB +
                ", match=" + match +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FutsalHandballExtra that = (FutsalHandballExtra) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
