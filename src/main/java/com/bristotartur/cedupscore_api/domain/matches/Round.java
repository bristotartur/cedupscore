package com.bristotartur.cedupscore_api.domain.matches;

import com.bristotartur.cedupscore_api.domain.events.SportEvent;
import com.bristotartur.cedupscore_api.enums.Importance;
import com.bristotartur.cedupscore_api.enums.SportType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "TB_ROUND")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SportType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Importance importance;

    @Column(nullable = false)
    private Integer totalMatches;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sport_event_id", nullable = false)
    private SportEvent sportEvent;

    @Override
    public String toString() {
        return "Round{" +
                "id=" + id +
                ", type=" + type +
                ", importance=" + importance +
                ", totalMatches=" + totalMatches +
                ", sportEvent=" + sportEvent +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Round round = (Round) o;
        return Objects.equals(id, round.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
