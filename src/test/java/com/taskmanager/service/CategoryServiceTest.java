package com.taskmanager.service;

import com.taskmanager.domain.Category;
import com.taskmanager.exception.ValidationException;
import com.taskmanager.persistence.FileCategoryRepository;
import com.taskmanager.testframework.Assert;
import com.taskmanager.testframework.TestCase;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class CategoryServiceTest extends TestCase {

    private Path tempFile;
    private CategoryService categoryService;

    @Override
    protected void setUp() throws Exception {
        tempFile = Files.createTempFile("cat_service_test", ".dat");
        categoryService = new CategoryService(new FileCategoryRepository(tempFile));
    }

    @Override
    protected void tearDown() throws Exception {
        Files.deleteIfExists(tempFile);
    }

    public void testCreateAndGetCategories() {
        categoryService.createCategory("U1001", "Work", "#ff0000");
        categoryService.createCategory("U1001", "Personal", "#00ff00");

        List<Category> cats = categoryService.getCategories("U1001");
        Assert.assertEquals(2, cats.size());
        Assert.assertEquals("Work", cats.get(0).getName());
    }

    public void testCreateDuplicateName() {
        categoryService.createCategory("U1001", "Work", null);
        Assert.assertThrows(ValidationException.class,
                () -> categoryService.createCategory("U1001", "work", null)); // Case insensitive
    }

    public void testRenameCategory() {
        Category cat = categoryService.createCategory("U1001", "Work", null);

        categoryService.renameCategory(cat.getId(), "Office");

        List<Category> cats = categoryService.getCategories("U1001");
        Assert.assertEquals("Office", cats.get(0).getName());
        Assert.assertEquals(cat.getId(), cats.get(0).getId()); // ID must not change
    }

    public void testDeleteCategory() {
        Category cat = categoryService.createCategory("U1001", "Work", null);
        categoryService.deleteCategory(cat.getId());

        Assert.assertTrue(categoryService.getCategories("U1001").isEmpty());
    }
}