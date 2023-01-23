package com.clinked.demo.article.repository;

import com.clinked.demo.article.model.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findArticleByPublishingDateIsBetweenOrderByPublishingDateAsc(ZonedDateTime startDate, ZonedDateTime endDate);
}
