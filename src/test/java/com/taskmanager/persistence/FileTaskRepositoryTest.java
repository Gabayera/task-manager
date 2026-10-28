package com.taskmanager.persistence;

import com.taskmanager.domain.*;
import com.taskmanager.testframework.Assert;
import com.taskmanager.testframework.TestCase;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class FileTaskRepositoryTest extends TestCase {

    private Path tempFile;
    private FileTaskRepository repository;

    @Override
    protected void setUp() throws Exception {
        tempFile = Files.createTempFile("tasks_test", ".dat");
        repository = new FileTaskRepository(tempFile);
    }

    @Override
    protected void tearDown() throws Exception {
        Files.deleteIfExists(tempFile);
    }

    public void testSaveAndLoadTasksWithFullData() {
        Task t = Task.builder("T2001", "U1001", "Write SRS")
                .description("Chapter 1-8")
                .dueDate(LocalDate.of(2025, 2, 1))
                .priority(Priority.HIGH)
                .categoryId("C3001")
                .addTag(new Tag("design"))
                .addTag(new Tag("srs"))
                .createdAt(LocalDateTime.of(2025, 1, 16, 10, 0))
                .build();

        repository.save(t);

        FileTaskRepository reloaded = new FileTaskRepository(tempFile);
        List<Task> tasks = reloaded.findAllByUserId("U1001");

        Assert.assertEquals(1, tasks.size());
        Task loaded = tasks.get(0);

        Assert.assertEquals("T2001", loaded.getId());
        Assert.assertEquals("Write SRS", loaded.getTitle());
        Assert.assertEquals("Chapter 1-8", loaded.getDescription().orElse(null));
        Assert.assertEquals(LocalDate.of(2025, 2, 1), loaded.getDueDate().orElse(null));
        Assert.assertEquals(Priority.HIGH, loaded.getPriority());
        Assert.assertEquals(Status.TODO, loaded.getStatus());
        Assert.assertEquals("C3001", loaded.getCategoryId().orElse(null));
        Assert.assertEquals(2, loaded.getTags().size());
        Assert.assertTrue(loaded.getTags().contains(new Tag("design")));
    }

    public void testSaveAndLoadTaskWithEmptyFields() {
        Task t = Task.create("T2002", "U1001", "Simple Task");
        repository.save(t);

        FileTaskRepository reloaded = new FileTaskRepository(tempFile);
        Task loaded = reloaded.findAllByUserId("U1001").get(0);

        Assert.assertTrue(loaded.getDescription().isEmpty());
        Assert.assertTrue(loaded.getDueDate().isEmpty());
        Assert.assertTrue(loaded.getCategoryId().isEmpty());
        Assert.assertTrue(loaded.getTags().isEmpty());
    }

    public void testDeleteTask() {
        Task t = Task.create("T2001", "U1001", "Delete me");
        repository.save(t);

        repository.delete("T2001");

        FileTaskRepository reloaded = new FileTaskRepository(tempFile);
        Assert.assertTrue(reloaded.findAllByUserId("U1001").isEmpty());
    }
}