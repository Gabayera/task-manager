package com.taskmanager.exception;

public class StorageException extends TaskManagerException {

    private static final long serialVersionUID = 1L;

    public StorageException(String msg, Throwable cause) {
        super(msg, cause);
    }

}