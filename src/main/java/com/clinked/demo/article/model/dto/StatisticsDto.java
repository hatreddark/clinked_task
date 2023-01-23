package com.clinked.demo.article.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class StatisticsDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1740736304325912928L;

    private List<StatisticsDayDto> statisticsDayDtoList = new ArrayList<>();
}
