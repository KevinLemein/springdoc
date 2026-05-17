/**
 * Represents a request for AI assistance, typically used to send source code for analysis,
 * documentation generation, or other AI-driven processing.
 * This record acts as a Data Transfer Object (DTO) for incoming requests to AI assistance endpoints.
 *
 * @param sourceCode The actual source code content for which assistance is requested.
 *                   This field is mandatory and cannot be empty or null, ensuring that there's always
 *                   code to process.
 * @param fileName   An optional name of the file corresponding to the {@code sourceCode}.
 *                   This can be useful for context, language detection, or when saving results.
 */
package com.quavo.springdoc_ai.dto;

import jakarta.validation.constraints.NotBlank;

public record AssistRequest(
        @NotBlank(message = "Source code cannot be blank") // Enforces that the source code content must not be null, empty, or whitespace-only.
        String sourceCode,
        String fileName
) {}