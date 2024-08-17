package com.bristotartur.cedupscore_api.domain.registrations;

import com.bristotartur.cedupscore_api.domain.events.SportEvent;
import com.bristotartur.cedupscore_api.domain.people.Participant;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "TB_SPORT_REGISTRATION")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SportRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sport_event_id", nullable = false)
    private SportEvent sportEvent;

    @Override
    public String toString() {
        return "SportRegistration{" +
                "id=" + id +
                ", participant=" + participant +
                ", sportRegistration=" + sportEvent +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SportRegistration that = (SportRegistration) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
