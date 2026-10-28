package com.taskmanager.persistence;

import com.taskmanager.domain.Category;
import com.taskmanager.exception.StorageException;
import com.taskmanager.util.FileStorage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileCategoryRepository implements CategoryRepository {
    private final Path path;
    private final List<Category> categories = new ArrayList<>();

    public FileCategoryRepository(Path path) {
        this.path = path;
        load();
    }

    private void load() {
        try {
            List<String> lines = FileStorage.readLines(path);
            if (lines.isEmpty()) return;

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                try {
                    String[] parts = line.split("\\|", -1);
                    if (parts.length != 4) {
                        System.err.println("Skipping malformed category line: " + line);
                        continue;
                    }
                    categories.add(new Category(
                            FileStorage.unescape(parts[0]),
                            FileStorage.unescape(parts[1]),
                            FileStorage.unescape(parts[2]),
                            FileStorage.unescape(parts[3])
                    ));
                } catch (Exception e) {
                    System.err.println("Error parsing category line: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new StorageException("Failed to load categories", e);
        }
    }

    private void saveToFile() {
        List<String> lines = new ArrayList<>();
        lines.add("# v1");
        for (Category c : categories) {
            lines.add(String.join("|",
                    FileStorage.escape(c.getId()),
                    FileStorage.escape(c.getUserId()),
                    FileStorage.escape(c.getName()),
                    FileStorage.escape(c.getColor())
            ));
        }
        try {
            FileStorage.writeLines(path, lines);
        } catch (IOException e) {
            throw new StorageException("Failed to save categories", e);
        }
    }

    @Override
    public List<Category> findAllByUserId(String userId) {
        return categories.stream().filter(c -> c.getUserId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public List<Category> findAll() {
        return List.of();
    }

    @Override
    public void save(Category category) {
        categories.removeIf(c -> c.getId().equals(category.getId()));
        categories.add(category);
        saveToFile();
    }

    @Override
    public void delete(Category category) {

    }

    @Override
    public void delete(String id) {
        categories.removeIf(c -> c.getId().equals(id));
        saveToFile();
    }
}