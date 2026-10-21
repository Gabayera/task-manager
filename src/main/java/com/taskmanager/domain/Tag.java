package com.taskmanager.domain;

import com.taskmanager.exception.ValidationException;

public record Tag(String name) {
    public Tag {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Tag name must not be blank");
        }
        name = name.trim().toLowerCase();
    }
}