package com.taskmanager.persistence;

import com.taskmanager.domain.*;
import com.taskmanager.exception.StorageException;
import com.taskmanager.util.FileStorage;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileTaskRepository implements TaskRepository {
    private final Path path;
    private final List<Task> tasks = new ArrayList<>();

    public FileTaskRepository(Path path) {
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
                    if (parts.length != 11) {
                        System.err.println("Skipping malformed task line: " + line);
                        continue;
                    }

                    Task.Builder builder = Task.builder(
                                    FileStorage.unescape(parts[0]), // id
                                    FileStorage.unescape(parts[1]), // userId
                                    FileStorage.unescape(parts[2])  // title
                            ).createdAt(LocalDateTime.parse(parts[9]))
                            .priority(Priority.valueOf(parts[5]));

                    String desc = parts[3];
                    if (!desc.isEmpty()) builder.description(FileStorage.unescape(desc));

                    String dueDate = parts[4];
                    if (!dueDate.isEmpty()) builder.dueDate(LocalDate.parse(dueDate));

                    String categoryId = parts[7];
                    if (!categoryId.isEmpty()) builder.categoryId(FileStorage.unescape(categoryId));

                    String tagsStr = parts[8];
                    if (!tagsStr.isEmpty()) {
                        for (String t : tagsStr.split(",")) {
                            if (!t.isEmpty()) builder.addTag(new Tag(FileStorage.unescape(t)));
                        }
                    }

                    String completedAt = parts[10];
                    if (!completedAt.isEmpty()) builder.completedAt(LocalDateTime.parse(completedAt));

                    builder.status(Status.valueOf(parts[6]));
                    tasks.add(builder.build());

                } catch (Exception e) {
                    System.err.println("Error parsing task line: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new StorageException("Failed to load tasks", e);
        }
    }

    private void saveToFile() {
        List<String> lines = new ArrayList<>();
        lines.add("# v1");
        for (Task t : tasks) {
            String tagsStr = t.getTags().stream()
                    .map(tag -> FileStorage.escape(tag.name()))
                    .collect(Collectors.joining(","));

            lines.add(String.join("|",
                    FileStorage.escape(t.getId()),
                    FileStorage.escape(t.getUserId()),
                    FileStorage.escape(t.getTitle()),
                    FileStorage.escape(t.getDescription().orElse("")),
                    t.getDueDate().map(LocalDate::toString).orElse(""),
                    t.getPriority().name(),
                    t.getStatus().name(),
                    FileStorage.escape(t.getCategoryId().orElse("")),
                    tagsStr,
                    t.getCreatedAt().toString(),
                    t.getCompletedAt().map(LocalDateTime::toString).orElse("")
            ));
        }
        try {
            FileStorage.writeLines(path, lines);
        } catch (IOException e) {
            throw new StorageException("Failed to save tasks", e);
        }
    }

    @Override
    public List<Task> findAllByUserId(String userId) {
        return tasks.stream().filter(t -> t.getUserId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public Optional<Task> findById(String id) {
        return tasks.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    @Override
    public void save(Task task) {
        tasks.removeIf(t -> t.getId().equals(task.getId()));
        tasks.add(task);
        saveToFile();
    }

    @Override
    public void delete(String id) {
        tasks.removeIf(t -> t.getId().equals(id));
        saveToFile();
    }
}