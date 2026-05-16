package com.quavo.springdoc_ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

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
}