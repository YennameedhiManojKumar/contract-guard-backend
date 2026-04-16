package com.contractguard.contractguard.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ContractRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Total value is required")
    @Positive(message = "Total value must be positive")
    private BigDecimal totalValue;

    @NotNull(message = "Freelancer ID is required")
    private Long freelancerId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    // Milestones can be added while creating contract
    private List<MilestoneRequest> milestones;
}