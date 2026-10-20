package com.taskmanager.exception;

public class ValidationException extends TaskManagerException {

    private static final long serialVersionUID = 1L;

    private final String field;

    public ValidationException(String msg) {
        super(msg);
        this.field = null;
    }

    public ValidationException(String field, String msg) {
        super(msg);
        this.field = field;
    }

    public String getField() {
        return field;
    }

} 