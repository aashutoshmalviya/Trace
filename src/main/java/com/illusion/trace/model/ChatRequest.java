package com.illusion.trace.model;

public record ChatRequest(
        String query,
        String projectName
) {}