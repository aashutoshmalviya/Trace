package com.illusion.trace.strategy;

import java.util.List;

/**
 * Interface for different source code ingestion methods.
 */
public interface IngestionStrategy {

    /**
     * Source type(e.g., "LOCAL_DIRECTORY", "GITHUB_URL").
     */
    String getSupportedSourceType();

    /**
     * Executes the ingestion process: reading, chunking, and saving embeddings.
     * * @param sourcePath
     * @param projectName
     * @return A summary message or status of the ingestion.
     */
    String ingest(String sourcePath, String projectName);
}