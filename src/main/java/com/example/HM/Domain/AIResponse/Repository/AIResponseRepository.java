package com.example.HM.Domain.AIResponse.Repository;

import com.example.HM.Domain.AIResponse.Entity.AIResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AIResponseRepository extends JpaRepository<AIResponse, Long> {
    Optional<AIResponse> findByConcernId(Long id);
}
