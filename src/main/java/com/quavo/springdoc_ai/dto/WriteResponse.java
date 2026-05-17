package com.quavo.springdoc_ai.dto;

public record WriteResponse (

    String filePath,
    String backupPath,
    int methodsDocumented
    )
{}
