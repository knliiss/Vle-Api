package dev.knalis.vleapi.exception.custom;

public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String message) { super(message); }
    public DuplicateEntityException(String message, Throwable cause) { super(message, cause); }
}

