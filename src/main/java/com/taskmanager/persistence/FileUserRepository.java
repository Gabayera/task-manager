package com.taskmanager.persistence;

import com.taskmanager.domain.User;
import com.taskmanager.exception.StorageException;
import com.taskmanager.util.FileStorage;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileUserRepository implements UserRepository {
    private final Path path;
    private final List<User> users = new ArrayList<>();

    public FileUserRepository(Path path) {
        this.path = path;
        load();
    }

    private void load() {
        try {
            List<String> lines = FileStorage.readLines(path);
            if (lines.isEmpty()) return;

            for (int i = 1; i < lines.size(); i++) { // Skip header
                String line = lines.get(i);
                try {
                    String[] parts = line.split("\\|", -1);
                    if (parts.length != 5) {
                        System.err.println("Skipping malformed user line: " + line);
                        continue;
                    }
                    users.add(new User(
                            FileStorage.unescape(parts[0]),
                            FileStorage.unescape(parts[1]),
                            FileStorage.unescape(parts[2]),
                            FileStorage.unescape(parts[3]),
                            LocalDateTime.parse(parts[4])
                    ));
                } catch (Exception e) {
                    System.err.println("Error parsing user line: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new StorageException("Failed to load users", e);
        }
    }

    private void saveToFile() {
        List<String> lines = new ArrayList<>();
        lines.add("# v1");
        for (User u : users) {
            lines.add(String.join("|",
                    FileStorage.escape(u.getId()),
                    FileStorage.escape(u.getUsername()),
                    FileStorage.escape(u.getPasswordHash()),
                    FileStorage.escape(u.getSalt()),
                    u.getCreatedAt().toString()
            ));
        }
        try {
            FileStorage.writeLines(path, lines);
        } catch (IOException e) {
            throw new StorageException("Failed to save users", e);
        }
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
    }

    @Override
    public void save(User user) {
        users.removeIf(u -> u.getId().equals(user.getId()) || u.getUsername().equals(user.getUsername()));
        users.add(user);
        saveToFile();
    }
}