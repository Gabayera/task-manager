package com.taskmanager.persistence;

import com.taskmanager.domain.User;
import com.taskmanager.testframework.Assert;
import com.taskmanager.testframework.TestCase;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public final class FileUserRepositoryTest extends TestCase {

    private Path tempFile;
    private FileUserRepository repository;

    @Override
    protected void setUp() throws Exception {
        tempFile = Files.createTempFile("users_test", ".dat");
        repository = new FileUserRepository(tempFile);
    }

    @Override
    protected void tearDown() throws Exception {
        Files.deleteIfExists(tempFile);
    }

    public void testSaveAndLoadUsers() {
        User u1 = new User("U1001", "alice", "hash1", "salt1", LocalDateTime.now());
        repository.save(u1);

        FileUserRepository reloaded = new FileUserRepository(tempFile);
        List<User> users = reloaded.findAll();

        Assert.assertEquals(1, users.size());
        Assert.assertEquals("alice", users.get(0).getUsername());
        Assert.assertEquals("hash1", users.get(0).getPasswordHash());
    }

    public void testFindByUsername() {
        User u1 = new User("U1001", "alice", "hash1", "salt1", LocalDateTime.now());
        User u2 = new User("U1002", "bob", "hash2", "salt2", LocalDateTime.now());
        repository.save(u1);
        repository.save(u2);

        Assert.assertTrue(repository.findByUsername("alice").isPresent());
        Assert.assertTrue(repository.findByUsername("bob").isPresent());
        Assert.assertTrue(repository.findByUsername("charlie").isEmpty());
    }

    public void testMalformedLinesAreSkipped() throws IOException {
        List<String> badLines = List.of(
                "# v1",
                "U1001|alice|hash1|salt1|2025-01-01T10:00:00",
                "BadLineWithNoPipes",
                "U1002|bob|hash2|salt2|INVALID_DATE"
        );
        Files.write(tempFile, badLines);

        FileUserRepository reloaded = new FileUserRepository(tempFile);
        List<User> users = reloaded.findAll();

        Assert.assertEquals(1, users.size());
        Assert.assertEquals("alice", users.get(0).getUsername());
    }
}