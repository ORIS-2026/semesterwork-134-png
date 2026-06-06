package com.tech.dimefresh.controller.rest;


import com.tech.dimefresh.dto.NewsDto;
import com.tech.dimefresh.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsApi {
    private final NewsService newsService;

    @GetMapping
    public List<NewsDto> getPublishedNews(@RequestParam(defaultValue = "0") int page) {
        return newsService.getPublishedNews(page);
    }

    @PostMapping("/{newsId}/like")
    public void toggleLike(@PathVariable UUID newsId) {
        newsService.toggleLike(newsId);
    }

}
