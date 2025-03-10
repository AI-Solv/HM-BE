package com.example.HM.Domain.Concern.Dto;

import com.example.HM.Domain.Concern.Entity.Concern;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UrgentConcernDto {
    private Long concernId;
    private String title;
    private String remainingTime;

    public static UrgentConcernDto fromEntity(Concern concern) {
        Duration duration = Duration.between(LocalDateTime.now(), concern.getDeadline());
        long days = duration.toDays();
        long hours = duration.toHoursPart();

        return new UrgentConcernDto(
                concern.getId(),
                concern.getTitle(),
                String.format("%d일 %d시간 남음", days, hours)
        );
    }
}
