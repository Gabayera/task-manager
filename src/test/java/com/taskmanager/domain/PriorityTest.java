package com.taskmanager.domain;

import com.taskmanager.exception.ValidationException;
import com.taskmanager.testframework.Assert;
import com.taskmanager.testframework.TestCase;

/**
 * Tests parsing and ordering invariants for Priority.
 */
public final class PriorityTest extends TestCase {

    /**
     * Tests exact, lowercase, mixed-case, and whitespace-padded values.
     */
    public void testFromStringAcceptsCaseAndWhitespace() {

        Assert.assertEquals(
                Priority.LOW,
                Priority.fromString("LOW"));

        Assert.assertEquals(
                Priority.MEDIUM,
                Priority.fromString("medium"));

        Assert.assertEquals(
                Priority.HIGH,
                Priority.fromString("hIgH"));

        Assert.assertEquals(
                Priority.URGENT,
                Priority.fromString("  UrGeNt  "));
    }

    /**
     * Tests rejection of null, blank, and unknown inputs.
     */
    public void testFromStringRejectsInvalidValues() {

        assertInvalid(null);
        assertInvalid("");
        assertInvalid("   ");
        assertInvalid("CRITICAL");
    }

    /**
     * Tests that declared order and explicit weights increase together.
     */
    public void testWeightsIncreaseInDeclarationOrder() {

        Priority[] priorities = Priority.values();

        for (int index = 1; index < priorities.length; index++) {

            Assert.assertTrue(
                    priorities[index - 1]
                            .compareTo(priorities[index]) < 0);

            Assert.assertTrue(
                    priorities[index - 1].getWeight() < priorities[index].getWeight());
        }

        Assert.assertEquals(1, Priority.LOW.getWeight());
        Assert.assertEquals(4, Priority.URGENT.getWeight());
    }

    /**
     * Checks that invalid input throws ValidationException.
     *
     * @param raw supplied input
     */
    private void assertInvalid(String raw) {

        ValidationException exception = Assert.assertThrows(
                ValidationException.class,
                () -> Priority.fromString(raw));

        Assert.assertTrue(
                exception.getMessage()
                        .contains("LOW, MEDIUM, HIGH, URGENT"));
    }
}