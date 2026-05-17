package com.quavo.springdoc_ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import com.quavo.springdoc_ai.dto.WriteResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Service responsible for interacting with an AI chat model to perform various documentation
 * and code-related assistance tasks. This includes generating Javadoc comments,
 * commit messages, and API documentation based on provided source code or diffs.
 */
@Service
public class AssistService {

    private final ChatClient chatClient;

    /**
     * Constructs an {@code AssistService} with a {@link ChatClient}.
     * The {@link ChatClient.Builder} is typically auto-configured by Spring AI
     * based on application properties, allowing for easy integration with various
     * AI models.
     *
     * @param builder The builder used to create the {@link ChatClient} instance.
     */
    public AssistService(ChatClient.Builder builder) {
        // Build the ChatClient from the provided builder, which is auto-configured by Spring AI.
        this.chatClient = builder.build();
    }

    /**
     * Generates comprehensive Javadoc and inline comments for a given Java source code string
     * using an AI model.
     * The AI is instructed to act as a senior Java/Spring Boot engineer and adhere to
     * production-grade documentation standards.
     *
     * @param sourceCode The Java source code content to be documented.
     * @param fileName The name of the file (e.g., "MyService.java"), used to provide context to the AI.
     *                 Can be {@code null} if no specific filename context is available.
     * @return A string containing the documented Java source code.
     */
    public String generateComments(String sourceCode, String fileName) {
        // Define the system prompt to instruct the AI on its role and output format.
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

        // Define the user prompt, including the source code and optional filename for context.
        String userPrompt = String.format(
                "Add comprehensive Javadoc and inline comments to this Java file%s:\n\n%s",
                fileName != null ? " (" + fileName + ")" : "",
                sourceCode
        );

        // Send the prompts to the AI model and retrieve the generated content.
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
    }

    /**
     * Reads a Java source code file from the specified path and generates comprehensive Javadoc
     * and inline comments for it using an AI model.
     *
     * @param filePath The absolute or relative path to the Java source file.
     * @return A string containing the documented Java source code.
     * @throws IllegalArgumentException If the file does not exist or is not a .java file.
     * @throws RuntimeException If an {@link IOException} occurs while reading the file.
     */
    public String generateCommentsFromFile(String filePath) {
        Path path = Path.of(filePath);

        // Ensure the file exists before attempting to read it.
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File not found: " + filePath);
        }
        // Ensure only Java files are processed to avoid unexpected AI behavior or errors.
        if (!filePath.endsWith(".java")) {
            throw new IllegalArgumentException("Only .java files are supported: " + filePath);
        }

        String sourceCode;
        try {
            // Read the entire content of the Java file into a string.
            sourceCode = Files.readString(path);
        } catch (IOException e) {
            // Wrap IOException in a RuntimeException for easier handling in service layer.
            throw new RuntimeException("Failed to read file: " + filePath, e);
        }

