package com.taskmanager.testframework;

/**
 * Self-tests for assertion helpers and the reflection-based test framework.
 */
public final class TestFrameworkTest extends TestCase {

    public void testAssertThrowsReturnsExpectedException() {
        IllegalArgumentException actual = Assert.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    throw new IllegalArgumentException("expected");
                });
        Assert.assertEquals("expected", actual.getMessage());
    }

    public void testAssertThrowsFailsWhenNothingIsThrown() {
        Assert.assertThrows(
                AssertionError.class,
                () -> Assert.assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            /* no throw */ }));
    }

    public void testAssertThrowsFailsForWrongExceptionType() {
        AssertionError error = Assert.assertThrows(
                AssertionError.class,
                () -> Assert.assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            throw new IllegalStateException("wrong");
                        }));
        Assert.assertTrue(error.getMessage()
                .contains(IllegalArgumentException.class.getName()));
        Assert.assertTrue(error.getMessage()
                .contains(IllegalStateException.class.getName()));
    }

    public void testAssertionsFailOnMismatch() {
        Assert.assertThrows(AssertionError.class,
                () -> Assert.assertEquals("expected", "actual"));
        Assert.assertThrows(AssertionError.class,
                () -> Assert.assertNotEquals(1, 1));
        Assert.assertThrows(AssertionError.class,
                () -> Assert.assertTrue(false));
        Assert.assertThrows(AssertionError.class,
                () -> Assert.assertFalse(true));
        Assert.assertThrows(AssertionError.class,
                () -> Assert.assertNull("not null"));
        Assert.assertThrows(AssertionError.class,
                () -> Assert.assertNotNull(null));
        Assert.assertThrows(AssertionError.class,
                () -> Assert.assertSame(new Object(), new Object()));
        Assert.assertThrows(AssertionError.class,
                () -> Assert.fail("manual failure"));
    }

    public void testAssertionsPassOnMatchingValues() {
        Object marker = new Object();
        Assert.assertEquals("same", "same");
        Assert.assertNotEquals("a", "b");
        Assert.assertTrue(true);
        Assert.assertFalse(false);
        Assert.assertNull(null);
        Assert.assertNotNull(marker);
        Assert.assertSame(marker, marker);
    }

    public void testRunnerRecordsPassAndFailure() {
        TinyTest tiny = new TinyTest();
        TestResult result = new TestResult();
        tiny.runAll(result);

        Assert.assertEquals(1, result.getPassed());
        Assert.assertEquals(1, result.getFailures().size());
        Assert.assertEquals(2, result.getTotal());
        Assert.assertTrue(result.getFailures().get(0)
                .getTestName().contains("testFails"));
        Assert.assertTrue(result.getFailures().get(0)
                .getThrowable() instanceof AssertionError);
    }

    private static final class TinyTest extends TestCase {

        @SuppressWarnings("unused")
        public void testPasses() {
            Assert.assertTrue(true);
        }

        @SuppressWarnings("unused")
        public void testFails() {
            Assert.fail("intentional self-test failure");
        }

        @SuppressWarnings("unused")
        public void testMethodWithArguments(String ignored) {
            Assert.fail("methods with arguments must not be discovered");
        }

        @SuppressWarnings("unused")
        public String testNonVoidMethod() {
            return "must not be discovered";
        }
    }
}