package com.contractguard.contractguard.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal amount;
    private LocalDate deadline;
    private String status;
    private LocalDateTime completedAt;
}