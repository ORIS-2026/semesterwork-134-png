package com.tech.dimefresh.service;


import com.tech.dimefresh.dto.NewsDto;
import com.tech.dimefresh.entity.Account;
import com.tech.dimefresh.entity.ChatMessage;
import com.tech.dimefresh.entity.News;
import com.tech.dimefresh.entity.NewsStatus;
import com.tech.dimefresh.repository.AccountRepository;
import com.tech.dimefresh.repository.ChatMessageRepository;
import com.tech.dimefresh.repository.NewsRepository;
import com.tech.dimefresh.s3.S3Manager;
import com.tech.dimefresh.service.dto.CreateNewsDto;
import com.tech.dimefresh.service.dto.NewsDataProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final S3Manager s3Manager;
    private final NewsRepository newsRepository;
    private final ChatMessageRepository chatMessageRepository;

    public static final int PAGE_SIZE = 7;

    @Transactional
    public void createNews(CreateNewsDto dto) {
        ChatMessage imageChatMessage = chatMessageRepository.findById(dto.imageChatMessageId()).orElseThrow();
        ChatMessage userChatMessage = chatMessageRepository.findById(dto.userChatMessageId()).orElseThrow();

        News news = new News();
        news.setImageChatMessage(imageChatMessage);
        news.setUserChatMessage(userChatMessage);
        news.setCreatedAt(Instant.now());
        news.setStatus(NewsStatus.PROCESSING);
        newsRepository.save(news);
    }

    @Transactional
    public void cancelNewsByChatMessageId(UUID chatMessageId) {
        News news = newsRepository.findByImageChatMessageId(chatMessageId).orElseThrow();

        news.setStatus(NewsStatus.CANCELLED);
        newsRepository.save(news);
    }

    @Transactional
    public void publishNewsByChatMessageId(UUID chatMessageId) {
        News news = newsRepository.findByImageChatMessageId(chatMessageId).orElseThrow();

        news.setPublishedAt(Instant.now());
        news.setStatus(NewsStatus.PUBLISHED);
        newsRepository.save(news);
    }

    @Transactional
    public List<NewsDto> getPublishedNews(int page) {
        int offset = page * PAGE_SIZE;

        return newsRepository.findNewsPage(offset, PAGE_SIZE).stream()
                .map(news -> {
                    String imageUrl = s3Manager.get(news.s3Key(), news.s3Bucket());
                    return new NewsDto(
                            news.newsId(),
                            news.prompt(),
                            imageUrl,
                            news.accountId(),
                            news.name(),
                            news.publishedAt()
                    );
                })
                .toList();
    }
}
