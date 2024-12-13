package com.bristotartur.cedupscore_api.enums;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum ParticipantType {
    STUDENT("Aluno"),
    TEACHER("Professor"),
    PARENT("Pai"),
    STUDENT_PARENT("Pai e aluno"),
    TEACHER_PARENT("Pai e professor"),
    TEACHER_STUDENT("Professor e aluno"),
    ALL("Todos");

    public final String value;

    public static ParticipantType findTypeLike(String value) throws IllegalArgumentException {

        return Arrays.stream(values())
                .filter(gender -> value.equals(gender.value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo de participante inválido."));
    }

    public static boolean compareTypes(ParticipantType type, ParticipantType comparedType) {
        if (type.equals(comparedType)) return true;

        return switch (type) {
            case STUDENT -> comparedType.equals(ParticipantType.STUDENT_PARENT) || comparedType.equals(ParticipantType.TEACHER_STUDENT);
            case TEACHER -> comparedType.equals(ParticipantType.TEACHER_PARENT) || comparedType.equals(ParticipantType.TEACHER_STUDENT);
            case PARENT -> comparedType.equals(ParticipantType.STUDENT_PARENT) || comparedType.equals(ParticipantType.TEACHER_PARENT);
            case STUDENT_PARENT -> comparedType.equals(ParticipantType.STUDENT) || comparedType.equals(ParticipantType.PARENT);
            case TEACHER_PARENT -> comparedType.equals(ParticipantType.TEACHER) || comparedType.equals(ParticipantType.PARENT);
            case TEACHER_STUDENT -> comparedType.equals(ParticipantType.TEACHER) || comparedType.equals(ParticipantType.STUDENT);
            case ALL -> true;
        };
    }

}
