package com.taskmanager.service;

import com.taskmanager.domain.Priority;
import com.taskmanager.domain.Status;
import com.taskmanager.domain.Tag;
import com.taskmanager.domain.Task;
import com.taskmanager.exception.ValidationException;
import com.taskmanager.persistence.CategoryRepository;
import com.taskmanager.persistence.TaskRepository;
import com.taskmanager.util.IdGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskService {
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    public TaskService(TaskRepository taskRepository, CategoryRepository categoryRepository) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Task> getTasks(String userId) {
        return taskRepository.findAllByUserId(userId);
    }

    public Task getTask(String taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new ValidationException("Task not found: " + taskId));
    }

    public Task createTask(String userId, String title, String description, LocalDate dueDate, Priority priority, String categoryId, List<Tag> tags) {

        validateCategory(userId, categoryId);

        List<Task> existingTasks = taskRepository.findAllByUserId(userId);
        List<String> existingIds = new ArrayList<>();

        for (Task task : existingTasks) {
            existingIds.add(task.getId());
        }
        String newId = IdGenerator.getNextId("T", existingIds);

        Task.Builder builder = Task.builder(newId, userId, title)
                .description(description)
                .dueDate(dueDate)
                .priority(priority)
                .categoryId(categoryId);

        if (tags != null) {
            for (Tag tag : tags) {
                builder.addTag(tag);
            }
        }

        Task task = builder.build();
        taskRepository.save(task);
        return task;
    }

    public Task updateTask(String taskId, String title, String description, LocalDate dueDate,
                           Priority priority, String categoryId, List<Tag> tags) {

        Task task = getTask(taskId);
        validateCategory(task.getUserId(), categoryId);

        if (title != null && !title.isBlank()) {
            task.rename(title);
        }
        task.describe(description);
        task.reschedule(dueDate);
        task.reprioritize(priority);
        task.categorize(categoryId);

        // Replace tags
        task.clearTags();
        if (tags != null) {
            for (Tag tag : tags) {
                task.addTag(tag);
            }
        }

        taskRepository.save(task);
        return task;
    }

    public void deleteTask(String taskId) {
        taskRepository.delete(taskId);
    }

    public Task changeStatus(String taskId, Status newStatus) {
        Task task = getTask(taskId);
        task.changeStatus(newStatus); // Throws ValidationException if illegal transition
        taskRepository.save(task);
        return task;
    }

    public Task markAsDone(String taskId) {
        return changeStatus(taskId, Status.DONE);
    }

    private void validateCategory(String userId, String categoryId) {
        if (categoryId != null && !categoryId.isBlank()) {
            boolean exists = categoryRepository.findAllByUserId(userId).stream()
                    .anyMatch(c -> c.getId().equals(categoryId));
            if (!exists) {
                throw new ValidationException("Category not found or does not belong to you");
            }
        }
    }
}