package com.taskmanager.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import com.taskmanager.exception.ValidationException;

public class User {

    private final String id;
    private final String username;
    private final String passwordHash;
    private final String salt;
    private final LocalDateTime createdAt;

    public User(String id, String username, String passwordHash, String salt, LocalDateTime createdAt) {
        if (id == null || id.isBlank()) throw new ValidationException("User id must not be blank");
        if (username == null || username.isBlank()) throw new ValidationException("Username must not be blank");
        if (passwordHash == null || passwordHash.isBlank()) throw new ValidationException("Password hash must not be blank");
        if (salt == null || salt.isBlank()) throw new ValidationException("Salt must not be blank");
        
        this.id = id;
        this.username = username.trim();
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.createdAt = (createdAt == null) ? LocalDateTime.now() : createdAt;
    }

    public User(String id, String username, String passwordHash, String salt) {
        this(id, username, passwordHash, salt, LocalDateTime.now());
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getSalt() { return salt; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override 
    public boolean equals(Object o) {
        if (this == o) 
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
