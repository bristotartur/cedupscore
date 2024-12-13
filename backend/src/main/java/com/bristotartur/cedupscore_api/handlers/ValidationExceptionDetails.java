package com.bristotartur.cedupscore_api.handlers;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ValidationExceptionDetails extends ExceptionDetails {

    public final String fields;

    public final String fieldsMessages;

}
