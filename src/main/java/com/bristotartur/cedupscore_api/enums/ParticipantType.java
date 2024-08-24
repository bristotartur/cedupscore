package com.bristotartur.cedupscore_api.enums;

public enum ParticipantType {
    STUDENT,
    TEACHER,
    PARENT,
    STUDENT_PARENT,
    TEACHER_PARENT;

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
