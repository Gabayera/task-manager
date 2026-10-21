package com.taskmanager.domain;

import com.taskmanager.exception.ValidationException;


public enum Priority {

    LOW(1),
    MEDIUM(2),
    HIGH(3),
    URGENT(4);

    private final int weight;

    Priority(int weight) {
        this.weight = weight;
    }

    
    public int getWeight() {
        return weight;
    }

 
    public static Priority fromString(String raw) {
        if (raw == null) {
            throw new ValidationException(
                    "Priority must not be null. Expected one of: "
                            + "LOW, MEDIUM, HIGH, URGENT");
        }
        String normalised = raw.trim().toUpperCase();
        for (Priority p : values()) {
            if (p.name().equals(normalised)) {
                return p;
            }
        }
        throw new ValidationException(
                "Unknown priority '" + raw + "'. Expected one of: "
                        + "LOW, MEDIUM, HIGH, URGENT");
    }
}