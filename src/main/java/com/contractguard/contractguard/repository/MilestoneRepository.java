package com.contractguard.contractguard.repository;

import com.contractguard.contractguard.entity.Milestone;
import com.contractguard.contractguard.entity.Milestone.MilestoneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    List<Milestone> findByContractId(Long contractId);

    // Find all pending milestones past their deadline
    // This is used by breach detection engine in Day 4
    List<Milestone> findByStatusAndDeadlineBefore(
        MilestoneStatus status,
        LocalDate date
    );
}