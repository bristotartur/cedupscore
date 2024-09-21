package com.bristotartur.cedupscore_api.domain;

import com.bristotartur.cedupscore_api.enums.Importance;
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

    @Column(nullable = false)
    private Integer round;

    @Column(name = "team_a_score", nullable = false)
    private Integer teamScoreA;

    @Column(name = "team_b_score", nullable = false)
    private Integer teamScoreB;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Importance importance;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "match_id", referencedColumnName = "id")
    private FutsalHandballExtra extra;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<MatchTeam> matchTeams = new HashSet<>(2);

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<MatchSet> sets = new HashSet<>();

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", round=" + round +
                ", teamScoreA=" + teamScoreA +
                ", teamScoreB=" + teamScoreB +
                ", importance=" + importance +
                ", status=" + status +
                ", woTeam=" + woTeam +
                ", hasExtra=" + hasExtra +
                ", startedAt=" + startedAt +
                ", endedAt=" + endedAt +
                ", extra=" + extra +
                ", event=" + event +
                ", matchTeams=" + matchTeams +
                ", sets=" + sets +
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
