package com.bristotartur.cedupscore_api.domain.people;

import com.bristotartur.cedupscore_api.domain.events.EditionRegistration;
import com.bristotartur.cedupscore_api.domain.events.TeamScore;
import com.bristotartur.cedupscore_api.enums.TeamLogo;
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
    @Enumerated(EnumType.STRING)
    private TeamLogo logo;

    @Column(nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private Set<TeamScore> teamScores = new HashSet<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private Set<EditionRegistration> editionRegistrations = new HashSet<>();

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", logo=" + logo +
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
