package com.bristotartur.cedupscore_api.enums;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Gender {
    MALE("Masculino"),
    FEMALE("Feminino");

    public final String value;

    public static Gender findGenderLike(String value) throws IllegalArgumentException {

        return Arrays.stream(values())
                .filter(gender -> value.equals(gender.value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Gênero inválido."));
    }

}
