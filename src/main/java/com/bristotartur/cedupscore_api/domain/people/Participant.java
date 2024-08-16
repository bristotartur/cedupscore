package com.bristotartur.cedupscore_api.domain.people;

import com.bristotartur.cedupscore_api.domain.events.EditionRegistration;
import com.bristotartur.cedupscore_api.enums.GenderCategory;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "TB_PARTICIPANT")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String cpf;

    @Column(nullable = false)
    private String rg;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GenderCategory gender;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ParticipantType type;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private Set<EditionRegistration> editionRegistrations = new HashSet<>();

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cpf='" + cpf + '\'' +
                ", rg='" + rg + '\'' +
                ", gender=" + gender +
                ", type=" + type +
                ", editionRegistrations=" + editionRegistrations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
