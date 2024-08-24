package com.bristotartur.cedupscore_api.domain.events;

import com.bristotartur.cedupscore_api.domain.registrations.EventRegistration;
import com.bristotartur.cedupscore_api.domain.scores.EventScore;
import com.bristotartur.cedupscore_api.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "TB_EVENT")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExtraType extraType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ParticipantType allowedParticipantType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Modality modality;

    @Column(nullable = false)
    private Integer minParticipantsPerTeam;

    @Column(nullable = false)
    private Integer maxParticipantsPerTeam;

    @Column(columnDefinition = "TEXT")
    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime endedAt;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "edition_id", nullable = false)
    private Edition edition;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<EventScore> scores = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<EventRegistration> registrations = new HashSet<>();

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", status=" + status +
                ", type=" + type +
                ", extraType=" + extraType +
                ", modality=" + modality +
                ", allowedParticipantType=" + allowedParticipantType +
                ", description='" + description + '\'' +
                ", startedAt=" + startedAt +
                ", endedAt=" + endedAt +
                ", edition=" + edition +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
