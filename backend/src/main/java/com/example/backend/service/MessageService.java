package com.example.backend.service;

import com.example.backend.config.SecurityConfig;
import com.example.backend.dto.custom.JwtDataDto;
import com.example.backend.dto.custom.ResponseDto;
import com.example.backend.model.Message;
import com.example.backend.model.User;
import com.example.backend.repository.MessageRepo;
import com.example.backend.repository.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepo messageRepo;
    private final UserRepo userRepo;
    private final SecurityConfig securityConfig;

    public MessageService(MessageRepo messageRepo, UserRepo userRepo, SecurityConfig securityConfig) {
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
        this.securityConfig = securityConfig;
    }

    public ResponseEntity createMessage(String subject, String content, String token) {
        ResponseDto response = new ResponseDto();
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body("Token is required");
        }
        JwtDataDto jwtData = securityConfig.getJWTData(token);
        if (jwtData.getUserId() == null) {
            return ResponseEntity.badRequest().body("User is not authenticated");
        }

        Optional<User> optUser = userRepo.findById(jwtData.getUserId());
        if (!optUser.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        Message message = new Message();
        message.setUser(optUser.get());
        message.setSubject(subject);
        message.setContent(content);
        message.setReply("");
        message.setStatus("OPEN");
        message.setCreatedDate(new Date());

        Message savedMsg = messageRepo.save(message);
        response.setMessage("Support ticket created successfully");
        response.setData(savedMsg);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity getUserMessages(String token) {
        ResponseDto response = new ResponseDto();
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body("Token is required");
        }
        JwtDataDto jwtData = securityConfig.getJWTData(token);
        if (jwtData.getUserId() == null) {
            return ResponseEntity.badRequest().body("User is not authenticated");
        }

        List<Message> messages = messageRepo.findByUserUserId(jwtData.getUserId()).stream()
                .sorted((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate())) // Newest first
                .collect(Collectors.toList());

        response.setMessage("User messages retrieved successfully");
        response.setData(messages);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity getAllMessages() {
        ResponseDto response = new ResponseDto();
        List<Message> messages = messageRepo.findAll().stream()
                .sorted((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate())) // Newest first
                .collect(Collectors.toList());

        response.setMessage("All messages retrieved successfully");
        response.setData(messages);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity replyToMessage(Integer messageId, String reply) {
        ResponseDto response = new ResponseDto();
        Optional<Message> optMsg = messageRepo.findById(messageId);
        if (optMsg.isPresent()) {
            Message message = optMsg.get();
            message.setReply(reply);
            message.setStatus("REPLIED");
            messageRepo.save(message);

            response.setMessage("Admin reply submitted successfully");
            response.setData(message);
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.badRequest().body("Message not found");
        }
    }
}
