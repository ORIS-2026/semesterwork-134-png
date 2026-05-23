package com.tech.dimefresh.controller;

import com.tech.dimefresh.dto.ChatMessageDto;
import com.tech.dimefresh.dto.PromptRequest;
import com.tech.dimefresh.service.ChatAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/ai")
@RequiredArgsConstructor
public class ChatAiApi {

    private final ChatAiService chatAiService;

    /**
     * POST /api/chat/ai/{chatId}
     * Принять новый промпт и запустить генерацию изображения.
     * Возвращает созданное сообщение (пока со статусом PROCESSING).
     */
    @PostMapping("/{chatId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void submitPrompt(
            @PathVariable Long chatId,
            @RequestBody PromptRequest request) {
        chatAiService.handlePrompt(chatId, request.prompt());
    }

    /**
     * GET /api/chat/ai/{chatId}?page=0
     * Получить страницу сообщений чата (по умолчанию по 15 сообщений на странице).
     */
    @GetMapping("/{chatId}")
    public List<ChatMessageDto> getMessages(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page) {
        return chatAiService.getMessages(chatId, page);
    }
}
