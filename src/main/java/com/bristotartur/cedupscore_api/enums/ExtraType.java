package com.bristotartur.cedupscore_api.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ExtraType {
    NORMAL("Normal"),
    COMPLETION("Conclusão"),
    CULTURAL("Cultural"),
    BASKETBALL("Basquete"),
    CHESS("Xadrez"),
    FUTSAL("Futsal"),
    HANDBALL("Handebol"),
    VOLLEYBALL("Vôlei"),
    TABLE_TENNIS("Tênis de mesa");

    public final String value;

    public static String getTaskTypePlural(ExtraType type) {
        return switch (type) {
            case NORMAL -> "normais";
            case COMPLETION -> "de conclusão";
            case CULTURAL -> "culturais";
            default -> throw new IllegalArgumentException("'%s' não é um tipo de tarefa.".formatted(type.name()));
        };
    }

}
