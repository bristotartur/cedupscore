package com.bristotartur.cedupscore_api.domain.actions;

import com.bristotartur.cedupscore_api.domain.matches.Match;
import com.bristotartur.cedupscore_api.domain.people.Participant;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "TB_GOAL")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Character scoredTeam;

    @Column(nullable = false)
    private LocalDateTime goalTime;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Override
    public String toString() {
        return "Goal{" +
                "id=" + id +
                ", scoredTeam=" + scoredTeam +
                ", goalTime=" + goalTime +
                ", participant=" + participant +
                ", match=" + match +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return Objects.equals(id, goal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
