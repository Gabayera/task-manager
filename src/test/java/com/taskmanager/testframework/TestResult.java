package com.taskmanager.testframework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collects pass and failure information for one test run.
 */
public final class TestResult {

    private int passed;
    private final List<String> passedTestNames = new ArrayList<>();
    private final List<Failure> failures = new ArrayList<>();

    public void recordPass(String testName) {
        passed++;
        passedTestNames.add(testName);
    }

    public void recordFailure(String testName, String message, Throwable throwable) {
        failures.add(new Failure(testName, message, throwable));
    }

    public int getPassed() {
        return passed;
    }

    public List<String> getPassedTestNames() {
        return Collections.unmodifiableList(passedTestNames);
    }

    public List<Failure> getFailures() {
        return Collections.unmodifiableList(failures);
    }

    public int getTotal() {
        return passed + failures.size();
    }

    public static final class Failure {

        private final String testName;
        private final String message;
        private final Throwable throwable;

        public Failure(String testName, String message, Throwable throwable) {
            this.testName = testName;
            this.message = message;
            this.throwable = throwable;
        }

        public String getTestName() {
            return testName;
        }

        public String getMessage() {
            return message;
        }

        public Throwable getThrowable() {
            return throwable;
        }
    }
}