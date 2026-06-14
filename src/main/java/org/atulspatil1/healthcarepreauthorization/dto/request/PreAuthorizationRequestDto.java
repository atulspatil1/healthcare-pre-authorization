package org.atulspatil1.healthcarepreauthorization.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.atulspatil1.healthcarepreauthorization.enums.Priority;

import java.math.BigDecimal;

@Data
public class PreAuthorizationRequestDto {
    @NotNull(message = "Member ID is required")
    private Long memberId;

    @NotNull(message = "Provider ID is required")
    private Long providerId;

    @NotBlank(message = "Diagnosis code is required")
    private String diagnosisCode;

    private String procedureCode;

    @NotNull(message = "Requested amount is required")
    @Positive(message = "Requested amount must be positive")
    private BigDecimal requestedAmount;

    private Priority priority;
}
