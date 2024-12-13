package com.bristotartur.cedupscore_api.enums;

import lombok.RequiredArgsConstructor;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public enum Patterns {
    CPF_REGEX("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$"),
    EMAIL_REGEX("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    public final String value;

    public static boolean validateCpf(String cpf) {
        var pattern = Pattern.compile(CPF_REGEX.value);
        return pattern.matcher(cpf).matches();
    }

    public static boolean validateEmail(String email) {
        var pattern = Pattern.compile(EMAIL_REGEX.value);
        return pattern.matcher(email).matches();
    }
}
