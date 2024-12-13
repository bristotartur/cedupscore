package com.bristotartur.cedupscore_api.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Modality {
    MASCULINE("Masculino"),
    FEMININE("Feminino"),
    MIXED("Misto");

    public final String value;

    public static boolean compareCategory(Modality modality, Gender gender) {

        return switch (modality) {
            case MIXED -> true;
            case MASCULINE -> gender.equals(Gender.MALE);
            case FEMININE -> gender.equals(Gender.FEMALE);
        };
    }

}
