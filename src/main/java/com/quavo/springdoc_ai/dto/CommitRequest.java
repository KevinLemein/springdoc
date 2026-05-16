package com.quavo.springdoc_ai.dto;

public class CommitRequest {

    private String diff;
    private String branch;
    private String recentCommits;

    public String getDiff() { return diff; }

    public void setDiff(String diff) { this.diff = diff; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getRecentCommits() { return recentCommits; }
    public void setRecentCommits(String recentCommits) { this.recentCommits = recentCommits; }
}
