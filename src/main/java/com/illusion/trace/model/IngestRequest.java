package com.illusion.trace.model;

public record IngestRequest(
        String projectName,
        String sourceType, 
        String sourcePath  
) {}