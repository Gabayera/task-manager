package com.taskmanager.service;

import com.taskmanager.domain.User;
import com.taskmanager.exception.AuthenticationException;
import com.taskmanager.exception.ValidationException;
import com.taskmanager.persistence.FileUserRepository;
import com.taskmanager.testframework.Assert;
import com.taskmanager.testframework.TestCase;

import java.nio.file.Files;
import java.nio.file.Path;

public final class AuthServiceTest extends TestCase {

    private Path tempFile;
    private AuthService authService;

    @Override
    protected void setUp() throws Exception {
        tempFile = Files.createTempFile("auth_test", ".dat");
        authService = new AuthService(new FileUserRepository(tempFile));
    }

    @Override
    protected void tearDown() throws Exception {
        Files.deleteIfExists(tempFile);
    }

    public void testRegisterSuccess() {
        User user = authService.register("alice", "password123", "password123");
        Assert.assertNotNull(user.getId());
        Assert.assertEquals("alice", user.getUsername());
        Assert.assertFalse(authService.isLoggedIn()); // Register does not auto-login
    }

    public void testRegisterDuplicateUsername() {
        authService.register("alice", "password123", "password123");
        Assert.assertThrows(ValidationException.class,
                () -> authService.register("alice", "otherpass", "otherpass"));
    }

    public void testRegisterPasswordRules() {
        Assert.assertThrows(ValidationException.class,
                () -> authService.register("bob", "short", "short"));
        Assert.assertThrows(ValidationException.class,
                () -> authService.register("bob", "password123", "mismatch"));
    }

    public void testLoginAndLogout() {
        authService.register("alice", "password123", "password123");

        User loggedIn = authService.login("alice", "password123");
        Assert.assertNotNull(loggedIn);
        Assert.assertTrue(authService.isLoggedIn());
        Assert.assertEquals("alice", authService.getCurrentUser().getUsername());

        authService.logout();
        Assert.assertFalse(authService.isLoggedIn());
        Assert.assertNull(authService.getCurrentUser());
    }

    public void testLoginInvalidCredentials() {
        authService.register("alice", "password123", "password123");

        Assert.assertThrows(AuthenticationException.class,
                () -> authService.login("alice", "wrongpass"));
        Assert.assertThrows(AuthenticationException.class,
                () -> authService.login("unknown", "password123"));
    }
}