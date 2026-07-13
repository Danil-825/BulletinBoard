package com.bulletinboard.BulletinBoard.common.exceptions;

import com.bulletinboard.BulletinBoard.user.api.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException e) {
        notFound(e, "User");
        ErrorResponse errorResponse = new ErrorResponse("User not found", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ObjectAlreadyExistsException.class)
    public ResponseEntity<Object> handleObjectAlreadyExistsException(ObjectAlreadyExistsException e) {
        log.warn("Object already exists: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Object already exists", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> errors
                .put(error.getField(), error.getDefaultMessage()));
        log.warn("Validation errors: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    private void notFound(Exception e, String objectName) {
        log.warn("{} not found: {}", objectName, e.getMessage());
    }

}
