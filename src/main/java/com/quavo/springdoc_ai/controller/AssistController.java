package com.quavo.springdoc_ai.controller;

import com.quavo.springdoc_ai.service.AssistService;
import com.quavo.springdoc_ai.dto.*;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
