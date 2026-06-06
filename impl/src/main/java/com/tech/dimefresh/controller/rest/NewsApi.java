package com.tech.dimefresh.controller.rest;

import com.tech.dimefresh.controller.NewsApiSpec;
import com.tech.dimefresh.dto.NewsDto;
import com.tech.dimefresh.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class NewsApi implements NewsApiSpec {

    private final NewsService newsService;

    @Override
    public List<NewsDto> getPublishedNews(int page) {
        return newsService.getPublishedNews(page);
    }

    @Override
    public void toggleLike(UUID newsId) {
        newsService.toggleLike(newsId);
    }
}
