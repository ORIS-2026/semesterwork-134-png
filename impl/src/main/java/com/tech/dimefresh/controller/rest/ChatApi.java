package com.tech.dimefresh.controller.rest;

import com.tech.dimefresh.controller.ChatApiSpec;
import com.tech.dimefresh.dto.ChatMessageDto;
import com.tech.dimefresh.dto.PromptRequest;
import com.tech.dimefresh.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatApi implements ChatApiSpec {

    private final ChatService chatService;

    @Override
    public void submitPrompt(Long chatId, PromptRequest request) {
        chatService.handlePrompt(chatId, request.prompt());
    }

    @Override
    public List<ChatMessageDto> getMessages(Long chatId, int page) {
        return chatService.getMessages(chatId, page);
    }
}
