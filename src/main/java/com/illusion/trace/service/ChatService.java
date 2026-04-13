package com.illusion.trace.service;

import com.illusion.trace.model.ChatRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder; // Imported builder
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:prompts/system-qa.st")
    private Resource systemPromptTemplate;

    public ChatService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    public String askQuestion(ChatRequest request) {
        log.info("Executing vector search for query: '{}' in project: {}", request.query(), request.projectName());

        try {
            // 1. Vector Search with Safe Metadata Filtering
            FilterExpressionBuilder b = new FilterExpressionBuilder();
            List<Document> similarDocuments = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(request.query())
                            .topK(5)
                            // Safely handles special characters in the project name
                            .filterExpression(b.eq("project", request.projectName()).build())
                            .build()
            );

            if (similarDocuments.isEmpty()) {
                return "I couldn't find any relevant code in the ingested repository to answer your question.";
            }

            // 2. Stitch the retrieved documents together
            String documentContext = similarDocuments.stream()
                    .map(doc -> "File: " + doc.getMetadata().get("file_name") + "\n" + doc.getText())
                    .collect(Collectors.joining("\n\n---\n\n"));

            log.info("Found {} relevant code chunks. Generating LLM response...", similarDocuments.size());

            // 3. Augment and Generate using the fluid ChatClient API
            return chatClient.prompt()
                    .system(s -> s.text(systemPromptTemplate)
                            .param("documents", documentContext))
                    .user(request.query())
                    .call()
                    .content();

        } catch (Exception e) {
            log.error("Failed to generate response due to an external API or database error.", e);
            return "I am currently unable to process your request due to a system error. Please try again later.";
        }
    }
}