package com.clinked.demo.article.service;

import com.clinked.demo.article.model.dto.ArticleDto;
import com.clinked.demo.article.model.dto.StatisticsDayDto;
import com.clinked.demo.article.model.dto.StatisticsDto;
import com.clinked.demo.article.model.entity.Article;
import com.clinked.demo.article.repository.ArticleRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @InjectMocks
    private ArticleService articleService;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ModelMapper mapper;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    private Article createNewArticle(ArticleDto articleDto) {
        Article article = new Article();
        article.setTitle(articleDto.getTitle());
        article.setAuthor(articleDto.getAuthor());
        article.setContent(articleDto.getContent());
        article.setPublishingDate(articleDto.getPublishingDate());
        Random random = new Random();
        article.setId(Math.abs(random.nextLong() % 1000));
        return article;
    }

    private Article createArticle(Long id, String postFix) {
        Article article = new Article();
        article.setId(id);
        article.setTitle("Title" + postFix);
        article.setAuthor("Author" + postFix);
        article.setContent("Content" + postFix);
        article.setPublishingDate(ZonedDateTime.now());
        return article;
    }

    private ArticleDto getArticleDto(Article article) {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setId(article.getId());
        articleDto.setTitle(article.getTitle());
        articleDto.setAuthor(article.getAuthor());
        articleDto.setContent(article.getContent());
        articleDto.setPublishingDate(article.getPublishingDate());
        return articleDto;
    }

    @Test
    public void createArticleSuccess() {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setTitle("Title");
        articleDto.setAuthor("Author");
        articleDto.setContent("Content");
        articleDto.setPublishingDate(ZonedDateTime.now());

        Article article = createNewArticle(articleDto);

        when(mapper.map(articleDto, Article.class)).thenReturn(article);
        when(articleRepository.save(article)).thenReturn(article);
        when(mapper.map(article, ArticleDto.class)).thenReturn(getArticleDto(article));

        ArticleDto result = articleService.createArticle(articleDto);

        assertEquals(article.getId(), result.getId());
        assertEquals(article.getTitle(), result.getTitle());
        assertEquals(article.getAuthor(), result.getAuthor());
        assertEquals(article.getContent(), result.getContent());
        assertEquals(article.getPublishingDate(), result.getPublishingDate());
    }

    @Test
    public void createArticleFailed() {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setTitle(null);
        articleDto.setAuthor(null);
        articleDto.setContent("ContentContentContentContentContentContentContentContentContentContentContentContentContentContentContent");
        articleDto.setPublishingDate(ZonedDateTime.now());

        Set<ConstraintViolation<ArticleDto>> violations = validator.validate(articleDto);

        assertEquals(3, violations.size());
        assertTrue(violations.stream().anyMatch(vm -> "not.blank.title".equals(vm.getMessage())));
        assertTrue(violations.stream().anyMatch(vm -> "not.blank.author".equals(vm.getMessage())));
        assertTrue(violations.stream().anyMatch(vm -> "size.exceed.content".equals(vm.getMessage())));
    }

    @Test
    public void getArticlesSuccess() {
        Article article1 = createArticle(1L, "1");
        Article article2 = createArticle(2L, "2");
        List<Article> articles = Arrays.asList(article1, article2);

        Page<Article> page = new PageImpl<>(articles, PageRequest.of(0, 10,
                Sort.by("publishingDate").descending()) ,3);
        when(articleRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.map(article1, ArticleDto.class)).thenReturn(getArticleDto(article1));
        when(mapper.map(article2, ArticleDto.class)).thenReturn(getArticleDto(article2));
        Page<ArticleDto> result = articleService.getArticles(0,10, null, null);

        assertEquals(2L, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("Title1", result.getContent().get(0).getTitle());
        assertEquals("Author2", result.getContent().get(1).getAuthor());
    }

    @Test
    public void getArticlesEmptyList() {
        List<Article> articles = new ArrayList<>();

        Page<Article> page = new PageImpl<>(articles, PageRequest.of(0, 10,
                Sort.by("publishingDate").descending()) ,0);
        when(articleRepository.findAll(any(Pageable.class))).thenReturn(page);
        Page<ArticleDto> result = articleService.getArticles(0,10, null, null);

        assertEquals(0L, result.getTotalElements());
        assertEquals(0, result.getContent().size());
    }

    @Test
    public void getStatisticsSuccess() {
        Article article1 = createArticle(1L, "1");
        article1.setPublishingDate(ZonedDateTime.now().minusDays(1));
        Article article2 = createArticle(2L, "2");
        article2.setPublishingDate(ZonedDateTime.now().minusDays(1));
        List<Article> articles = Arrays.asList(article1, article2);

        when(articleRepository
                .findArticleByPublishingDateIsBetweenOrderByPublishingDateAsc(
                        any(ZonedDateTime.class), any(ZonedDateTime.class)))
                .thenReturn(articles);
        StatisticsDto result = articleService.getStatistics();


        assertEquals(0, result.getStatisticsDayDtoList().stream()
                .filter(s -> ZonedDateTime.now().minusDays(2).format(DateTimeFormatter.ISO_DATE).equals(s.getDate()))
                .map(StatisticsDayDto::getCount)
                .findFirst().orElse(0));
        assertEquals(2, result.getStatisticsDayDtoList().stream()
                .filter(s -> ZonedDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_DATE).equals(s.getDate()))
                .map(StatisticsDayDto::getCount)
                .findFirst().orElse(0));
    }

    @Test
    public void getStatisticsEmpty() {
        List<Article> articles = new ArrayList<>();

        when(articleRepository
                .findArticleByPublishingDateIsBetweenOrderByPublishingDateAsc(
                        any(ZonedDateTime.class), any(ZonedDateTime.class)))
                .thenReturn(articles);
        StatisticsDto result = articleService.getStatistics();


        assertEquals(0, result.getStatisticsDayDtoList().stream()
                .filter(s -> ZonedDateTime.now().minusDays(2).format(DateTimeFormatter.ISO_DATE).equals(s.getDate()))
                .map(StatisticsDayDto::getCount)
                .findFirst().orElse(0));
        assertEquals(0, result.getStatisticsDayDtoList().stream()
                .filter(s -> ZonedDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_DATE).equals(s.getDate()))
                .map(StatisticsDayDto::getCount)
                .findFirst().orElse(0));
    }
}
