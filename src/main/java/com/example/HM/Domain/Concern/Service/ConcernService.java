package com.example.HM.Domain.Concern.Service;

import com.example.HM.Domain.AI.Controller.AIResponseController;
import com.example.HM.Domain.AIResponse.Entity.AIResponse;
import com.example.HM.Domain.AIResponse.Repository.AIResponseRepository;
import com.example.HM.Domain.Concern.Dto.ConcernStatisticsDto;
import com.example.HM.Domain.Concern.Dto.RecentAISolvedConcernDto;
import com.example.HM.Domain.Concern.Dto.UrgentConcernDto;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConcernService {

    private final ConcernRepository concernRepository;
    private final SolutionRepository solutionRepository;
    private final AIResponseRepository aiResponseRepository;

    private final AIResponseController aiController;

    // 고민 조회 (all)
    public List<Concern> getAllConcerns(){
        return concernRepository.findAll();
    }

    // 고민 조회 (ID)
    public Concern getConcernById(Long id) {
        return concernRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Concern not found with id: " + id));
    }

    // 고민 생성
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

    // 고민 삭제
    public void deleteConcernById(Long id) {
        Concern concern = concernRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Concern not found with id: " + id));

        // Solution 존재하면 삭제
        Optional<Solution> solution = solutionRepository.findByConcernId(id);
        solution.ifPresent(solutionRepository::delete);

        // AIResponse 존재하면 삭제
        Optional<AIResponse> aiResponse = aiResponseRepository.findByConcernId(id);
        aiResponse.ifPresent(aiResponseRepository::delete);

        // 고민 삭제
        concernRepository.delete(concern);
    }

    // 고민 해답 수정
    public void modifyConcernSolution(Long id, String solutionContent) {
        Concern concern = concernRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Concern not found with id: " + id));

        // Solution contet 수정
        Optional<Solution> solution = solutionRepository.findByConcernId(id);
        solution.ifPresent(s -> {
            s.setContent(solutionContent);  // Solution의 content 수정
            solutionRepository.save(s);  // 저장
        });
    }

    // 마감일 정렬 상위 5개 고민 조회
    public List<UrgentConcernDto> getUrgentConcerns() {
        List<Concern> concerns = concernRepository.findTop5ByDeadline();
        return concerns.stream()
                .map(UrgentConcernDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 최근 AI가 해결한 고민 3개 조회
    public List<RecentAISolvedConcernDto> getRecentAISolvedConcerns() {
        List<Concern> concerns = concernRepository.findTop3ByAiSolved();
        return concerns.stream()
                .map(concern -> {
                    AIResponse aiResponse = aiResponseRepository.findByConcernId(concern.getId()).orElse(null);
                    return RecentAISolvedConcernDto.fromEntity(concern, aiResponse);
                })
                .collect(Collectors.toList());
    }

    // 고민 목록 상태 조회
    public ConcernStatisticsDto getConcernStatistics(Long userId) {
        long total = concernRepository.countTotalConcerns(userId);
        long solvedBefore = concernRepository.countSolvedBeforeDeadline(userId);
        long solvedAfter = concernRepository.countSolvedAfterDeadline(userId);
        String category = concernRepository.findMostFrequentCategory(userId);

        return ConcernStatisticsDto.fromValues(total, solvedBefore, solvedAfter, category);
    }
}
