package com.taskmanager.service;

import com.taskmanager.domain.Status;
import com.taskmanager.domain.Task;
import com.taskmanager.exception.ValidationException;
import com.taskmanager.persistence.TaskRepository;
import com.taskmanager.testframework.Assert;
import com.taskmanager.testframework.TestCase;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Unit tests for {@link ReminderService} (FR-14, UC-06).
 *
 * <p>Scenarios follow {@code docs/features/reminders.feature}: "today" is
 * fixed to 2025-02-10 with a {@link Clock}, and tasks are kept in an
 * in-memory repository so the tests do not touch the file system.</p>
 */
public final class ReminderServiceTest extends TestCase {

    private static final String USER = "U1001";
    private static final LocalDate TODAY = LocalDate.of(2025, 2, 10);

    private InMemoryTaskRepository repository;
    private ReminderService reminderService;

    @Override
    protected void setUp() {
        repository = new InMemoryTaskRepository();
        Clock fixedClock = Clock.fixed(
                TODAY.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));
        reminderService = new ReminderService(repository, fixedClock);
    }

    // ---------------------------------------------------------------- valid

    /** Valid scenario: the example table of reminders.feature. */
    public void testGetRemindersReturnsOverdueAndDueSoonTasks() {
        repository.save(task("T1", USER, TODAY.minusDays(1), Status.TODO));        // overdue
        repository.save(task("T2", USER, TODAY, Status.TODO));                     // due today
        repository.save(task("T3", USER, TODAY.plusDays(1), Status.IN_PROGRESS)); // due tomorrow
        repository.save(task("T4", USER, TODAY.plusDays(2), Status.TODO));        // too far away
        repository.save(task("T5", USER, TODAY.minusDays(1), Status.DONE));        // finished
        repository.save(task("T6", USER, TODAY.minusDays(1), Status.CANCELLED));   // cancelled
        repository.save(task("T7", USER, null, Status.TODO));                      // no due date
        repository.save(task("T8", "U2002", TODAY, Status.TODO));                  // other user

        List<Task> reminders = reminderService.getReminders(USER);

        Assert.assertEquals(List.of("T1", "T2", "T3"), ids(reminders));
    }

    /** Overdue tasks are the ones shown with the "!" marker. */
    public void testIsOverdueOnlyForPastDueDates() {
        Assert.assertTrue(reminderService.isOverdue(task("T1", USER, TODAY.minusDays(1), Status.TODO)));
        Assert.assertFalse(reminderService.isOverdue(task("T2", USER, TODAY, Status.TODO)));
        Assert.assertFalse(reminderService.isOverdue(task("T3", USER, null, Status.TODO)));
    }

    /** "Nothing qualifies": the list is empty, not null. */
    public void testGetRemindersReturnsEmptyListWhenNothingIsDue() {
        repository.save(task("T1", USER, TODAY.plusDays(7), Status.TODO));

        Assert.assertTrue(reminderService.getReminders(USER).isEmpty());
    }

    /** "Reminders do not modify data": no task is saved or deleted. */
    public void testGetRemindersDoesNotModifyData() {
        repository.save(task("T1", USER, TODAY.minusDays(1), Status.TODO));
        int writesBefore = repository.writes;

        reminderService.getReminders(USER);

        Assert.assertEquals(writesBefore, repository.writes);
        Assert.assertEquals(Status.TODO, repository.findById("T1").orElseThrow().getStatus());
    }

    // -------------------------------------------------------------- invalid

    /** Error condition: a missing user id is rejected with ValidationException. */
    public void testGetRemindersRejectsBlankUserId() {
        Assert.assertThrows(ValidationException.class, () -> reminderService.getReminders(null));
        Assert.assertThrows(ValidationException.class, () -> reminderService.getReminders("   "));
    }

    /** Error condition: the service cannot be built without a repository. */
    public void testConstructorRejectsNullRepository() {
        Assert.assertThrows(NullPointerException.class, () -> new ReminderService(null));
    }

    // -------------------------------------------------------------- helpers

    private static Task task(String id, String userId, LocalDate due, Status status) {
        return Task.builder(id, userId, "Task " + id)
                .createdAt(LocalDateTime.of(2025, 1, 1, 9, 0))
                .dueDate(due)
                .status(status)
                .build();
    }

    private static List<String> ids(List<Task> tasks) {
        List<String> ids = new ArrayList<>();
        for (Task t : tasks) {
            ids.add(t.getId());
        }
        return ids;
    }

    /** Minimal in-memory TaskRepository that also counts write operations. */
    private static final class InMemoryTaskRepository implements TaskRepository {
        private final Map<String, Task> tasks = new LinkedHashMap<>();
        private int writes;

        @Override
        public List<Task> findAllByUserId(String userId) {
            return tasks.values().stream()
                    .filter(t -> t.getUserId().equals(userId))
                    .toList();
        }

        @Override
        public Optional<Task> findById(String id) {
            return Optional.ofNullable(tasks.get(id));
        }

        @Override
        public void save(Task task) {
            tasks.put(task.getId(), task);
            writes++;
        }

        @Override
        public void delete(String id) {
            tasks.remove(id);
            writes++;
        }
    }
}
