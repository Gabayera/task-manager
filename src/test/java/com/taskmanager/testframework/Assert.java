package com.taskmanager.testframework;

import java.util.Objects;

/**
 * Static assertion helpers that throw AssertionError when a check fails.
 */
public final class Assert {

    private Assert() {
        // Utility class.
    }

    public static void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(
                    "Expected <" + expected + "> but was <" + actual + ">");
        }
    }

    public static void assertNotEquals(Object expected, Object actual) {
        if (Objects.equals(expected, actual)) {
            throw new AssertionError(
                    "Expected values to differ, but both were <" + actual + ">");
        }
    }

    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected <true> but was <false>");
        }
    }

    public static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected <false> but was <true>");
        }
    }

    public static void assertNull(Object actual) {
        if (actual != null) {
            throw new AssertionError(
                    "Expected <null> but was <" + actual + ">");
        }
    }

    public static void assertNotNull(Object actual) {
        if (actual == null) {
            throw new AssertionError(
                    "Expected a non-null value but was <null>");
        }
    }

    public static void assertSame(Object expected, Object actual) {
        if (expected != actual) {
            throw new AssertionError(
                    "Expected the same object <" + expected
                            + "> but was <" + actual + ">");
        }
    }

    public static void fail(String message) {
        throw new AssertionError(message);
    }

    /**
     * Asserts that an action throws an exception of the requested type.
     *
     * @param type   expected exception class
     * @param action action expected to throw
     * @param <T>    expected throwable type
     * @return the caught throwable, cast to the expected type
     */
    public static <T extends Throwable> T assertThrows(
            Class<T> type, Runnable action) {

        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (action == null) {
            throw new IllegalArgumentException("action must not be null");
        }

        try {
            action.run();
        } catch (Throwable thrown) {
            if (type.isInstance(thrown)) {
                return type.cast(thrown);
            }
            throw new AssertionError(
                    "Expected exception <" + type.getName()
                            + "> but caught <"
                            + thrown.getClass().getName() + ">",
                    thrown);
        }

        throw new AssertionError(
                "Expected exception <" + type.getName()
                        + "> but nothing was thrown");
    }
}