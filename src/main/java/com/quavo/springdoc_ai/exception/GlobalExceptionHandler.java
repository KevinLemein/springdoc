package com.quavo.springdoc_ai.exception;

import com.quavo.springdoc_ai.dto.AssistResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AssistResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AssistResponse.error(
                        "Invalid request",
                        ex.getMessage(),
                        UUID.randomUUID().toString()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AssistResponse> handleValidation(MethodArgumentNotValidException ex) {
        String error = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AssistResponse.error(
                        "Validation error",
                        error,
                        UUID.randomUUID().toString()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AssistResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AssistResponse.error(
                        "Something went wrong",
                        ex.getMessage(),
                        UUID.randomUUID().toString()
                ));
    }
}