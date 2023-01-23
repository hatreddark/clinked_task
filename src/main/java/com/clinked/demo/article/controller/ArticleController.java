package com.clinked.demo.article.controller;

import com.clinked.demo.article.model.dto.ArticleDto;
import com.clinked.demo.article.model.dto.StatisticsDto;
import com.clinked.demo.article.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<ArticleDto> createArticle(@Valid @RequestBody ArticleDto articleDto) {
        System.out.println(articleDto);
        ArticleDto result = articleService.createArticle(articleDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<ArticleDto>> getArticles(@RequestParam(required = false) Integer pageNumber,
                                                        @RequestParam(required = false) Integer pageSize,
                                                        @RequestParam(required = false) String sortField,
                                                        @RequestParam(required = false) Boolean ascending) {
        return ResponseEntity.ok(articleService.getArticles(pageNumber, pageSize, sortField, ascending));
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsDto> getStatistics() {
        return ResponseEntity.ok(articleService.getStatistics());
    }
}
