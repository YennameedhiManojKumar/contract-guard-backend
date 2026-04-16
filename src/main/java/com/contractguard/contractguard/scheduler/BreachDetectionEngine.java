package com.contractguard.contractguard.scheduler;

import com.contractguard.contractguard.entity.*;
import com.contractguard.contractguard.entity.Milestone.MilestoneStatus;
import com.contractguard.contractguard.repository.*;
import com.contractguard.contractguard.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BreachDetectionEngine {

    private final MilestoneRepository milestoneRepository;
    private final ContractRepository contractRepository;
    private final NotificationService notificationService;

    // Runs every day at midnight
    // For testing we use every 60 seconds → change to midnight later
    @Scheduled(fixedRate = 60000) // every 60 seconds for testing
    // @Scheduled(cron = "0 0 0 * * *") // midnight — use this in production
    @Transactional
    public void detectBreaches() {

        log.info("🔍 Breach Detection Engine running at {}",
                LocalDate.now());

        // Find all PENDING milestones past their deadline
        List<Milestone> overdueMilestones = milestoneRepository
                .findByStatusAndDeadlineBefore(
                        MilestoneStatus.PENDING,
                        LocalDate.now());

        if (overdueMilestones.isEmpty()) {
            log.info("✅ No breaches found.");
            return;
        }

        log.info("⚠️ Found {} overdue milestones", overdueMilestones.size());

        for (Milestone milestone : overdueMilestones) {

            // Mark milestone as BREACHED
            milestone.setStatus(MilestoneStatus.BREACHED);
            milestoneRepository.save(milestone);

            log.info("🚨 Milestone BREACHED: '{}' | Deadline was: {}",
                    milestone.getTitle(),
                    milestone.getDeadline());

            // Mark contract as BREACHED
            Contract contract = milestone.getContract();
            if (contract.getStatus() != Contract.ContractStatus.BREACHED) {
                contract.setStatus(Contract.ContractStatus.BREACHED);
                contractRepository.save(contract);

                log.info("📋 Contract BREACHED: '{}'", contract.getTitle());

                // Send alerts to both client and freelancer
                notificationService.sendBreachAlert(
                        contract.getClient(), contract);
                notificationService.sendBreachAlert(
                        contract.getFreelancer(), contract);
            }
        }

        log.info("🏁 Breach Detection complete. Processed {} milestones",
                overdueMilestones.size());
    }
}