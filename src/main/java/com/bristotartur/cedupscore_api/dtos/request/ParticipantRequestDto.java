package com.bristotartur.cedupscore_api.dtos.request;

import com.bristotartur.cedupscore_api.enums.Gender;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParticipantRequestDto {

        @CsvBindByName(column = "nome")
        @NotBlank
        private String name;

        @CsvBindByName(column = "cpf")
        @NotBlank
        private String cpf;

        @CsvBindByName(column = "genero")
        @NotNull
        private Gender gender;

        @CsvBindByName(column = "tipo")
        @NotNull
        private ParticipantType type;

        @CsvBindByName(column = "equipe")
        @NotNull
        private Long teamId;

}
