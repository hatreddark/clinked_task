package com.clinked.demo.article.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class StatisticsDayDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -6804857503846968450L;

    private String date;

    private int count;
}
