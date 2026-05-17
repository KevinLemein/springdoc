/**
 * Represents a standardized response for batch processing operations.
 * This record provides a summary of the batch execution, including the total number of files processed,
 * how many succeeded, how many failed, and a detailed list of results for each file.
 *
 * @param totalFiles The total number of files submitted or processed in the batch.
 * @param succeeded The count of files that were processed successfully within the batch.
 * @param failed The count of files that failed during processing within the batch.
 * @param results A list of {@link BatchFileResult} objects, each detailing the outcome for an individual file in the batch.
 */
package com.quavo.springdoc_ai.dto;

import java.util.List;

public record BatchResponse(
        int totalFiles,
        int succeeded,
        int failed,
        List<BatchFileResult> results
)
{}