package com.illusion.trace.controller;

import com.illusion.trace.model.ChatRequest;
import com.illusion.trace.model.ChatResponse;
import com.illusion.trace.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String answer = chatService.askQuestion(request);
        return ResponseEntity.ok(new ChatResponse(answer));
    }
}