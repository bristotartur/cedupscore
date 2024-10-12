package com.bristotartur.cedupscore_api.enums;

import com.bristotartur.cedupscore_api.exceptions.BadRequestException;
import com.bristotartur.cedupscore_api.exceptions.UnprocessableEntityException;

public enum Status {
    SCHEDULED,
    IN_PROGRESS,
    STOPPED,
    ENDED,
    CANCELED,
    OPEN_FOR_EDITS;

    public static Status findStatusLike(String status) {

        var formattedStatus = status.replace("-", "_").toUpperCase();

        try {
            return valueOf(formattedStatus);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Status inválido.", e);
        }
    }

    public static void checkStatus(Status originalStatus, Status newStatus) {
        if (originalStatus.equals(newStatus)) return;

        var message = "Status inválido.";

        switch (originalStatus) {
            case SCHEDULED -> {
                if (!newStatus.equals(IN_PROGRESS)) throw new UnprocessableEntityException(message);
            }
            case IN_PROGRESS, STOPPED -> {
                if (newStatus.equals(SCHEDULED) || newStatus.equals(OPEN_FOR_EDITS)) throw new UnprocessableEntityException(message);
            }
            case ENDED -> {
                if (!newStatus.equals(OPEN_FOR_EDITS)) throw new UnprocessableEntityException(message);
            }
            case CANCELED -> throw new UnprocessableEntityException(message);
            case OPEN_FOR_EDITS -> {
                if (!newStatus.equals(ENDED)) throw new UnprocessableEntityException(message);
            }
        }
    }
}
