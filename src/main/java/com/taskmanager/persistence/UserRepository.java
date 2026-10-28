package com.taskmanager.persistence;

import com.taskmanager.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    Optional<User> findByUsername(String username);

    void save(User user);
}