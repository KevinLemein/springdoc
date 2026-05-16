package com.quavo.springdoc_ai.controller;

import com.quavo.springdoc_ai.service.AssistService;
import com.quavo.springdoc_ai.dto.*;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.quavo.springdoc_ai.dto.CommitRequest;
import com.quavo.springdoc_ai.dto.DocsRequest;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("api/assist")
public class AssistController {

    private final AssistService assistService;

    public AssistController(AssistService assistService) {
        this.assistService = assistService;
    }

    @PostMapping("/comment")
    public ResponseEntity<AssistResponse> comment(@Valid @RequestBody AssistRequest request) {
        String documented = assistService.generateComments(
                request.getSourceCode(),
                request.getFileName()
        );

        return ResponseEntity.ok(AssistResponse.builder()
                .success(true)
                .message("Documentation generated successfully")
                .data(documented)
                .timestamp(Instant.now())
                .requestId(UUID.randomUUID().toString())
                .build());
    }

    @PostMapping("/comment/file")
    public ResponseEntity<AssistResponse> commentFile(@RequestBody FileAssistRequest request) {
        String documented = assistService.generateCommentsFromFile(request.getFilePath());

        return ResponseEntity.ok(AssistResponse.builder()
                .success(true)
                .message("Documentation generated successfully")
                .data(documented)
                .timestamp(Instant.now())
                .requestId(UUID.randomUUID().toString())
                .build());
    }

    @PostMapping("/commit")
    public ResponseEntity<AssistResponse> commit(@RequestBody CommitRequest request) {
        String message = assistService.generateCommitMessage(
                request.getDiff(),
                request.getBranch(),
                request.getRecentCommits()
        );

        return ResponseEntity.ok(AssistResponse.builder()
                .success(true)
                .message("Commit message generated successfully")
                .data(message)
                .timestamp(Instant.now())
                .requestId(UUID.randomUUID().toString())
                .build());
    }

    @PostMapping("/docs")
    public ResponseEntity<AssistResponse> docs(@RequestBody DocsRequest request) {
        String markdown = assistService.generateApiDocs(request.getFilePath());

        return ResponseEntity.ok(AssistResponse.builder()
                .success(true)
                .message("API documentation generated successfully")
                .data(markdown)
                .timestamp(Instant.now())
                .requestId(UUID.randomUUID().toString())
                .build());
    }
}
