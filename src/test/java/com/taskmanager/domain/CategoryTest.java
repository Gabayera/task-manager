package com.taskmanager.domain;

import com.taskmanager.exception.ValidationException;
import com.taskmanager.testframework.Assert;
import com.taskmanager.testframework.TestCase;

public final class CategoryTest extends TestCase {

    public void testValidCategoryCreation() {
        // Uses the new 3-argument constructor
        Category category = new Category("C1", "U1", " University ");
        Assert.assertEquals("C1", category.getId());
        Assert.assertEquals("U1", category.getUserId());
        Assert.assertEquals("University", category.getName());
        Assert.assertEquals("#3366cc", category.getColor()); // Asserts the default color
    }

    public void testInvalidCategoryCreation() {
        Assert.assertThrows(ValidationException.class, () -> new Category(null, "U1", "Name"));
        Assert.assertThrows(ValidationException.class, () -> new Category("C1", "", "Name"));
        Assert.assertThrows(ValidationException.class, () -> new Category("C1", "U1", "   "));
    }

    public void testEquality() {
        Category c1 = new Category("C1", "U1", "Work");
        Category c2 = new Category("C1", "U2", "Personal");
        Category c3 = new Category("C2", "U1", "Work");

        Assert.assertEquals(c1, c2); // Same ID = equal
        Assert.assertNotEquals(c1, c3); // Different ID = not equal
    }
}