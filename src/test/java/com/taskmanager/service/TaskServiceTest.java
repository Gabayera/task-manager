package com.taskmanager.service;

import com.taskmanager.domain.*;
import com.taskmanager.exception.ValidationException;
import com.taskmanager.persistence.FileCategoryRepository;
import com.taskmanager.persistence.FileTaskRepository;
import com.taskmanager.testframework.Assert;
import com.taskmanager.testframework.TestCase;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public final class TaskServiceTest extends TestCase {

    private Path tempTaskFile;
    private Path tempCatFile;
    private TaskService taskService;
    private CategoryService categoryService;

    @Override
    protected void setUp() throws Exception {
        tempTaskFile = Files.createTempFile("tasks_test", ".dat");
        tempCatFile = Files.createTempFile("cats_test", ".dat");

        FileTaskRepository taskRepo = new FileTaskRepository(tempTaskFile);
        FileCategoryRepository catRepo = new FileCategoryRepository(tempCatFile);

        taskService = new TaskService(taskRepo, catRepo);
        categoryService = new CategoryService(catRepo);
    }

    @Override
    protected void tearDown() throws Exception {
        Files.deleteIfExists(tempTaskFile);
        Files.deleteIfExists(tempCatFile);
    }

    public void testCreateAndGetTask() {
        Task task = taskService.createTask("U1001", "Write SRS", "Chapter 1-8",
                LocalDate.of(2025, 2, 1), Priority.HIGH, null, List.of(new Tag("design")));

        Assert.assertNotNull(task.getId());
        Assert.assertEquals("Write SRS", task.getTitle());

        List<Task> tasks = taskService.getTasks("U1001");
        Assert.assertEquals(1, tasks.size());
        Assert.assertEquals("Write SRS", tasks.get(0).getTitle());
    }

    public void testCreateTaskWithValidCategory() {
        Category cat = categoryService.createCategory("U1001", "University", "#fff");

        Task task = taskService.createTask("U1001", "Homework", null,
                null, Priority.MEDIUM, cat.getId(), List.of());

        Assert.assertEquals(cat.getId(), task.getCategoryId().orElse(null));
    }

    public void testCreateTaskWithInvalidCategory() {
        Assert.assertThrows(ValidationException.class,
                () -> taskService.createTask("U1001", "Invalid Cat", null,
                        null, Priority.MEDIUM, "C9999", List.of()));
    }

    public void testUpdateTask() {
        Task task = taskService.createTask("U1001", "Old Title", "Old Desc",
                null, Priority.LOW, null, List.of());

        Task updated = taskService.updateTask(task.getId(), "New Title", "New Desc",
                LocalDate.now().plusDays(5), Priority.URGENT, null, List.of(new Tag("newtag")));

        Assert.assertEquals("New Title", updated.getTitle());
        Assert.assertEquals("New Desc", updated.getDescription().orElse(null));
        Assert.assertEquals(Priority.URGENT, updated.getPriority());
        Assert.assertEquals(1, updated.getTags().size());
        Assert.assertTrue(updated.getTags().contains(new Tag("newtag")));
    }

    public void testChangeStatus() {
        Task task = taskService.createTask("U1001", "Status Test", null,
                null, Priority.MEDIUM, null, List.of());

        // TODO -> IN_PROGRESS
        taskService.changeStatus(task.getId(), Status.IN_PROGRESS);
        Assert.assertEquals(Status.IN_PROGRESS, taskService.getTask(task.getId()).getStatus());

        // IN_PROGRESS -> DONE
        taskService.markAsDone(task.getId());
        Task doneTask = taskService.getTask(task.getId());
        Assert.assertEquals(Status.DONE, doneTask.getStatus());
        Assert.assertTrue(doneTask.getCompletedAt().isPresent());
    }

    public void testIllegalStatusTransition() {
        Task task = taskService.createTask("U1001", "Illegal Test", null,
                null, Priority.MEDIUM, null, List.of());

        taskService.markAsDone(task.getId());

        // DONE -> TODO is illegal
        Assert.assertThrows(ValidationException.class,
                () -> taskService.changeStatus(task.getId(), Status.TODO));
    }

    public void testDeleteTask() {
        Task task = taskService.createTask("U1001", "Delete Me", null,
                null, Priority.MEDIUM, null, List.of());

        taskService.deleteTask(task.getId());

        Assert.assertTrue(taskService.getTasks("U1001").isEmpty());
    }
}