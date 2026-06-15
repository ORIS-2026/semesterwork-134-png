package com.tech.dimefresh.controller;

import com.tech.dimefresh.dto.ChatMessageDto;
import com.tech.dimefresh.dto.PromptRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chat", description = "Управление чатом и сообщениями")
@RequestMapping("/api/chat")
public interface ChatApiSpec {

    @Operation(summary = "Отправить промпт", description = "Принимает текстовый промпт и запускает генерацию изображения")
    @ApiResponse(responseCode = "201", description = "Промпт принят в обработку")
    @ApiResponse(responseCode = "400", description = "Промпт превышает допустимую длину")
    @ApiResponse(responseCode = "401", description = "Не авторизован")
    @PostMapping("/{chatId}")
    @ResponseStatus(HttpStatus.CREATED)
    void submitPrompt(
            @Parameter(description = "ID чата") @PathVariable Long chatId,
            @RequestBody PromptRequest request
    );

    @Operation(summary = "Получить сообщения чата", description = "Возвращает страницу сообщений, отсортированных по убыванию даты")
    @ApiResponse(responseCode = "200", description = "Список сообщений")
    @ApiResponse(responseCode = "401", description = "Не авторизован")
    @GetMapping("/{chatId}")
    List<ChatMessageDto> getMessages(
            @Parameter(description = "ID чата") @PathVariable Long chatId,
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page
    );
}
