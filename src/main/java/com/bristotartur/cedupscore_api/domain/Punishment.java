package com.bristotartur.cedupscore_api.domain;

import com.bristotartur.cedupscore_api.enums.PunishmentScope;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "TB_PUNISHMENT")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Punishment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PunishmentScope scope;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Lob
    private String description;

    @Column(nullable = false)
    private Integer lostScore;

    @Column(nullable = false)
    private LocalDateTime appliedAt;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "edition_id", nullable = false)
    private Edition edition;

    @Override
    public String toString() {
        return "Punishment{" +
                "id=" + id +
                ", scope=" + scope +
                ", description='" + description + '\'' +
                ", lostScore=" + lostScore +
                ", appliedAt=" + appliedAt +
                ", team=" + team +
                ", edition=" + edition +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Punishment that = (Punishment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
