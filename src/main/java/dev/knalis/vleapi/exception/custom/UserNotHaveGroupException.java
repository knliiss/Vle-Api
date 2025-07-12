package dev.knalis.vleapi.exception.custom;

public class UserNotHaveGroupException extends RuntimeException {

    public UserNotHaveGroupException() {
        super("User does not belong to any group.");
    }

    public UserNotHaveGroupException(String message) {
        super(message);
    }

    public UserNotHaveGroupException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotHaveGroupException(Throwable cause) {
        super(cause);
    }
}
