package com.taskmanager.domain;

import com.taskmanager.exception.ValidationException;
import com.taskmanager.testframework.Assert;
import com.taskmanager.testframework.TestCase;

/**
 * Tests status parsing, terminal-state behavior,
 * and every permitted transition.
 */
public final class StatusTest extends TestCase {

    /**
     * Tests exact, case-insensitive, hyphenated, and space-separated inputs.
     */
    public void testFromStringParsesNormalisedForms() {

        Assert.assertEquals(
                Status.TODO,
                Status.fromString("TODO")
        );

        Assert.assertEquals(
                Status.DONE,
                Status.fromString("done")
        );

        Assert.assertEquals(
                Status.IN_PROGRESS,
                Status.fromString("in-progress")
        );

        Assert.assertEquals(
                Status.IN_PROGRESS,
                Status.fromString("IN PROGRESS")
        );

        Assert.assertEquals(
                Status.CANCELLED,
                Status.fromString("  cancelled ")
        );
    }

    /**
     * Tests rejection of invalid status text.
     */
    public void testFromStringRejectsInvalidValues() {

        for (String raw : new String[]{
                null,
                "",
                "   ",
                "BLOCKED"
        }) {

            ValidationException exception = Assert.assertThrows(
                    ValidationException.class,
                    () -> Status.fromString(raw)
            );

            Assert.assertTrue(
                    exception.getMessage().contains(
                            "TODO, IN_PROGRESS, DONE, CANCELLED"
                    )
            );
        }
    }

    /**
     * Exhaustively checks all sixteen status-to-status transitions.
     */
    public void testTransitionMatrixIsExact() {

        for (Status from : Status.values()) {

            for (Status to : Status.values()) {

                boolean expected = switch (from) {

                    case TODO ->
                            to == Status.IN_PROGRESS
                            || to == Status.DONE
                            || to == Status.CANCELLED;

                    case IN_PROGRESS ->
                            to == Status.DONE
                            || to == Status.CANCELLED;

                    case DONE, CANCELLED -> false;
                };

                Assert.assertEquals(
                        expected,
                        from.canTransitionTo(to)
                );
            }
        }

        Assert.assertFalse(
                Status.TODO.canTransitionTo(null)
        );
    }

    /**
     * Tests that only DONE and CANCELLED are terminal.
     */
    public void testOnlyDoneAndCancelledAreTerminal() {

        Assert.assertFalse(Status.TODO.isTerminal());

        Assert.assertFalse(Status.IN_PROGRESS.isTerminal());

        Assert.assertTrue(Status.DONE.isTerminal());

        Assert.assertTrue(Status.CANCELLED.isTerminal());
    }

    /**
     * Tests that same-state transitions are rejected.
     */
    public void testSameStateTransitionsAreRejected() {

        for (Status status : Status.values()) {

            Assert.assertFalse(
                    status.canTransitionTo(status)
            );
        }
    }
}