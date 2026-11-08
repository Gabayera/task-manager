package com.taskmanager.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

public class FileStorage {

    public static List<String> readLines(Path path) throws IOException {
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    public static void writeLines(Path path, List<String> lines) throws IOException {
        ensureParentExists(path);

        // Create a temporary file in the same directory
        Path tempPath = path.resolveSibling(path.getFileName() + ".tmp");

        Files.write(tempPath, lines, StandardCharsets.UTF_8);

        Files.move(tempPath, path,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
    }

    public static String escape(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("|", "\\p");
    }

    public static String unescape(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\p", "|");
    }

    public static void ensureParentExists(Path path) throws IOException {
        Path parent = path.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }
    }
}
