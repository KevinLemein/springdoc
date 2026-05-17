package com.quavo.springdoc_ai.dto;

public record CommitRequest(String diff, String branch, String recentCommits) {}