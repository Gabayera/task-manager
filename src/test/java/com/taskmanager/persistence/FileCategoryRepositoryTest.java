package com.taskmanager.persistence;

import com.taskmanager.domain.Category;
import com.taskmanager.testframework.Assert;
import com.taskmanager.testframework.TestCase;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class FileCategoryRepositoryTest extends TestCase {

    private Path tempFile;
    private FileCategoryRepository repository;

    @Override
    protected void setUp() throws Exception {
        tempFile = Files.createTempFile("categories_test", ".dat");
        repository = new FileCategoryRepository(tempFile);
    }

    @Override
    protected void tearDown() throws Exception {
        Files.deleteIfExists(tempFile);
    }

    public void testSaveAndLoadCategories() {
        Category c1 = new Category("C1001", "U1001", "University", "#3366cc");
        Category c2 = new Category("C1002", "U1001", "Work", "#ff0000");

        repository.save(c1);
        repository.save(c2);

        FileCategoryRepository reloaded = new FileCategoryRepository(tempFile);
        List<Category> loaded = reloaded.findAllByUserId("U1001");

        Assert.assertEquals(2, loaded.size());
        Assert.assertEquals("University", loaded.get(0).getName());
    }
}