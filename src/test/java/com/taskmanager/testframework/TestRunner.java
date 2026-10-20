package com.taskmanager.testframework;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Command-line entry point for the project's JDK-only test harness.
 */
public final class TestRunner {

    private TestRunner() {
        // Utility class.
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        List<Class<? extends TestCase>> classes = new ArrayList<>();

        if (args != null && args.length > 0) {
            for (String className : args) {
                try {
                    Class<?> loaded = Class.forName(className);
                    if (!TestCase.class.isAssignableFrom(loaded)) {
                        System.err.println(
                                "FAIL " + className + " (does not extend TestCase)");
                        System.exit(1);
                    }
                    classes.add((Class<? extends TestCase>) loaded);
                } catch (ClassNotFoundException ex) {
                    System.err.println("FAIL Could not load test class " + className);
                    ex.printStackTrace(System.err);
                    System.exit(1);
                }
            }
        } else {
            try {
                Class<?> suiteClass = Class.forName("com.taskmanager.AllTests");
                Method suiteMethod = suiteClass.getMethod("suite");
                List<Class<? extends TestCase>> suite =
                        (List<Class<? extends TestCase>>) suiteMethod.invoke(null);
                if (suite == null || suite.isEmpty()) {
                    System.err.println("FAIL AllTests.suite() returned no test classes");
                    System.exit(1);
                }
                classes.addAll(suite);
            } catch (ReflectiveOperationException | ClassCastException ex) {
                System.err.println("FAIL Could not load the registered test suite");
                ex.printStackTrace(System.err);
                System.exit(1);
            }
        }

        TestResult result = new TestResult();

        for (Class<? extends TestCase> testClass : classes) {
            try {
                Constructor<? extends TestCase> ctor = testClass.getConstructor();
                TestCase instance = ctor.newInstance();
                instance.runAll(result);
            } catch (Throwable thrown) {
                Throwable actual = thrown.getCause() != null
                        ? thrown.getCause() : thrown;
                result.recordFailure(
                        testClass.getSimpleName() + ".<construction>",
                        actual.getMessage(),
                        actual);
            }
        }

        for (String name : result.getPassedTestNames()) {
            System.out.println("PASS " + name);
        }
        for (TestResult.Failure f : result.getFailures()) {
            System.out.println("FAIL " + f.getTestName());
        }

        int failureCount = result.getFailures().size();
        System.out.println(result.getPassed() + " passed, "
                + failureCount + " failed, "
                + result.getTotal() + " total");

        if (failureCount > 0) {
            System.out.println("\nFailure details:");
            for (TestResult.Failure f : result.getFailures()) {
                System.out.println("\n--- " + f.getTestName()
                        + ": " + f.getMessage() + " ---");
                if (f.getThrowable() != null) {
                    f.getThrowable().printStackTrace(System.out);
                }
            }
        }

        System.exit(failureCount == 0 ? 0 : 1);
    }
}