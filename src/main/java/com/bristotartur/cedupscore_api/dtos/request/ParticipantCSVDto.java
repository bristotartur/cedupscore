package com.bristotartur.cedupscore_api.dtos.request;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParticipantCSVDto {

    @CsvBindByName(column = "nome")
    private String name;

    @CsvBindByName(column = "cpf")
    private String cpf;

    @CsvBindByName(column = "gênero")
    private String gender;

    @CsvBindByName(column = "tipo")
    private String type;

    @CsvBindByName(column = "equipe")
    private String teamName;

    private Long teamId;

    private String message;

    public ParticipantCSVDto(String name, String cpf) {
        this.name = name;
        this.cpf = cpf;
    }

    public ParticipantCSVDto(String name, String cpf, String gender, String type, String teamName) {
        this.name = name;
        this.cpf = cpf;
        this.gender = gender;
        this.type = type;
        this.teamName = teamName;
    }

}
