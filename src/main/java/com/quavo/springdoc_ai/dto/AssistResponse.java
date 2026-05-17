package com.quavo.springdoc_ai.dto;

import java.time.Instant;

public record AssistResponse(
        boolean success,
        String message,
        Object data,
        String error,
        Instant timestamp,
        String requestId
) {
    // Static factory replaces @Builde
    public static AssistResponse success(String message, Object data, String requestId) {
        return new AssistResponse(true, message, data, null, Instant.now(), requestId);
    }

    public static AssistResponse error(String message, String error, String requestId) {
        return new AssistResponse(false, message, null, error, Instant.now(), requestId);
    }
}