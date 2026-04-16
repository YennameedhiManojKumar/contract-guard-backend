package com.contractguard.contractguard.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal totalValue;
    private String status;
    private String clientName;
    private String freelancerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private List<MilestoneResponse> milestones;
}