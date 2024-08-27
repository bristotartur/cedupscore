package com.bristotartur.cedupscore_api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "TB_SET")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MatchSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_a_score", nullable = false)
    private Integer teamScoreA;

    @Column(name = "team_b_score", nullable = false)
    private Integer teamScoreB;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime endedAt;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Override
    public String toString() {
        return "MatchSet{" +
                "id=" + id +
                ", teamScoreA=" + teamScoreA +
                ", teamScoreB=" + teamScoreB +
                ", startedAt=" + startedAt +
                ", endedAt=" + endedAt +
                ", match=" + match +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchSet matchSet = (MatchSet) o;
        return Objects.equals(id, matchSet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
