/**
 * Represents a request to process a batch operation, typically involving files within a specified directory.
 * This record is used as a Data Transfer Object (DTO) for incoming requests that require a directory path.
 *
 * @param directoryPath The absolute or relative path to the directory where the batch operation should be performed.
 *                        This path is crucial for locating the files or subdirectories to be processed.
 */
package com.quavo.springdoc_ai.dto;

public record BatchRequest(String directoryPath) {}