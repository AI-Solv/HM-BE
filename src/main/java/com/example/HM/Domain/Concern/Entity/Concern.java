package com.example.HM.Domain.Concern.Entity;

import com.example.HM.Domain.Solution.Entity.Solution;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Concern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID

    private String title; // 고민 제목

    @Lob
    private String description; // 고민 내용

    private LocalDateTime createdAt; // 생성일

    private LocalDateTime deadline; // 마감일

    @Enumerated(EnumType.STRING)
    private Status status; // 상태 ( pending || solved || ai_solved )

    @Enumerated(EnumType.STRING)
    private Category category; // 카테고리 (한글 매핑된 Enum)

    public enum Status {
        PENDING,
        SOLVED,
        AI_SOLVED
    }

    public enum Category {
        학업("학업"),
        운동("운동"),
        취미("취미"),
        인간관계("인간관계");

        private final String displayName;

        Category(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        // 한글 이름으로 카테고리 Enum 찾기
        public static Category fromDisplayName(String displayName) {
            for (Category category : values()) {
                if (category.getDisplayName().equals(displayName)) {
                    return category;
                }
            }
            throw new IllegalArgumentException("유효하지 않은 카테고리입니다: " + displayName);
        }
    }
}
