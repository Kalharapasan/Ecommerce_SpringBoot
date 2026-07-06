package com.example.backend.controller;

import com.example.backend.dto.custom.ResponseDto;
import com.example.backend.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createMessage(
            @RequestParam String subject,
            @RequestParam String content,
            @RequestParam String token) {
        return messageService.createMessage(subject, content, token);
    }

    @GetMapping("/user/{token}")
    public ResponseEntity<ResponseDto> getUserMessages(@PathVariable String token) {
        return messageService.getUserMessages(token);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseDto> getAllMessages() {
        return messageService.getAllMessages();
    }

    @PostMapping("/{messageId}/reply")
    public ResponseEntity<ResponseDto> replyToMessage(
            @PathVariable Integer messageId,
            @RequestParam String reply) {
        return messageService.replyToMessage(messageId, reply);
    }
}
