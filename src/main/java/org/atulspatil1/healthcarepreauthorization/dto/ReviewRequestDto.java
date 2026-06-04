package org.atulspatil1.healthcarepreauthorization.dto;

import lombok.Data;
import org.atulspatil1.healthcarepreauthorization.enums.Decision;

import java.math.BigDecimal;

@Data
public class ReviewRequestDto {
    private Decision decision;
    private BigDecimal approvedAmount;
    private String comments;
}
