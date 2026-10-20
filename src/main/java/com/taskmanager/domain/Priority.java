package com.taskmanager.domain;

import com.taskmanager.exception.ValidationException;

/**
 * Priority of a task. Natural order (LOW &lt; MEDIUM &lt; HIGH &lt; URGENT)
 * equals ascending weight, so {@link Enum#compareTo} may be used for sorting.
 */
public enum Priority {

    LOW(1),
    MEDIUM(2),
    HIGH(3),
    URGENT(4);

    private final int weight;

    Priority(int weight) {
        this.weight = weight;
    }

    /**
     * Returns the numeric weight used for sorting and display.
     *
     * @return weight in [1, 4]
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Parses a priority from user input (case-insensitive, trimmed).
     *
     * @param raw raw input; may be {@code null}
     * @return the matching priority
     * @throws ValidationException if {@code raw} is null, blank or unknown
     */
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