package com.example.HM.Domain.Concern.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.channels.Pipe;

@Getter
@AllArgsConstructor
public class ConcernStatisticsDto {
    private Long totalConcerns;
    private Long solvedBeforeDeadline;
    private Long solvedAfterDeadline;
    private String mostFrequentCategory;

    public static ConcernStatisticsDto fromValues(Long total, Long solvedBefore, Long solvedAfter, String category) {
        return new ConcernStatisticsDto(total, solvedBefore, solvedAfter, category != null ? category : "없음");
    }}
