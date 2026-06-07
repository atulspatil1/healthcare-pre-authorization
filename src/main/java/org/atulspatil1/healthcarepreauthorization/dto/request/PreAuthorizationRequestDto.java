package org.atulspatil1.healthcarepreauthorization.dto.request;

import lombok.Data;
import org.atulspatil1.healthcarepreauthorization.enums.Priority;

import java.math.BigDecimal;

@Data
public class PreAuthorizationRequestDto {
    private Long memberId;
    private Long providerId;
    private String diagnosisCode;
    private String procedureCode;
    private BigDecimal requestedAmount;
    private Priority priority;
}
