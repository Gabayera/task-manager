package com.taskmanager.domain;

import com.taskmanager.exception.ValidationException;
import java.util.Objects;

public final class Category {
    private final String id;
    private final String userId;
    private final String name;
    private final String color;

    // 4-argument constructor (kept for when a specific color is provided)
    public Category(String id, String userId, String name, String color) {
        if (id == null || id.isBlank()) throw new ValidationException("Category id must not be blank");
        if (userId == null || userId.isBlank()) throw new ValidationException("Category userId must not be blank");
        if (name == null || name.isBlank()) throw new ValidationException("Category name must not be blank");
        
        this.id = id;
        this.userId = userId;
        this.name = name.trim();
        this.color = (color == null || color.isBlank()) ? "#3366cc" : color;
    }

    public Category(String id, String userId, String name) {
        this(id, userId, name, "#3366cc");
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getColor() { return color; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}