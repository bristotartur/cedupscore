package com.bristotartur.cedupscore_api.domain;

import com.bristotartur.cedupscore_api.enums.Color;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "TB_PENALTY_CARD")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PenaltyCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Color color;

    @Column(nullable = false)
    private LocalDateTime penaltyCardTime;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Override
    public String toString() {
        return "PenaltyCard{" +
                "id=" + id +
                ", color=" + color +
                ", penaltyCardTime=" + penaltyCardTime +
                ", participant=" + participant +
                ", match=" + match +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PenaltyCard that = (PenaltyCard) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
