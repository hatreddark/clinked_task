package com.clinked.demo.article.service;

import com.clinked.demo.article.model.dto.ArticleDto;
import com.clinked.demo.article.model.dto.StatisticsDayDto;
import com.clinked.demo.article.model.dto.StatisticsDto;
import com.clinked.demo.article.model.entity.Article;
import com.clinked.demo.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mehmet.sahin
 */
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ModelMapper mapper;

    private final ArticleRepository articleRepository;

    /**
     * Create Article Record
     *
     * @param articleDto ArticleDto object
     * @return ArticleDto
     */
    public ArticleDto createArticle(ArticleDto articleDto) {
        Article article = mapper.map(articleDto, Article.class);
        articleRepository.save(article);
        return mapper.map(article, ArticleDto.class);
    }

    /**
     * Get Articles as a Page List
     *
     * @param pageNumber Page number default is 0
     * @param pageSize Page size default is 10
     * @param sortField Sort Field default is publishingDate
     * @param ascending is ascending flag, default is false
     * @return Page<ArticleDto>
     */
    public Page<ArticleDto> getArticles(Integer pageNumber,
                                        Integer pageSize,
                                        String sortField,
                                        Boolean ascending) {
        return articleRepository
                .findAll(getPageable(pageNumber, pageSize, sortField, ascending))
                .map(a -> mapper.map(a, ArticleDto.class));
    }

    /**
     * Get Statistics
     *
     * @return StatisticsDto
     */
    public StatisticsDto getStatistics() {
        ZonedDateTime sevenDaysAgo = ZonedDateTime.now().minusDays(6);
        List<Article> articles = articleRepository.findArticleByPublishingDateIsBetweenOrderByPublishingDateAsc(sevenDaysAgo, ZonedDateTime.now());
        StatisticsDto statisticsDto = new StatisticsDto();
        Map<String, Integer> statisticsDayMap = new HashMap<>();


        for (int i = 0; i < 7; i++) {
            statisticsDayMap.put(sevenDaysAgo.plusDays(i).format(DateTimeFormatter.ISO_DATE), 0);
        }


        if (articles == null || articles.isEmpty()) {
            return statisticsDto;
        }

        articles.forEach(a -> {
            String key = a.getPublishingDate().format(DateTimeFormatter.ISO_DATE);
            Integer count = statisticsDayMap.get(key);
            if (count != null) {
                statisticsDayMap.put(key, ++count);
            }
        });

        statisticsDayMap.forEach((key, value) -> statisticsDto.getStatisticsDayDtoList()
                .add(StatisticsDayDto.builder()
                .date(key)
                .count(value)
                .build()));

        statisticsDto.getStatisticsDayDtoList().sort(Comparator.comparing(StatisticsDayDto::getDate));

        return statisticsDto;
    }

    /**
     * Build Pageable
     *
     * @param pageNumber Page number default is 0
     * @param pageSize Page size default is 10
     * @param sortField Sort Field default is publishingDate
     * @param ascending is ascending flag, default is false
     * @return Pageable
     */
    private Pageable getPageable(Integer pageNumber,
                                 Integer pageSize,
                                 String sortField,
                                 Boolean ascending) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = 0;
        }
        if (pageSize == null || pageSize < 0) {
            pageSize = 10;
        }
        if (sortField == null || sortField.isEmpty()) {
            sortField = "publishingDate";
        }
        if (ascending == null) {
            ascending = Boolean.FALSE;
        }

        Sort sort = Sort.by(sortField);
        if (ascending) {
            sort = sort.ascending();
        } else {
            sort = sort.descending();
        }
        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
