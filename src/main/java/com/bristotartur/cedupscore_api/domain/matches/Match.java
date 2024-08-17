package com.bristotartur.cedupscore_api.domain.matches;

import com.bristotartur.cedupscore_api.domain.actions.Goal;
import com.bristotartur.cedupscore_api.domain.actions.MatchSet;
import com.bristotartur.cedupscore_api.domain.actions.PenaltyCard;
import com.bristotartur.cedupscore_api.enums.GenderCategory;
import com.bristotartur.cedupscore_api.enums.Importance;
import com.bristotartur.cedupscore_api.enums.SportType;
import com.bristotartur.cedupscore_api.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "TB_MATCH")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_a_score", nullable = false)
    private Integer teamScoreA;

    @Column(name = "team_b_score", nullable = false)
    private Integer teamScoreB;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SportType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Importance importance;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GenderCategory modality;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private Character woTeam;

    @Column(nullable = false)
    private Boolean hasExtra;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime endedAt;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    private Set<MatchTeam> matchTeams = new HashSet<>(2);

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    private Set<Goal> goals = new HashSet<>();

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    private Set<PenaltyCard> penaltyCards = new HashSet<>();

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    private Set<MatchSet> sets = new HashSet<>();

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", teamScoreA=" + teamScoreA +
                ", teamScoreB=" + teamScoreB +
                ", type=" + type +
                ", importance=" + importance +
                ", modality=" + modality +
                ", status=" + status +
                ", woTeam=" + woTeam +
                ", hasExtra=" + hasExtra +
                ", startedAt=" + startedAt +
                ", endedAt=" + endedAt +
                ", round=" + round +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(id, match.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void setMatchTeams(Set<MatchTeam> matchTeams) {
        if (matchTeams.size() != 2) {
            throw new IllegalArgumentException("Uma partida deve ter duas equipes");
        }
        this.matchTeams = matchTeams;
    }

}
