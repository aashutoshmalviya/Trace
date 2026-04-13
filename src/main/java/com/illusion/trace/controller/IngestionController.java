package com.illusion.trace.controller;

import com.illusion.trace.model.IngestRequest;
import com.illusion.trace.strategy.IngestionStrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ingest")
public class IngestionController {

    private final List<IngestionStrategy> strategies;

    public IngestionController(List<IngestionStrategy> strategies) {
        this.strategies = strategies;
    }

    @PostMapping
    public ResponseEntity<String> ingestCodebase(@RequestBody IngestRequest request) {
        // Find the strategy that matches the source type (Strategy Pattern)
        IngestionStrategy strategy = strategies.stream()
                .filter(s -> s.getSupportedSourceType().equals(request.sourceType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unsupported source type: " + request.sourceType()));

        String result = strategy.ingest(request.sourcePath(), request.projectName());
        return ResponseEntity.ok(result);
    }
}