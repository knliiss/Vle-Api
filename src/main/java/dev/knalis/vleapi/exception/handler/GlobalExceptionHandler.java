package dev.knalis.vleapi.exception.handler;

import dev.knalis.vleapi.exception.custom.DuplicateEntityException;
import dev.knalis.vleapi.exception.custom.EntityNotFoundException;
import dev.knalis.vleapi.exception.custom.AlreadyBoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ProblemDetail base(HttpStatus status, String title, String detail, String type) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(title);
        pd.setDetail(detail);
        if (type != null) pd.setType(URI.create(type));
        pd.setProperty("timestamp", OffsetDateTime.now());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        String first = ex.getBindingResult().getAllErrors().isEmpty() ? "Invalid payload" : ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ProblemDetail pd = base(HttpStatus.BAD_REQUEST, "Validation failed", first, "https://http.dev/problems/validation-error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail pd = base(HttpStatus.FORBIDDEN, "Access denied", ex.getMessage(), "https://http.dev/problems/access-denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(pd);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail pd = base(HttpStatus.BAD_REQUEST, "Bad request", ex.getMessage(), "https://http.dev/problems/bad-request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(EntityNotFoundException ex) {
        ProblemDetail pd = base(HttpStatus.NOT_FOUND, "Not found", ex.getMessage(), "https://http.dev/problems/not-found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ProblemDetail> handleDuplicate(DuplicateEntityException ex) {
        ProblemDetail pd = base(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), "https://http.dev/problems/conflict");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    @ExceptionHandler(AlreadyBoundException.class)
    public ResponseEntity<ProblemDetail> handleAlreadyBound(AlreadyBoundException ex) {
        ProblemDetail pd = base(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), "https://http.dev/problems/conflict");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAny(Exception ex) {
        ProblemDetail pd = base(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", ex.getMessage() == null ? "Unexpected error occurred" : ex.getMessage(), "https://http.dev/problems/internal-error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pd);
    }
}
