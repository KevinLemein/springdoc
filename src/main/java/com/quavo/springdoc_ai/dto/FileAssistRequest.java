package com.quavo.springdoc_ai.dto;

/**
 * Represents a request to assist with a file, typically involving operations
 * like reading, parsing, or analyzing its content.
 * This record serves as a data transfer object (DTO) for API requests
 * where a file path is the primary identifier for the operation.
 *
 * @param filePath The absolute or relative path to the file that needs assistance.
 *                 This path is crucial for locating and processing the target file.
 */
public record FileAssistRequest(String filePath) {}