package com.quavo.springdoc_ai.dto;

import jakarta.validation.constraints.NotBlank;

public record AssistRequest(
        @NotBlank(message = "Source code cannot be blank")
        String sourceCode,
        String fileName
) {}