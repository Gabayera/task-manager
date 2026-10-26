package com.taskmanager.util;

import com.taskmanager.testframework.Assert;
import com.taskmanager.testframework.TestCase;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class FileStorageTest extends TestCase {

    public void testEscapeAndUnescape() {
        Assert.assertEquals("a\\pb", FileStorage.escape("a|b"));
        Assert.assertEquals("a|b", FileStorage.unescape("a\\pb"));
        Assert.assertEquals("", FileStorage.escape(null));
        Assert.assertEquals("", FileStorage.unescape(null));
    }

    public void testWriteAndReadLines() throws IOException {
        Path tempFile = Files.createTempFile("task-manager-test", ".txt");
        List<String> lines = List.of("line1", "line2|escaped", "line3");

        FileStorage.writeLines(tempFile, lines);
        List<String> readLines = FileStorage.readLines(tempFile);

        Assert.assertEquals(lines, readLines);

        Files.deleteIfExists(tempFile);
    }

    public void testReadNonExistentFileReturnsEmpty() throws IOException {
        Path nonExistent = Path.of("does_not_exist_12345.txt");
        Assert.assertTrue(FileStorage.readLines(nonExistent).isEmpty());
    }
}