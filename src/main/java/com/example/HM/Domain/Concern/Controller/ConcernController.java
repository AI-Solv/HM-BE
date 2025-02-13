package com.example.HM.Domain.Concern.Controller;

import com.example.HM.Domain.AIResponse.Repository.AIResponseRepository;
import com.example.HM.Domain.AIResponse.Service.AIResponseService;
import com.example.HM.Domain.Concern.Dto.CreateConcernRequestDto;
import com.example.HM.Domain.Concern.Entity.Concern;
import com.example.HM.Domain.Concern.Service.ConcernResolveService;
import com.example.HM.Domain.Concern.Service.ConcernService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/problems")
@RequiredArgsConstructor
public class ConcernController {

    private final ConcernService concernService;
    private final ConcernResolveService concernResolveService;
    private final AIResponseService aiResponseService;

    // 고민 생성
    @PostMapping
    public Concern createConcern(@RequestBody CreateConcernRequestDto request) {
        return concernService.createConcern(request.getTitle(), request.getDescription(), request.getDeadline(), request.getCategory());
    }

    // 고민 조회
    @GetMapping
    public List<Concern> getAllConcerns() {
        return concernService.getAllConcerns();
    }

    // 고민 조회 (ID)
    @GetMapping("/{id}")
    public Concern getConcernById(@PathVariable Long id) {
        return concernService.getConcernById(id);
    }

    // 고민 해결
    @PostMapping("/{id}/resolve")
    public Concern resolveConcern(@PathVariable Long id, @RequestParam String solutionContent) {
        return concernResolveService.resolveConcern(id, solutionContent);
    }

    // 고민 삭제
    @DeleteMapping("/{id}")
    public void deleteConcern(@PathVariable Long id) {
        concernService.deleteConcernById(id);
    }

    // 고민 수정
    @PutMapping("/{id}")
    public void modifyConcern(@PathVariable Long id, @RequestParam String solutionContent){
        concernService.modifyConcernSolution(id, solutionContent);
    }

    // ai 답변 조회
    @GetMapping("/{id}/ai-answer")
    public String getAIResponse(@PathVariable Long id) {
        return aiResponseService.getAIResponseByConcernId(id);
    }
}
