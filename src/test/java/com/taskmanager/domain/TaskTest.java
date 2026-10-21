package com.taskmanager.domain;

import com.taskmanager.exception.ValidationException;
import com.taskmanager.testframework.Assert;
import com.taskmanager.testframework.TestCase;
import java.time.LocalDate;

public final class TaskTest extends TestCase {

    public void testValidTaskCreation() {
        Task task = Task.create("T1", "U1", "  Write SRS  ");
        Assert.assertEquals("T1", task.getId());
        Assert.assertEquals("U1", task.getUserId());
        Assert.assertEquals("Write SRS", task.getTitle());
        Assert.assertEquals(Status.TODO, task.getStatus());
        Assert.assertEquals(Priority.MEDIUM, task.getPriority());
        Assert.assertNull(task.getCompletedAt().orElse(null));
        Assert.assertTrue(task.getTags().isEmpty());
    }

    public void testInvalidTaskCreation() {
        Assert.assertThrows(ValidationException.class, () -> Task.create(null, "U1", "Title"));
        Assert.assertThrows(ValidationException.class, () -> Task.create("T1", "", "Title"));
        Assert.assertThrows(ValidationException.class, () -> Task.create("T1", "U1", "   "));
    }

    public void testStatusTransitionAndCompletedAtInvariant() {
        Task task = Task.create("T1", "U1", "Test Task");
        
        // TODO -> IN_PROGRESS (completedAt must remain null)
        task.changeStatus(Status.IN_PROGRESS);
        Assert.assertEquals(Status.IN_PROGRESS, task.getStatus());
        Assert.assertNull(task.getCompletedAt().orElse(null));

        // IN_PROGRESS -> DONE (completedAt must be set)
        task.changeStatus(Status.DONE);
        Assert.assertEquals(Status.DONE, task.getStatus());
        Assert.assertNotNull(task.getCompletedAt().orElse(null));
    }

    public void testIllegalStatusTransition() {
        Task task = Task.create("T1", "U1", "Test Task");
        
        // Cannot jump directly from TODO to CANCELLED? Wait, we allowed that in Status.canTransitionTo.
        // But DONE -> TODO is illegal.
        task.changeStatus(Status.DONE);
        Assert.assertThrows(ValidationException.class, () -> task.changeStatus(Status.TODO));
    }

    public void testReschedule() {
        Task task = Task.create("T1", "U1", "Test Task");
        LocalDate today = LocalDate.now();
        
        task.reschedule(today.plusDays(1));
        Assert.assertEquals(today.plusDays(1), task.getDueDate().orElse(null));

        // Should throw if date is before creation date
        Assert.assertThrows(ValidationException.class, () -> task.reschedule(today.minusDays(1)));
    }

    public void testBuilder() {
        Task task = Task.builder("T2", "U1", "Build Test")
                .description("Detailed description")
                .priority(Priority.HIGH)
                .dueDate(LocalDate.now().plusDays(5))
                .addTag(new Tag("test"))
                .build();

        Assert.assertEquals("T2", task.getId());
        Assert.assertEquals("Detailed description", task.getDescription().orElse(""));
        Assert.assertEquals(Priority.HIGH, task.getPriority());
        Assert.assertEquals(1, task.getTags().size());
        Assert.assertTrue(task.getTags().contains(new Tag("test")));
    }
}