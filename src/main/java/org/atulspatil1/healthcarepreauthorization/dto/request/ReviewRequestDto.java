package org.atulspatil1.healthcarepreauthorization.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.atulspatil1.healthcarepreauthorization.enums.Decision;

import java.math.BigDecimal;

@Data
public class ReviewRequestDto {
    @NotNull(message = "Decision is required")
    private Decision decision;

    private BigDecimal approvedAmount;

    @NotBlank(message = "Comments are required")
    private String comments;
}
