package com.bristotartur.cedupscore_api.domain.events;

import com.bristotartur.cedupscore_api.domain.registrations.SportRegistration;
import com.bristotartur.cedupscore_api.domain.scores.SportScore;
import com.bristotartur.cedupscore_api.enums.GenderCategory;
import com.bristotartur.cedupscore_api.enums.SportType;
import com.bristotartur.cedupscore_api.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "TB_SPORT_EVENT")
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class SportEvent extends Event {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SportType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GenderCategory modality;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "edition_id", nullable = false)
    private Edition edition;

    @OneToMany(mappedBy = "sportEvent", cascade = CascadeType.ALL)
    private Set<SportScore> sportScores = new HashSet<>();

    @OneToMany(mappedBy = "sportEvent", cascade = CascadeType.ALL)
    private Set<SportRegistration> sportRegistrations = new HashSet<>();

    public SportEvent(Long id, Status status, LocalDateTime startedAt, LocalDateTime endedAt, SportType type, GenderCategory modality, Edition edition) {
        super(id, status, startedAt, endedAt);
        this.type = type;
        this.modality = modality;
        this.edition = edition;
    }

    @Override
    public String toString() {
        return "SportEvent{" +
                "id=" + getId() +
                ", status=" + getStatus() +
                ", startedAt=" + getStartedAt() +
                ", endedAt=" + getEndedAt() +
                ", type=" + type +
                ", modality=" + modality +
                ", edition=" + edition +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SportEvent sportEvent = (SportEvent) o;
        return Objects.equals(getId(), sportEvent.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
