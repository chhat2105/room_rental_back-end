package com.chhatkh.dev.exception;

import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    public record ApiError(int status, String message, LocalDateTime timestamp) {}

    private ResponseEntity<ApiError> build(HttpStatus s, String msg) {
        return ResponseEntity.status(s).body(new ApiError(s.value(), msg, LocalDateTime.now()));
    }
    @ExceptionHandler(ResourceNotFoundException.class) ResponseEntity<ApiError> notFound(ResourceNotFoundException e) { return build(HttpStatus.NOT_FOUND, e.getMessage()); }
    @ExceptionHandler(BadRequestException.class) ResponseEntity<ApiError> bad(BadRequestException e) { return build(HttpStatus.BAD_REQUEST, e.getMessage()); }
    @ExceptionHandler(ForbiddenException.class) ResponseEntity<ApiError> forbidden(ForbiddenException e) { return build(HttpStatus.FORBIDDEN, e.getMessage()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> invalid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map((FieldError f) -> f.getField() + ": " + f.getDefaultMessage()).collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, msg);
    }
}
