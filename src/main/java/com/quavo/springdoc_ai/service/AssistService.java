package com.quavo.springdoc_ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class AssistService {

    private final ChatClient chatClient;

    // Spring AI auto-configures the ChatClient.Builder bean from your application.properties
    public AssistService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String generateComments(String sourceCode, String fileName) {
        String systemPrompt = """
                You are a senior Java/Spring Boot engineer writing production-grade documentation.
                Your output is ONLY the documented Java source code — no explanations, no markdown
                fences, no preamble, no ```java blocks. Return raw Java only.
                Rules:
                - Add Javadoc to every public class and method that lacks one
                - Add @param, @return, @throws where appropriate
                - For Spring REST endpoints: mention HTTP verb, path, and return value
                - Keep inline comments short — explain WHY, not WHAT
                - Do NOT modify any logic, only add documentation
                """;

        String userPrompt = String.format(
                "Add comprehensive Javadoc and inline comments to this Java file%s:\n\n%s",
                fileName != null ? " (" + fileName + ")" : "",
                sourceCode
        );

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
    }

    public String generateCommentsFromFile(String filePath) {
        Path path = Path.of(filePath);

        // Validate the file exists and is actually a .java file
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File not found: " + filePath);
        }
        if (!filePath.endsWith(".java")) {
            throw new IllegalArgumentException("Only .java files are supported: " + filePath);
        }

        String sourceCode;
        try {
            sourceCode = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + filePath, e);
        }

        // Reuse the same prompt logic, passing the filename for context
        return generateComments(sourceCode, path.getFileName().toString());
    }

    public String generateCommitMessage(String diff, String branch, String recentCommits) {
        if (diff == null || diff.isBlank()) {
            throw new IllegalArgumentException(
                    "Diff is empty. Stage your changes first with: git add <files>"
            );
        }

        String systemPrompt = """
            You are a senior engineer writing Git commit messages.
            Output ONLY the commit message — no explanation, no markdown, no preamble.
            Follow Conventional Commits (https://www.conventionalcommits.org):

            Format:
              <type>(<scope>): <short description>

              [optional body — explain WHY, not WHAT. Wrap at 72 chars.]

              [optional footer: BREAKING CHANGE, Fixes #123, etc.]

            Types: feat | fix | refactor | perf | docs | test | chore | build | ci | style
            Scope: the module or class affected (e.g. UserService, auth, payment)
            Description: imperative mood, lowercase, no period ("add" not "adds" or "added")

            Rules:
            - Subject line MUST be under 72 characters
            - Body only when the WHY is not obvious from the diff
            - Be specific — never write "various changes" or "misc updates"
            - BREAKING CHANGE footer when public API changes
            """;

        String userPrompt = String.format("""
            Generate a Conventional Commit message for this staged diff.

            Branch: %s
            %s
            Diff:
            %s
            """,
                branch != null ? branch : "unknown",
                recentCommits != null && !recentCommits.isBlank()
                        ? "Recent commits (match this style):\n" + recentCommits
                        : "",
                diff
        );

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content()
                .strip();
    }
}