package com.bristotartur.cedupscore_api.enums;

public enum Modality {
    MASCULINE,
    FEMININE,
    MIXED;

    public static boolean compareCategory(Modality modality, Gender gender) {

        return switch (modality) {
            case MIXED -> true;
            case MASCULINE -> gender.equals(Gender.MALE);
            case FEMININE -> gender.equals(Gender.FEMALE);
        };
    }

}
