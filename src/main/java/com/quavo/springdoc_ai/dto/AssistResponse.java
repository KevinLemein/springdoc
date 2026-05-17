package com.quavo.springdoc_ai.dto;

import java.time.Instant;

/**
 * A standardized record for API responses, providing a consistent structure for both
 * successful operations and error scenarios. This record acts as a universal wrapper
 * for data returned from REST endpoints, encapsulating status, messages, data payload,
 * error details, a timestamp, and a request identifier.
 * <p>
 * This design promotes uniformity across various API endpoints, simplifying client-side
 * parsing and error handling. When used as a return type for Spring REST controllers,
 * it ensures a predictable JSON structure for all responses (e.g., GET /api/resource,
 * POST /api/resource, etc.).
 * </p>
 *
 * @param success   {@code true} if the operation was successful, {@code false} otherwise.
 * @param message   A human-readable message providing context about the response,
 *                  e.g., "User created successfully" or "Validation failed".
 * @param data      The actual payload of the response for successful operations. This can be
 *                  any Java object representing the requested resource or result. It will be
 *                  {@code null} in case of an error.
 * @param error     A detailed error message or code in case of an unsuccessful operation.
 *                  This will be {@code null} for successful responses.
 * @param timestamp The exact instant (UTC) when this response was generated.
 * @param requestId A unique identifier for the request that generated this response,
 *                  useful for tracing and logging across systems.
 */
public record AssistResponse(
        boolean success,
        String message,
        Object data,
        String error,
        Instant timestamp,
        String requestId
) {
    /**
     * Creates a new {@code AssistResponse} instance for a successful operation.
     * This factory method simplifies the construction of positive API responses,
     * automatically setting {@code success} to {@code true},
     * {@code error} to {@code null}, and {@code timestamp} to the current instant.
     *
     * @param message   A human-readable message describing the successful outcome.
     * @param data      The payload object containing the result of the successful operation.
     *                  This can be any serializable Java object.
     * @param requestId The unique identifier for the original request.
     * @return A new {@link AssistResponse} instance representing a successful API call.
     */
    // Static factory methods provide a clear, intention-revealing way to construct responses,
    // effectively replacing the need for a @Builder annotation on the record itself.
    public static AssistResponse success(String message, Object data, String requestId) {
        return new AssistResponse(true, message, data, null, Instant.now(), requestId);
    }

    /**
     * Creates a new {@code AssistResponse} instance for an unsuccessful (error) operation.
     * This factory method simplifies the construction of error API responses,
     * automatically setting {@code success} to {@code false},
     * {@code data} to {@code null}, and {@code timestamp} to the current instant.
     *
     * @param message   A human-readable message providing context about the error,
     *                  e.g., "Failed to process request".
     * @param error     A detailed error description or code, often more technical than the message,
     *                  e.g., "INVALID_INPUT_FORMAT" or "User not found".
     * @param requestId The unique identifier for the original request.
     * @return A new {@link AssistResponse} instance representing a failed API call.
     */
    public static AssistResponse error(String message, String error, String requestId) {
        return new AssistResponse(false, message, null, error, Instant.now(), requestId);
    }
}