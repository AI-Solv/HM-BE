package com.example.HM.Domain.AIResponse.Entity;

import com.example.HM.Domain.Concern.Entity.Concern;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content; // 내용

    private LocalDateTime createdAt; // 생성일

    @OneToOne
    @JoinColumn(name = "concernId")
    private Concern concern; // 해당 고민

}
