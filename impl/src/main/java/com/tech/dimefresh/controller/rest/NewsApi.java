package com.tech.dimefresh.controller.rest;


import com.tech.dimefresh.dto.NewsDto;
import com.tech.dimefresh.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsApi {
    private final NewsService newsService;

    @GetMapping
    public List<NewsDto> getPublishedNews(@RequestParam(defaultValue = "0") int page) {
        return newsService.getPublishedNews(page);
    }
}
