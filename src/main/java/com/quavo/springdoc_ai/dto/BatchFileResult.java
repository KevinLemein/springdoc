/**
 * Represents the result of processing a single batch file.
 * This record encapsulates the outcome, including the file path, success status,
 * count of methods documented (if applicable), and any error messages.
 *
 * @param filePath The absolute or relative path to the batch file that was processed.
 * @param success  A boolean indicating whether the processing of the file was successful.
 *                 {@code true} if successful, {@code false} otherwise.
 * @param methodDocumented An integer representing the number of methods that were
 *                         successfully documented within the processed file.
 *                         This count is relevant only if {@code success} is {@code true}.
 * @param error    A string containing an error message if the processing was not successful.
 *                 This field will be {@code null} or empty if {@code success} is {@code true}.
 */
package com.quavo.springdoc_ai.dto;

public record BatchFileResult(

        String filePath,
        boolean success,
        int methodsDocumented,
        String error
) {
}

