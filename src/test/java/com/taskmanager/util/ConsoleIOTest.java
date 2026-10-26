package com.taskmanager.util;

import com.taskmanager.testframework.Assert;
import com.taskmanager.testframework.TestCase;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public final class ConsoleIOTest extends TestCase {

    private ConsoleIO createIO(String input) {
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        return new ConsoleIO(in, new PrintStream(out));
    }

    public void testReadIntValid() {
        ConsoleIO io = createIO("42\n");
        Assert.assertEquals(42, io.readInt("Prompt: ", 1, 100));
    }

    public void testReadIntInvalidThenValid() {
        ConsoleIO io = createIO("abc\n200\n-5\n50\n");
        Assert.assertEquals(50, io.readInt("Prompt: ", 1, 100));
    }

    public void testReadDateAndEmpty() {
        ConsoleIO io = createIO("2025-05-01\n\n");
        Assert.assertEquals(LocalDate.of(2025, 5, 1), io.readDate("Date: "));
        Assert.assertNull(io.readDate("Date: "));
    }

    public void testReadOptionalString() {
        ConsoleIO io = createIO("hello\n\n");
        Assert.assertEquals("hello", io.readOptionalString("Prompt: "));
        Assert.assertNull(io.readOptionalString("Prompt: "));
    }
}