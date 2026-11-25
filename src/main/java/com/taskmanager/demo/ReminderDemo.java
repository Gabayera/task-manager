package com.taskmanager.demo;

import com.taskmanager.domain.Priority;
import com.taskmanager.domain.Status;
import com.taskmanager.domain.Task;
import com.taskmanager.persistence.FileCategoryRepository;
import com.taskmanager.persistence.FileTaskRepository;
import com.taskmanager.service.ReminderService;
import com.taskmanager.service.TaskService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/**
 * Stand-alone demo of the reminder feature (FR-14, UC-06).
 *
 * <p>This is <b>not</b> the final application entry point (the
 * {@code ConsoleUI}/{@code Main} is planned for a later part). It creates a
 * few sample tasks in a temporary data directory using the real
 * {@link TaskService} and {@link FileTaskRepository}, then prints the
 * reminder list the way the console UI will: overdue tasks are marked
 * with {@code !}.</p>
 */
public final class ReminderDemo {

    private static final String USER = "U1001";

    private ReminderDemo() {
    }

    /**
     * Runs the demo.
     *
     * @param args ignored
     * @throws Exception if the temporary data directory cannot be created
     */
    public static void main(String[] args) throws Exception {
        Path dataDir = Files.createTempDirectory("task-manager-demo");
        FileTaskRepository taskRepo = new FileTaskRepository(dataDir.resolve("tasks.dat"));
        FileCategoryRepository catRepo = new FileCategoryRepository(dataDir.resolve("categories.dat"));
        TaskService taskService = new TaskService(taskRepo, catRepo);

        LocalDate today = LocalDate.now();
        taskService.createTask(USER, "Submit assignment 6", null, today,
                Priority.URGENT, null, List.of());
        taskService.createTask(USER, "Prepare presentation", null, today.plusDays(1),
                Priority.HIGH, null, List.of());
        taskService.createTask(USER, "Read chapter 5", null, today.plusDays(5),
                Priority.LOW, null, List.of());
        Task done = taskService.createTask(USER, "Register for exam", null, today,
                Priority.MEDIUM, null, List.of());
        taskService.markAsDone(done.getId());
        // An overdue task: the domain rejects a due date before creation,
        // so it is built with an earlier creation date and saved directly.
        taskRepo.save(Task.builder("T9001", USER, "Pay library fine")
                .createdAt(today.minusDays(10).atStartOfDay())
                .dueDate(today.minusDays(2))
                .priority(Priority.HIGH)
                .build());

        System.out.println("All tasks of " + USER + " (data: " + dataDir + "):");
        for (Task t : taskService.getTasks(USER)) {
            System.out.printf("  %-6s %-24s due %-10s %s%n", t.getId(), t.getTitle(),
                    t.getDueDate().map(LocalDate::toString).orElse("-"), t.getStatus());
        }

        ReminderService reminderService = new ReminderService(taskRepo);
        List<Task> reminders = reminderService.getReminders(USER);

        System.out.println();
        System.out.println("Reminders (overdue or due within 24 hours, today = " + today + "):");
        if (reminders.isEmpty()) {
            System.out.println("  (none)");
        }
        for (Task t : reminders) {
            String marker = reminderService.isOverdue(t) ? "!" : " ";
            System.out.printf("%s %-6s %-24s due %s  [%s]%n", marker, t.getId(), t.getTitle(),
                    t.getDueDate().orElseThrow(), t.getPriority());
        }
    }
}
