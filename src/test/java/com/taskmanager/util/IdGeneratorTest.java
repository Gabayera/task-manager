package com.taskmanager.util;

import com.taskmanager.exception.ValidationException;
import com.taskmanager.testframework.Assert;
import com.taskmanager.testframework.TestCase;
import java.util.List;

public final class IdGeneratorTest extends TestCase {

    public void testGenerateId() {
        Assert.assertEquals("T1005", IdGenerator.generateId("T", 5L));
        Assert.assertEquals("U1001", IdGenerator.generateId("U", 1L));
    }

    public void testExtractNumber() {
        Assert.assertEquals(2001L, IdGenerator.extractNumber("T2001"));
        Assert.assertEquals(1001L, IdGenerator.extractNumber("U1001"));
        Assert.assertThrows(ValidationException.class, () -> IdGenerator.extractNumber("INVALID"));
    }

    public void testGetNextId() {
        Assert.assertEquals("U1001", IdGenerator.getNextId("U", List.of()));
        Assert.assertEquals("U1006", IdGenerator.getNextId("U", List.of("U1001", "U1005", "U1003")));
        Assert.assertEquals("T2002", IdGenerator.getNextId("T", List.of("T2001")));
    }
}