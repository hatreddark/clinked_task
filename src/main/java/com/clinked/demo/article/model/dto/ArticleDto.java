package com.clinked.demo.article.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
public class ArticleDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 8317098724045209901L;
    private Long id;

    @NotBlank(message = "not.blank.title")
    private String title;
    @NotBlank(message = "not.blank.author")
    private String author;

    @NotBlank(message = "not.blank.content")
    @Size(max = 100, message = "size.exceed.content")
    private String content;

    @NotNull(message = "not.null.publishingDate")
    private ZonedDateTime publishingDate;
}
