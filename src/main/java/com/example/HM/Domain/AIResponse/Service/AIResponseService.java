package com.example.HM.Domain.AIResponse.Service;

import com.example.HM.Domain.AIResponse.Entity.AIResponse;
import com.example.HM.Domain.AIResponse.Repository.AIResponseRepository;
import com.example.HM.Domain.Concern.Entity.Concern;
import com.example.HM.Domain.Concern.Repository.ConcernRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class AIResponseService {
    private final AIResponseRepository aiResponseRepository;
    private final ConcernRepository concernRepository;

    public String getAIResponseByConcernId(Long concernId) {
        Optional<AIResponse> aiResponse = aiResponseRepository.findByConcernId(concernId);
        return aiResponse.map(AIResponse::getContent).orElse(null);
    }
}
