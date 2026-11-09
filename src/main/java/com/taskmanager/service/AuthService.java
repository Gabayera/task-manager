package com.taskmanager.service;

import com.taskmanager.domain.User;
import com.taskmanager.exception.AuthenticationException;
import com.taskmanager.exception.ValidationException;
import com.taskmanager.persistence.UserRepository;
import com.taskmanager.util.IdGenerator;
import com.taskmanager.util.PasswordHasher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;
    private User currentUser;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String password, String confirmPassword) {
        if (username == null || username.isBlank()) {
            throw new ValidationException("Username cannot be blank");
        }
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password cannot be less than 8 characters");
        }
        if (!password.equals(confirmPassword)) {
            throw new ValidationException("Password does not match");
        }

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            throw new ValidationException("Username already taken");
        }

        var allUsers = userRepository.findAll();
        List<String> existingIds = new ArrayList<>();
        for (User u : allUsers) {
            existingIds.add(u.getId());
        }

        String id = IdGenerator.getNextId("U", existingIds);

        String salt = PasswordHasher.generateSalt();
        String hashedPassword = PasswordHasher.hash(password, salt);

        User newUser = new User(id, username, hashedPassword, salt); // Fixed argument order
        userRepository.save(newUser);
        return newUser; // Don't auto-login on register
    }

    public User login(String username, String password) {
        if (username == null || username.isEmpty() || password == null) {
            throw new AuthenticationException("Invalid credentials");
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new AuthenticationException("Invalid credentials");
        }

        User user = userOpt.get();
        boolean passwordVerification = PasswordHasher.verify(password, user.getSalt(), user.getPasswordHash());
        if (!passwordVerification) {
            throw new AuthenticationException("Invalid credentials");
        }

        this.currentUser = user;
        return currentUser;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}