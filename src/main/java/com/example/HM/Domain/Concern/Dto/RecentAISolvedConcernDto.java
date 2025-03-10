package com.example.HM.Domain.Concern.Dto;

import com.example.HM.Domain.AIResponse.Entity.AIResponse;
import com.example.HM.Domain.Concern.Entity.Concern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecentAISolvedConcernDto {
    private Long concernId;
    private String title;
    private String category;

    public static RecentAISolvedConcernDto fromEntity(Concern concern, AIResponse aiResponse) {
        return new RecentAISolvedConcernDto(
                concern.getId(),
                concern.getTitle(),
                concern.getCategory().name()
        );
    }
}
