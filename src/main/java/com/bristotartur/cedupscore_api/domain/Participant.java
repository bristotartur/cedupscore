package com.bristotartur.cedupscore_api.domain;

import com.bristotartur.cedupscore_api.dtos.request.ParticipantCSVDto;
import com.bristotartur.cedupscore_api.enums.Gender;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import com.bristotartur.cedupscore_api.exceptions.BadRequestException;
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
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ParticipantType type;

    @Column(nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<EditionRegistration> editionRegistrations = new HashSet<>();

    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<EventRegistration> eventRegistrations = new HashSet<>();

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cpf='" + cpf + '\'' +
                ", gender=" + gender +
                ", type=" + type +
                ", isActive=" + isActive +
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

    public boolean compareTo(ParticipantCSVDto dto) throws BadRequestException {
        if (dto == null) return false;
        
        if (dto.getName() == null) {
            throw new BadRequestException("O campo 'nome' está vazio ou possui um cabeçalho inválido.");
        }
        if (dto.getCpf() == null) {
            throw new BadRequestException("O campo 'cpf' está vazio ou possui um cabeçalho inválido.");
        }
        if (dto.getGender() == null) {
            throw new BadRequestException("O campo 'gênero' está vazio ou possui um cabeçalho inválido.");
        }
        if (dto.getType() == null) {
            throw new BadRequestException("O campo 'tipo' está vazio ou possui um cabeçalho inválido.");
        }
        return Objects.equals(name, dto.getName()) &&
               Objects.equals(cpf, dto.getCpf()) &&
               gender.value.equalsIgnoreCase(dto.getGender()) &&
               type.value.equalsIgnoreCase(dto.getType());
    }

}
