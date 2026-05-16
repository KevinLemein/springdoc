package com.quavo.springdoc_ai.dto;

import jakarta.validation.constraints.NotBlank;

public class AssistRequest {

    @NotBlank(message = "Source code cannot be blank")
    private String sourceCode;

    private String fileName;

    public String getSourceCode() {
        return sourceCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}