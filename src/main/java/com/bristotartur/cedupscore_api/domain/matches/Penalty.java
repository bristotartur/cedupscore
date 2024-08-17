package com.bristotartur.cedupscore_api.domain.matches;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "TB_PENALTY")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_a_score", nullable = false)
    private Integer teamScoreA;

    @Column(name = "team_b_score", nullable = false)
    private Integer teamScoreB;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Override
    public String toString() {
        return "Penalty{" +
                "id=" + id +
                ", teamScoreA=" + teamScoreA +
                ", teamScoreB=" + teamScoreB +
                ", match=" + match +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Penalty penalty = (Penalty) o;
        return Objects.equals(id, penalty.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
