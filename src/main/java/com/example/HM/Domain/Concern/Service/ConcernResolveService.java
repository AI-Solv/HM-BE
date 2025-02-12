package com.example.HM.Domain.Concern.Service;

import com.example.HM.Domain.AI.Controller.AIResponseController;
import com.example.HM.Domain.AIResponse.Entity.AIResponse;
import com.example.HM.Domain.AIResponse.Repository.AIResponseRepository;
import com.example.HM.Domain.Concern.Controller.ConcernController;
import com.example.HM.Domain.Concern.Entity.Concern;
import com.example.HM.Domain.Concern.Repository.ConcernRepository;
import com.example.HM.Domain.Solution.Entity.Solution;
import com.example.HM.Domain.Solution.Repository.SolutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConcernResolveService {

    private final ConcernRepository concernRepository;
    private final SolutionRepository solutionRepository;
    private final AIResponseRepository aiResponseRepository;

    private final AIResponseController aiController;

    private final ConcernService concernService;

    public Concern resolveConcern(Long id, String solutionContent) {
        Concern concern = concernService.getConcernById(id);
        LocalDateTime now = LocalDateTime.now(); // 현재 시간

        // 해결책 저장
        Solution solution = Solution.builder()
                .content(solutionContent)
                .createdAt(now)
                .concern(concern)
                .build();
        solutionRepository.save(solution);

        if (now.isBefore(concern.getDeadline())) {
            // 마감일 이전에 해결 - 수동
            concern.setStatus(Concern.Status.SOLVED);
        } else {
            // 마감일 이후에 해결 - AI
            String aiSolutionContent = aiController.chat(concern.getDescription());

            AIResponse aiResponse = AIResponse.builder()
                    .content(aiSolutionContent)
                    .createdAt(now)
                    .concern(concern)
                    .build();
            aiResponseRepository.save(aiResponse);

            concern.setStatus(Concern.Status.AI_SOLVED);
        }

        return concernRepository.save(concern);
    }
}
