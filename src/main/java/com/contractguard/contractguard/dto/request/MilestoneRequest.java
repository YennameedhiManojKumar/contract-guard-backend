package com.contractguard.contractguard.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MilestoneRequest {

    @NotBlank(message = "Milestone title is required")
    private String title;

    private String description;

    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Deadline is required")
    private LocalDate deadline;
}