package com.example.HM.Domain.Concern.Entity;

import com.example.HM.Domain.Member.Entity.MemberEntity;
import com.example.HM.Domain.Solution.Entity.Solution;
import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

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
    private Long id;

    private String title; // 제목

    @Lob
    private String description; // 내용

    private LocalDateTime createdAt; // 생성일

    private LocalDateTime deadline; // 마감일

    private LocalDateTime remainingTime; // 마감일까지 남은 날짜 + 시간

    @Enumerated(EnumType.STRING)
    private Status status; // pending || solved || ai_solved

    @Enumerated(EnumType.STRING)
    private Category category; // 유형

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private MemberEntity user;

    public enum Status {
        PENDING,
        SOLVED,
        AI_SOLVED
    }

    public enum Category {
        STUDY("학업"),
        EXERCISE("운동"),
        HOBBY("취미"),
        RELATIONSHIP("인간관계");

        private final String displayName;

        Category(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

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
