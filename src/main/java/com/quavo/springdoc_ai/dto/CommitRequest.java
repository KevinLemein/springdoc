package com.quavo.springdoc_ai.dto;

/**
 * Represents a request to commit changes to a version control system.
 * This record encapsulates all necessary information for a commit operation,
 * including the actual changes, the target branch, and context from recent commits.
 *
 * @param diff The string representation of the changes to be committed.
 *             This typically includes additions, deletions, and modifications in a unified diff format.
 * @param branch The name of the target branch to which the changes should be committed.
 *               For example, "main", "develop", or a feature branch like "feature/my-new-feature".
 * @param recentCommits A string containing information about recent commits on the target branch.
 *                      This can be used for context, conflict detection, or to inform the commit message generation.
 *                      For example, a list of commit hashes and messages.
 */
public record CommitRequest(String diff, String branch, String recentCommits) {}