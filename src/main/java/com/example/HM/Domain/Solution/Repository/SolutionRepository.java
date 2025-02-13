package com.example.HM.Domain.Solution.Repository;

import com.example.HM.Domain.Solution.Entity.Solution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SolutionRepository extends JpaRepository<Solution, Long> {
    Optional<Solution> findByConcernId(Long concernId);
}
