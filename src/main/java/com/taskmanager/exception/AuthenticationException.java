package com.taskmanager.exception;

public class AuthenticationException extends TaskManagerException {

    private static final long serialVersionUID = 1L;

    public AuthenticationException() {
        super("Invalid credentials.");
    }

}