package com.taskmanager.service;

import com.taskmanager.domain.Task;
import com.taskmanager.exception.ValidationException;
import com.taskmanager.persistence.TaskRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class ReminderService {

    private final TaskRepository taskRepository;
    private final Clock clock;

    /**
     * Creates a reminder service that uses the system clock.
     *
     * @param taskRepository source of the user's tasks
     */
    public ReminderService(TaskRepository taskRepository) {
        this(taskRepository, Clock.systemDefaultZone());
    }

    /**
     * Creates a reminder service with an explicit clock (used by tests).
     *
     * @param taskRepository source of the user's tasks
     * @param clock          clock that supplies "today"
     */
    public ReminderService(TaskRepository taskRepository, Clock clock) {
        this.taskRepository = Objects.requireNonNull(taskRepository, "taskRepository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    /**
     * Returns the tasks of {@code userId} that are overdue or due within the
     * next 24 hours and are not {@code DONE}/{@code CANCELLED}.
     *
     * @param userId id of the logged-in user
     * @return reminder tasks sorted by due date (oldest first); empty if none
     * @throws ValidationException if {@code userId} is null or blank
     */
    public List<Task> getReminders(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ValidationException("userId", "userId must not be blank");
        }

        LocalDate today = LocalDate.now(clock);
        LocalDate limit = today.plusDays(1);

        return taskRepository.findAllByUserId(userId).stream()
                .filter(task -> !task.getStatus().isTerminal())
                .filter(task -> task.getDueDate()
                        .map(due -> !due.isAfter(limit))
                        .orElse(false))
                .sorted(Comparator.comparing((Task task) -> task.getDueDate().get())
                        .thenComparing(Task::getId))
                .toList();
    }

    /**
     * Tells whether a task is overdue, i.e. its due date is before today.
     * The UI marks such tasks with {@code !} in the reminder list.
     *
     * @param task task to check
     * @return {@code true} if the task has a due date earlier than today
     * @throws NullPointerException if {@code task} is null
     */
    public boolean isOverdue(Task task) {
        Objects.requireNonNull(task, "task");
        LocalDate today = LocalDate.now(clock);
        return task.getDueDate().map(due -> due.isBefore(today)).orElse(false);
    }
}
