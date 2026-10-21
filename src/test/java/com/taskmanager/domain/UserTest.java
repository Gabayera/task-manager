package com.taskmanager.domain;

import com.taskmanager.exception.ValidationException;
import com.taskmanager.testframework.Assert;
import com.taskmanager.testframework.TestCase;

public final class UserTest extends TestCase {

    public void testValidUserCreation() {
        User user = new User("U1", "alice", "hash", "salt");
        Assert.assertEquals("U1", user.getId());
        Assert.assertEquals("alice", user.getUsername());
        Assert.assertNotNull(user.getCreatedAt());
    }

    public void testInvalidUserCreation() {
        Assert.assertThrows(ValidationException.class, () -> new User(null, "alice", "h", "s"));
        Assert.assertThrows(ValidationException.class, () -> new User("U1", "   ", "h", "s"));
        Assert.assertThrows(ValidationException.class, () -> new User("U1", "alice", null, "s"));
        Assert.assertThrows(ValidationException.class, () -> new User("U1", "alice", "h", ""));
    }

    public void testEquality() {
        User u1 = new User("U1", "alice", "h1", "s1");
        User u2 = new User("U1", "bob", "h2", "s2");
        User u3 = new User("U2", "alice", "h1", "s1");

        Assert.assertEquals(u1, u2); // Same ID = equal
        Assert.assertNotEquals(u1, u3); // Different ID = not equal
    }
}