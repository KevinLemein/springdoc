package com.quavo.springdoc_ai.dto;

/**
 * Represents the response from a documentation writing operation,
 * providing details about the files processed and the outcome.
 * This record encapsulates information such as the path to the modified file,
 * the path to its backup, and a count of methods that were successfully documented.
 *
 * @param filePath          The absolute or relative path to the Java source file that was modified and documented.
 * @param backupPath        The absolute or relative path to the backup copy of the original Java source file
 *                          before any modifications were applied. This is null if no backup was created.
 * @param methodsDocumented The number of methods within the {@code filePath} that had Javadoc added or updated.
 */
public record WriteResponse (

    String filePath,
    String backupPath,
    int methodsDocumented
    )
{}
