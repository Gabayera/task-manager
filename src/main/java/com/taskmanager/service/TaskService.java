package com.taskmanager.service;

import com.taskmanager.domain.Task;
import com.taskmanager.exception.ValidationException;
import com.taskmanager.persistence.CategoryRepository;
import com.taskmanager.persistence.TaskRepository;

public class TaskService {
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    public TaskService(TaskRepository taskRepository, CategoryRepository categoryRepository) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
    }

    public Task getTask(String taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new ValidationException("Task not found: " + taskId));
    }

    
}
