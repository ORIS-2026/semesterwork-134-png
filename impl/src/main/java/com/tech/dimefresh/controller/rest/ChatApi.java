package com.tech.dimefresh.controller.rest;

import com.tech.dimefresh.dto.ChatMessageDto;
import com.tech.dimefresh.dto.PromptRequest;
import com.tech.dimefresh.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatApi {

    private final ChatService chatService;

    /**
     * POST /api/chat/{chatId}
     * Принять новый промпт и запустить генерацию изображения.
     * Возвращает созданное сообщение (пока со статусом PROCESSING).
     */
    @PostMapping("/{chatId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void submitPrompt(
            @PathVariable Long chatId,
            @RequestBody PromptRequest request) {
        chatService.handlePrompt(chatId, request.prompt());
    }

    /**
     * GET /api/chat/{chatId}?page=0
     * Получить страницу сообщений чата (по умолчанию по 15 сообщений на странице).
     */
    @GetMapping("/{chatId}")
    public List<ChatMessageDto> getMessages(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page) {
        return chatService.getMessages(chatId, page);
    }
}
