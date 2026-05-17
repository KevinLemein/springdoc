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
                request.sourceCode(),
                request.fileName()
        );

        return ResponseEntity.ok(
                AssistResponse.success("Documentation generated successfully", documented,
                        UUID.randomUUID().toString())
        );
    }

    @PostMapping("/comment/file")
    public ResponseEntity<AssistResponse> commentFile(@RequestBody FileAssistRequest request) {
        String documented = assistService.generateCommentsFromFile(request.filePath());
        return ResponseEntity.ok(
                AssistResponse.success("Documentation generated successfully", documented,
                        UUID.randomUUID().toString())
        );
    }

    @PostMapping("/commit")
    public ResponseEntity<AssistResponse> commit(@RequestBody CommitRequest request) {
        String message = assistService.generateCommitMessage(
                request.diff(),
                request.branch(),
                request.recentCommits()
        );

        return ResponseEntity.ok(
                AssistResponse.success("Commit message generated successfully", message,
                        UUID.randomUUID().toString())
        );
    }

    @PostMapping("/docs")
    public ResponseEntity<AssistResponse> docs(@RequestBody DocsRequest request) {
        String markdown = assistService.generateApiDocs(request.filePath());
        return ResponseEntity.ok(
                AssistResponse.success("API documentation generated successfully", markdown,
                        UUID.randomUUID().toString())
        );
    }
}
