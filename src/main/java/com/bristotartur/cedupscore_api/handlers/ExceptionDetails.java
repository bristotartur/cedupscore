package com.bristotartur.cedupscore_api.handlers;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class ExceptionDetails {

    public final String title;

    public final Integer status;

    public final String details;

    public final String developerMessage;

    public final LocalDateTime timestamp;

}
