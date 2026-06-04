package org.atulspatil1.healthcarepreauthorization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.atulspatil1.healthcarepreauthorization.enums.PreAuthStatus;
import org.atulspatil1.healthcarepreauthorization.enums.Priority;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreAuthorizationResponseDto {
    private Long id;
    private String requestNumber;
    private Long memberId;
    private Long providerId;
    private String diagnosisCode;
    private String procedureCode;
    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;
    private PreAuthStatus status;
    private Priority priority;
    private LocalDateTime expiresAt;
    private LocalDateTime slaDeadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
