package com.recruitment.chat.controller;

import com.recruitment.chat.model.ChatMessage;
import com.recruitment.chat.model.MessageType;
import com.recruitment.chat.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    public ChatController(SimpMessagingTemplate messagingTemplate, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }

    @MessageMapping("/chat/{to}")
    public void sendMessage(@DestinationVariable String to, @Payload ChatMessage message) {
        try {
            message.setTimestamp(LocalDateTime.now());
            messagingTemplate.convertAndSend("/user/" + to + "/queue/messages", message);
            logger.info("Sent message from {} to {}: {}", message.getSender(), to, message.getContent());
        } catch (Exception e) {
            logger.error("Error sending message: {}", e.getMessage(), e);
        }
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        try {
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
            userService.addUser(com.recruitment.chat.model.User.builder()
                    .id(chatMessage.getSender())
                    .name(chatMessage.getSender())
                    .online(true)
                    .build());
            chatMessage.setType(MessageType.JOIN);
            chatMessage.setTimestamp(LocalDateTime.now());
            logger.info("User {} joined the chat", chatMessage.getSender());
            return chatMessage;
        } catch (Exception e) {
            logger.error("Error adding user: {}", e.getMessage(), e);
            return null;
        }
    }

    @MessageMapping("/chat.removeUser")
    @SendTo("/topic/public")
    public ChatMessage removeUser(@Payload ChatMessage chatMessage) {
        try {
            userService.removeUser(chatMessage.getSender());
            chatMessage.setType(MessageType.LEAVE);
            chatMessage.setTimestamp(LocalDateTime.now());
            logger.info("User {} left the chat", chatMessage.getSender());
            return chatMessage;
        } catch (Exception e) {
            logger.error("Error removing user: {}", e.getMessage(), e);
            return null;
        }
    }

    @GetMapping("/test")
    public String test() {
        return "test"; // 返回 test.html 页面
    }
}