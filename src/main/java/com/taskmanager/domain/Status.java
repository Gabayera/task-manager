package com.taskmanager.domain;

import com.taskmanager.exception.ValidationException;

public enum Status {

    TODO,
    IN_PROGRESS,
    DONE,
    CANCELLED;

    public boolean isTerminal() {
        return this == DONE || this == CANCELLED;
    }

    public boolean canTransitionTo(Status next) {

        if (next == null) {
            return false;
        }

        return switch (this) {

            case TODO ->
                next == IN_PROGRESS
                        || next == DONE
                        || next == CANCELLED;

            case IN_PROGRESS ->
                next == DONE
                        || next == CANCELLED;

            case DONE, CANCELLED -> false;
        };
    }

    public static Status fromString(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw invalidStatus(raw);
        }

        String normalized = raw.trim().replace('-', '_').replace(' ', '_');

        for (Status status : values()) {
            if (status.name().equalsIgnoreCase(normalized)) {
                return status;
            }
        }

        throw invalidStatus(raw);
    }

    private static ValidationException invalidStatus(String raw) {

        return new ValidationException(
                "Invalid status '" + raw
                        + "'. Expected one of: TODO, IN_PROGRESS, DONE, CANCELLED.");
    }

}