        // Delegate to the core generation logic, passing the filename for better AI context.
        return generateComments(sourceCode, path.getFileName().toString());
    }

    /**
     * Generates a Git commit message using an AI model based on a provided diff,
     * current branch name, and recent commit messages for stylistic context.
     * The AI is instructed to follow Conventional Commits specification.
     *
     * @param diff The Git diff output representing the staged changes.
     * @param branch The name of the current Git branch, used for context.
     * @param recentCommits A string containing recent commit messages, used by the AI
     *                      to match the existing commit style. Can be {@code null} or empty.
     * @return A string containing the generated Conventional Commit message.
     * @throws IllegalArgumentException If the provided diff is {@code null} or blank.
     */
    public String generateCommitMessage(String diff, String branch, String recentCommits) {
        // Enforce that a diff must be provided to generate a meaningful commit message.
        if (diff == null || diff.isBlank()) {
            throw new IllegalArgumentException(
                    "Diff is empty. Stage your changes first with: git add <files>"
            );
        }

        // Define the system prompt, instructing the AI on commit message format and rules.
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

        // Define the user prompt, providing the diff, branch, and recent commits for AI context.
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

        // Send the prompts to the AI model and retrieve the generated commit message, stripping whitespace.
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content()
                .strip();
    }

    /**
     * Generates REST API documentation in Markdown format for a given Spring Boot controller
     * Java file using an AI model.
     * The AI is instructed to extract endpoint details, request/response examples,
     * and security information.
     *
     * @param filePath The absolute or relative path to the Spring Boot controller Java file.
     * @return A string containing the generated REST API documentation in Markdown format.
     * @throws IllegalArgumentException If the file does not exist or is not a .java file.
     * @throws RuntimeException If an {@link IOException} occurs while reading the file.
     */
    public String generateApiDocs(String filePath) {
        Path path = Path.of(filePath);

        // Validate that the file exists before proceeding.
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File not found: " + filePath);
        }
        // Validate that the file is a Java source file.
        if (!filePath.endsWith(".java")) {
            throw new IllegalArgumentException("Only .java files are supported: " + filePath);
        }

        String sourceCode;
        try {
            // Read the entire content of the controller file.
            sourceCode = Files.readString(path);
        } catch (IOException e) {
            // Propagate file reading errors as runtime exceptions.
            throw new RuntimeException("Failed to read file: " + filePath, e);
        }

        // Define the system prompt for generating API documentation.
        String systemPrompt = """
            You are a senior engineer creating REST API documentation for a Spring Boot service.
            Output clean Markdown only — no preamble, no meta-commentary, no explanations.

            For each endpoint include:
            - HTTP method + full path (combine class-level @RequestMapping with method-level mapping)
            - One line description of what it does
            - Request headers table: Authorization, Content-Type where applicable
            - Path/query parameters table: | Name | Type | Required | Description |
            - Request body: show a realistic JSON example
            - Response: show a realistic JSON example matching the actual return type
            - Status codes: list all realistic ones (200, 201, 400, 401, 403, 404, 500)

            Use H2 (##) for the controller name, H3 (###) for each endpoint.
            If you see @PreAuthorize or security annotations, note the required role.
            If a method has no request body, omit that section entirely.
            """;

        // Define the user prompt, including the filename and source code of the controller.
        String userPrompt = String.format("""
            Generate REST API documentation for this Spring Boot controller.

            File: %s

            Source:
            %s
            """,
                path.getFileName().toString(),
                sourceCode
        );

        // Send the prompts to the AI model and retrieve the generated Markdown documentation.
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content()
                .strip();
    }

    /**
     * Reads a Java source code file, generates Javadoc and inline comments for it,
     * overwrites the original file with the documented version, and creates a backup
     * of the original file.
     *
     * @param filePath The absolute or relative path to the Java source file.
     * @return A {@link WriteResponse} object containing the path to the original file,
     *         the path to the backup file, and the count of methods documented.
     * @throws IllegalArgumentException If the file does not exist or is not a .java file.
     * @throws RuntimeException If an {@link IOException} occurs during file operations
     *                          (read, write, backup).
     */
    public WriteResponse writeComments(String filePath) {
        Path path = Path.of(filePath);

        // Pre-validate file existence and type.
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File not found: " + filePath);
        }
        if (!filePath.endsWith(".java")) {
            throw new IllegalArgumentException("Only .java files are supported: " + filePath);
        }

        String original;
        try {
            // Read the original content of the file.
            original = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + filePath, e);
        }

        // Create a backup of the original file before making any modifications.
        Path backupPath = Path.of(filePath + ".bak");
        try {
            Files.writeString(backupPath, original);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create backup: " + backupPath, e);
        }

        // Generate the documented version of the source code using the AI.
        String documented = generateComments(original, path.getFileName().toString());

        // Write the newly documented content back to the original file path.
        try {
            Files.writeString(path, documented);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write documented file: " + filePath, e);
        }

        // Count the number of Javadoc blocks added to provide feedback.
        int methodsDocumented = countJavadocBlocks(documented);

        return new WriteResponse(filePath, backupPath.toString(), methodsDocumented);
    }

    /**
     * Counts the number of Javadoc blocks ({@code /** ... * /}) within a given source code string.
     * It subtracts one to account for the typical class-level Javadoc block, aiming to return
     * the count of method-level Javadocs.
     *
     * @param source The source code string to analyze.
     * @return The estimated number of method-level Javadoc blocks found.
     */
    private int countJavadocBlocks(String source) {
        int count = 0;
        int index = 0;
        // Iterate through the source string to find all occurrences of "/**".
        while ((index = source.indexOf("/**", index)) != -1) {
            count++;
            index += 3; // Move past the found "/**" to continue searching.
        }
        // Subtract 1 from the total count to exclude the class-level Javadoc,
        // as the intention is usually to count method-specific documentation.
        return Math.max(0, count - 1);
    }
}