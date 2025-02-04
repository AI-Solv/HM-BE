package com.example.HM.Domain.Concern.Service;

import com.example.HM.Domain.AI.Controller.AIResponseController;
import com.example.HM.Domain.AIResponse.Entity.AIResponse;
import com.example.HM.Domain.AIResponse.Repository.AIResponseRepository;
import com.example.HM.Domain.Concern.Entity.Concern;
import com.example.HM.Domain.Concern.Repository.ConcernRepository;
import com.example.HM.Domain.Solution.Entity.Solution;
import com.example.HM.Domain.Solution.Repository.SolutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcernService {

    private final ConcernRepository concernRepository;
    private final SolutionRepository solutionRepository;
    private final AIResponseRepository aiResponseRepository;

    private final AIResponseController aiController;

    public List<Concern> getAllConcerns(){
        return concernRepository.findAll();
    }

    public Concern getConcernById(Long id) {
        return concernRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Concern not found with id: " + id));
    }

    @Transactional
    public Concern createConcern(String title, String description, LocalDateTime deadline, Concern.Category category) {
        Concern concern = Concern.builder()
                .title(title)
                .description(description)
                .deadline(deadline)
                .createdAt(LocalDateTime.now())
                .status(Concern.Status.PENDING)
                .build();

        return concernRepository.save(concern);
    }

    @Transactional
    public Concern resolveConcern(Long id, String solutionContent) {
        Concern concern = getConcernById(id);
        LocalDateTime now = LocalDateTime.now();

        // 해결책 저장
        Solution solution = Solution.builder()
                .content(solutionContent)
                .createdAt(now)
                .concern(concern)
                .build();

        solutionRepository.save(solution);

        if (now.isBefore(concern.getDeadline())) {
            // 마감일 이전에 해결
            concern.setStatus(Concern.Status.SOLVED);
        } else {
            // 마감일 이후에 해결
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
