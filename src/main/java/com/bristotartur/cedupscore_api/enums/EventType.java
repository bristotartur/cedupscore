package com.bristotartur.cedupscore_api.enums;

import org.springframework.security.authentication.BadCredentialsException;

public enum EventType {
    TASK,
    SPORT;

    public static EventType findEventTypeLike(String type) {
        try {
            return valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Tipo de evento inválido", e);
        }
    }

}
