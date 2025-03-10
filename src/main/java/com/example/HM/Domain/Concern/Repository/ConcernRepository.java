package com.example.HM.Domain.Concern.Repository;

import com.example.HM.Domain.Concern.Entity.Concern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ConcernRepository extends JpaRepository<Concern, Long> {

    // 마감기한 기준 정렬 고민 5개 조회
    @Query("SELECT c FROM Concern c " +
            "WHERE c.status = 'PENDING' "+
            "AND c.deadline > CURRENT_TIMESTAMP " +
            "ORDER BY c.deadline ASC " +
            "LIMIT 5")
    List<Concern> findTop5ByDeadline();

    // AI_SOLVED 상태 기준 정렬 고민 3개 조회
    @Query("SELECT c FROM Concern c " +
            "WHERE c.status = 'AI_SOLVED' " +
            "ORDER BY c.deadline DESC " +
            "LIMIT 3")
    List<Concern> findTop3ByAiSolved();

    // 전체 고민 개수
    @Query("SELECT COUNT(c) FROM Concern c WHERE c.user.id = :userId")
    long countTotalConcerns(@Param("userId") Long userId);

    // 마감일 전에 해결된 고민 개수 (SOLVED)
    @Query("SELECT COUNT(c) FROM Concern c WHERE c.status = 'SOLVED' AND c.user.id = :userId")
    long countSolvedBeforeDeadline(@Param("userId") Long userId);

    // 마감일 후 AI가 해결한 고민 개수 (AI_SOLVED)
    @Query("SELECT COUNT(c) FROM Concern c WHERE c.status = 'AI_SOLVED' AND c.user.id = :userId")
    long countSolvedAfterDeadline(@Param("userId") Long userId);

    // 가장 많이 작성된 고민 카테고리
    @Query("SELECT c.category FROM Concern c WHERE c.user.id = :userId " +
            "GROUP BY c.category ORDER BY COUNT(c) DESC LIMIT 1")
    String findMostFrequentCategory(@Param("userId") Long userId);

}
