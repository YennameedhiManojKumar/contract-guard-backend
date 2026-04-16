package com.contractguard.contractguard.service;

import com.contractguard.contractguard.entity.*;
import com.contractguard.contractguard.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void sendBreachAlert(User user, Contract contract) {

        String message = buildBreachMessage(user, contract);

        // For now — log to console
        // Day 7 we replace this with real JavaMail
        log.info("📧 BREACH ALERT → {} | Contract: {} | Message: {}",
                user.getEmail(),
                contract.getTitle(),
                message);

        // Log notification to DB
        Notification notification = Notification.builder()
                .user(user)
                .contract(contract)
                .type(Notification.NotificationType.EMAIL)
                .message(message)
                .status("SENT")
                .build();

        notificationRepository.save(notification);
    }

    private String buildBreachMessage(User user, Contract contract) {
        return String.format(
                "Dear %s, contract '%s' has been marked as BREACHED " +
                        "because one or more milestones passed their deadline. " +
                        "Please log in to review and take action.",
                user.getName(),
                contract.getTitle());
    }
}