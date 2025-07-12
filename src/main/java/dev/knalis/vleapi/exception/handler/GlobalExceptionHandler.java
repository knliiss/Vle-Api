package dev.knalis.vleapi.exception.handler;

import dev.knalis.vleapi.exception.custom.UserNotHaveGroupException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleBadRequest(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid request payload"));
    }

    @ExceptionHandler(UserNotHaveGroupException.class)
    public ResponseEntity<?> handleUserNotHaveGroup(UserNotHaveGroupException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

}
