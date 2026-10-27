package com.taskmanager.testframework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Base class for tests run by the project's JDK-only test harness.
 *
 * <p>Test methods must be {@code public}, accept no arguments,
 * return {@code void}, and have names beginning with {@code test}.</p>
 */
public abstract class TestCase {

    protected void setUp() {
        // Optional per-test setup hook.
    }

    protected void tearDown() {
        // Optional per-test cleanup hook.
    }

    /**
     * Discovers and executes test methods in name-sorted order.
     *
     * @param result result accumulator to update
     */
    public final void runAll(TestResult result) {
        if (result == null) {
            throw new IllegalArgumentException("result must not be null");
        }

        List<Method> methods = Arrays.stream(getClass().getMethods())
                .filter(m -> m.getName().startsWith("test"))
                .filter(m -> m.getParameterCount() == 0)
                .filter(m -> m.getReturnType() == void.class)
                .sorted(Comparator.comparing(Method::getName))
                .toList();

        for (Method method : methods) {
            String testName = getClass().getSimpleName()
                    + "." + method.getName();
            Throwable failure = null;

            try {
                setUp();
                method.invoke(this);
            } catch (Throwable thrown) {
                failure = unwrap(thrown);
            } finally {
                try {
                    tearDown();
                } catch (Throwable thrown) {
                    Throwable cleanup = unwrap(thrown);
                    if (failure == null) {
                        failure = cleanup;
                    } else if (failure != cleanup) {
                        failure.addSuppressed(cleanup);
                    }
                } 
            }

            if (failure == null) {
                result.recordPass(testName);
            } else {
                result.recordFailure(testName, failure.getMessage(), failure);
            }
        }
    }

    private static Throwable unwrap(Throwable thrown) {
        if (thrown instanceof InvocationTargetException inv
                && inv.getCause() != null) {
            return inv.getCause();
        }
        return thrown;
    }
}