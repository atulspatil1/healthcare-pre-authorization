package org.atulspatil1.healthcarepreauthorization.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.atulspatil1.healthcarepreauthorization.enums.Decision;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private Long preAuthorizationId;
    private String reviewerId;
    private Decision decision;
    private BigDecimal approvedAmount;
    private String comments;
    private LocalDateTime reviewDate;
}
