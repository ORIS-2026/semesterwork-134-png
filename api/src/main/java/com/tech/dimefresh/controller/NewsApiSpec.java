package com.tech.dimefresh.controller;

import com.tech.dimefresh.dto.NewsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "News", description = "Лента новостей с опубликованными изображениями")
@RequestMapping("/api/news")
public interface NewsApiSpec {

    @Operation(summary = "Получить ленту новостей", description = "Возвращает страницу опубликованных постов, отсортированных по дате публикации")
    @ApiResponse(responseCode = "200", description = "Список постов")
    @ApiResponse(responseCode = "401", description = "Не авторизован")
    @GetMapping
    List<NewsDto> getPublishedNews(
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page
    );

    @Operation(summary = "Поставить / убрать лайк", description = "Если лайк уже стоит — убирает, иначе добавляет")
    @ApiResponse(responseCode = "200", description = "Лайк переключён")
    @ApiResponse(responseCode = "401", description = "Не авторизован")
    @ApiResponse(responseCode = "404", description = "Пост не найден")
    @PostMapping("/{newsId}/like")
    void toggleLike(
            @Parameter(description = "ID новости") @PathVariable UUID newsId
    );
}
