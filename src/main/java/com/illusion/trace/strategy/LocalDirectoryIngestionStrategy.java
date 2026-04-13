package com.illusion.trace.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class LocalDirectoryIngestionStrategy implements IngestionStrategy {

    private static final Logger log = LoggerFactory.getLogger(LocalDirectoryIngestionStrategy.class);


    private final VectorStore vectorStore;

    public LocalDirectoryIngestionStrategy(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public String getSupportedSourceType() {
        return "LOCAL_DIRECTORY";
    }

    @Override
    public String ingest(String sourcePath, String projectName) {
        log.info("Starting ingestion for project: {} from path: {}", projectName, sourcePath);

        List<Document> rawDocuments = new ArrayList<>();

        // 1. Read files and attach context (Metadata)
        try (Stream<Path> paths = Files.walk(Paths.get(sourcePath))) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> {
                        try {
                            String content = Files.readString(path);

                            // We inject metadata so the LLM knows WHERE this code lives
                            Document doc = new Document(content, Map.of(
                                    "project", projectName,
                                    "file_path", path.toString(),
                                    "file_name", path.getFileName().toString()
                            ));
                            rawDocuments.add(doc);
                        } catch (IOException e) {
                            log.error("Failed to read file: {}", path, e);
                        }
                    });
        } catch (IOException e) {
            log.error("Failed to traverse directory: {}", sourcePath, e);
            throw new RuntimeException("Directory traversal failed. Check the path.", e);
        }

        if (rawDocuments.isEmpty()) {
            return "No .java files found in the specified directory.";
        }

        log.info("Found {} .java files. Starting the chunking process...", rawDocuments.size());

        // 2. Sliding Window Chunking
        // (Default constructor typically chunks at ~800-1000 tokens with overlap)
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> chunkedDocuments = splitter.apply(rawDocuments);

        log.info("Split files into {} chunks. Sending to Gemini API and saving to PostgreSQL...", chunkedDocuments.size());

        // 3. The Magic Line: This automatically generates embeddings via Gemini and saves to pgvector
        vectorStore.add(chunkedDocuments);

        log.info("Ingestion completely successfully!");
        return String.format("Success! Ingested %d files into %d searchable chunks.", rawDocuments.size(), chunkedDocuments.size());
    }
}