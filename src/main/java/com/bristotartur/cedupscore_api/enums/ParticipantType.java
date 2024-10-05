package com.bristotartur.cedupscore_api.enums;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum ParticipantType {
    STUDENT("Aluno"),
    TEACHER("Professor"),
    PARENT("Pai"),
    STUDENT_PARENT("Pai e aluno"),
    TEACHER_PARENT("Pai e professor");

    public final String value;

    public static ParticipantType findTypeLike(String value) throws IllegalArgumentException {

        return Arrays.stream(values())
                .filter(gender -> value.equals(gender.value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo de participante inválido."));
    }

    public static boolean compareTypes(ParticipantType type, ParticipantType comparedType) {
        if (type.equals(comparedType)) return true;

        switch (type) {
            case STUDENT -> {
                if (comparedType.equals(STUDENT_PARENT)) return true;
            }
            case TEACHER -> {
                if (comparedType.equals(TEACHER_PARENT)) return true;
            }
            case PARENT -> {
                if (comparedType.equals(STUDENT_PARENT) || comparedType.equals(TEACHER_PARENT)) return true;
            }
            case STUDENT_PARENT -> {
                if (comparedType.equals(STUDENT) || comparedType.equals(PARENT)) return true;
            }
            case TEACHER_PARENT -> {
                if (comparedType.equals(TEACHER) || comparedType.equals(PARENT)) return true;
            }
        }
        return false;
    }

}
