package com.taskmanager.persistence;

import com.taskmanager.domain.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    List<Task> findAllByUserId(String userId);

    Optional<Task> findById(String id);

    void save(Task task);

    void delete(String id);
}