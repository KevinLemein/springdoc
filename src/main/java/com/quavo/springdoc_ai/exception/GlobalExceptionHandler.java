package com.quavo.springdoc_ai.exception;

import com.quavo.springdoc_ai.dto.AssistResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Bad input — file not found, not a .java file, blank source code
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AssistResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AssistResponse.builder()
                        .success(false)
                        .message("Invalid request")
                        .error(ex.getMessage())
                        .timestamp(Instant.now())
                        .requestId(UUID.randomUUID().toString())
                        .build());
    }

    // @Valid failures on request body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AssistResponse> handleValidation(MethodArgumentNotValidException ex) {
        String error = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AssistResponse.builder()
                        .success(false)
                        .message("Validation error")
                        .error(error)
                        .timestamp(Instant.now())
                        .requestId(UUID.randomUUID().toString())
                        .build());
    }

    // AI call failures, file read failures, anything unexpected
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AssistResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AssistResponse.builder()
                        .success(false)
                        .message("Something went wrong")
                        .error(ex.getMessage())
                        .timestamp(Instant.now())
                        .requestId(UUID.randomUUID().toString())
                        .build());
    }
}