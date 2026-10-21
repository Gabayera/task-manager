package com.taskmanager.domain;

import com.taskmanager.exception.ValidationException;
import com.taskmanager.testframework.Assert;
import com.taskmanager.testframework.TestCase;

public final class TagTest extends TestCase {

    public void testValidTagCreationAndNormalisation() {
        Tag tag = new Tag("  Work  ");
        Assert.assertEquals("work", tag.name());
    }

    public void testInvalidTagCreation() {
        Assert.assertThrows(ValidationException.class, () -> new Tag(null));
        Assert.assertThrows(ValidationException.class, () -> new Tag(""));
        Assert.assertThrows(ValidationException.class, () -> new Tag("   "));
    }
}