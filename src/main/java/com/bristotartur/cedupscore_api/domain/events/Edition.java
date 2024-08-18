package com.bristotartur.cedupscore_api.domain.events;

import com.bristotartur.cedupscore_api.domain.actions.Punishment;
import com.bristotartur.cedupscore_api.domain.registrations.EditionRegistration;
import com.bristotartur.cedupscore_api.domain.scores.TeamScore;
import com.bristotartur.cedupscore_api.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "TB_EDITION")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Edition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate closingDate;

    @OneToMany(mappedBy = "edition", cascade = CascadeType.ALL)
    private Set<TaskEvent> taskEvents = new HashSet<>();

    @OneToMany(mappedBy = "edition", cascade = CascadeType.ALL)
    private Set<SportEvent> sportEvents = new HashSet<>();

    @OneToMany(mappedBy = "edition", cascade = CascadeType.ALL)
    private Set<TeamScore> teamScores = new HashSet<>();

    @OneToMany(mappedBy = "edition", cascade = CascadeType.ALL)
    private Set<EditionRegistration> editionRegistrations = new HashSet<>();

    @OneToMany(mappedBy = "edition", cascade = CascadeType.ALL)
    private Set<Punishment> punishments = new HashSet<>();

    @Override
    public String toString() {
        return "Edition{" +
                "id=" + id +
                ", status=" + status +
                ", startDate=" + startDate +
                ", closingDate=" + closingDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edition edition = (Edition) o;
        return Objects.equals(id, edition.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
