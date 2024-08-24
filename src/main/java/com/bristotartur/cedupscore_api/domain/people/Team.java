package com.bristotartur.cedupscore_api.domain.people;

import com.bristotartur.cedupscore_api.domain.actions.Punishment;
import com.bristotartur.cedupscore_api.domain.matches.MatchTeam;
import com.bristotartur.cedupscore_api.domain.registrations.EditionRegistration;
import com.bristotartur.cedupscore_api.domain.registrations.EventRegistration;
import com.bristotartur.cedupscore_api.domain.scores.EventScore;
import com.bristotartur.cedupscore_api.domain.scores.TeamScore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "TB_TEAM")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String logoUrl;

    @Column(nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<TeamScore> teamScores = new HashSet<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<EventScore> eventScores = new HashSet<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<EditionRegistration> editionRegistrations = new HashSet<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<EventRegistration> eventRegistrations = new HashSet<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Punishment> punishments = new HashSet<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<MatchTeam> matchTeams = new HashSet<>();

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
