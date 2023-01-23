package com.clinked.demo.article.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "ARTICLE")
@Data
public class Article implements Serializable {

    @Serial
    private static final long serialVersionUID = -6668625485413794160L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 50, nullable = false)
    private String author;

    @Column(length = 100, nullable = false)
    private String content;

    @Column(nullable = false)
    private ZonedDateTime publishingDate;
}
