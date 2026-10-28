package com.taskmanager.domain;

import com.taskmanager.exception.ValidationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Task {
    private final String id;
    private final String userId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Priority priority;
    private Status status;
    private String categoryId;
    private final Set<Tag> tags = new HashSet<>();

    private final LocalDateTime createdAt;
    private LocalDateTime completedAt;

    private Task(String id, String userId, String title, LocalDateTime createdAt) {
        if (id == null || id.isBlank()) throw new ValidationException("Task id must not be blank");
        if (userId == null || userId.isBlank()) throw new ValidationException("Task userId must not be blank");
        if (title == null || title.isBlank()) throw new ValidationException("Task title must not be blank");

        this.id = id;
        this.userId = userId;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.title = title.trim();
        this.priority = Priority.MEDIUM;
        this.status = Status.TODO;
        this.completedAt = null;
    }

    public static Task create(String id, String userId, String title) {
        return new Task(id, userId, title, LocalDateTime.now());
    }

    public static Builder builder(String id, String userId, String title) {
        return new Builder(id, userId, title);
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getTitle() { return title; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Priority getPriority() { return priority; }
    public Status getStatus() { return status; }
    public Optional<String> getDescription() { return Optional.ofNullable(description); }
    public Optional<LocalDate> getDueDate() { return Optional.ofNullable(dueDate); }
    public Optional<String> getCategoryId() { return Optional.ofNullable(categoryId); }
    public Optional<LocalDateTime> getCompletedAt() { return Optional.ofNullable(completedAt); }
    public Set<Tag> getTags() { return Collections.unmodifiableSet(tags); }

    public void rename(String newTitle) {
        if (newTitle == null || newTitle.isBlank()) {
            throw new ValidationException("title must not be blank");
        }
        this.title = newTitle.trim();
    }

    public void describe(String description) { this.description = description; }

    public void reschedule(LocalDate dueDate) {
        if (dueDate != null && dueDate.isBefore(createdAt.toLocalDate())) {
            throw new ValidationException("dueDate cannot be before the creation date");
        }
        this.dueDate = dueDate;
    }

    public void reprioritize(Priority priority) {
        this.priority = Objects.requireNonNull(priority, "priority");
    }

    public void categorize(String categoryId) { this.categoryId = categoryId; }

    public void addTag(Tag tag) { tags.add(Objects.requireNonNull(tag, "tag")); }

    public boolean removeTag(Tag tag) { return tags.remove(tag); }


    public void changeStatus(Status newStatus) {
        if (newStatus == null) throw new ValidationException("New status must not be null");
        if (!this.status.canTransitionTo(newStatus)) {
            throw new ValidationException("Illegal transition from " + this.status + " to " + newStatus);
        }

        this.status = newStatus;

        if (newStatus == Status.DONE) {
            this.completedAt = LocalDateTime.now();
        } else {
            this.completedAt = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Task other && id.equals(other.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() {
        return "Task[" + id + ", " + title + ", " + status + "]";
    }

    public static final class Builder {
        private final String id;
        private final String userId;
        private final String title;
        private LocalDateTime createdAt = LocalDateTime.now();
        private String description;
        private LocalDate dueDate;
        private Priority priority = Priority.MEDIUM;
        private Status status = Status.TODO;
        private String categoryId;
        private final Set<Tag> tags = new HashSet<>();
        private LocalDateTime completedAt; // NEW

        private Builder(String id, String userId, String title) {
            this.id = id;
            this.userId = userId;
            this.title = title;
        }

        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder dueDate(LocalDate v) { this.dueDate = v; return this; }
        public Builder status(Status v) { this.status = v; return this; }
        public Builder priority(Priority v) { this.priority = v; return this; }
        public Builder categoryId(String v) { this.categoryId = v; return this; }
        public Builder addTag(Tag v) { this.tags.add(v); return this; }
        public Builder completedAt(LocalDateTime v) { this.completedAt = v; return this; } // NEW

        public Task build() {
            Task task = new Task(id, userId, title, createdAt);
            task.describe(description);
            task.reschedule(dueDate);
            task.reprioritize(priority);
            task.categorize(categoryId);
            task.tags.addAll(tags);
            if (status != Status.TODO) {
                task.changeStatus(status);
            }
            if (this.completedAt != null) {
                task.completedAt = this.completedAt; // Restore exact timestamp
            }
            return task;
        }
    }
}