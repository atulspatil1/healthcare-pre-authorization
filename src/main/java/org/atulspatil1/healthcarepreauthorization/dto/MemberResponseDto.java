package org.atulspatil1.healthcarepreauthorization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.atulspatil1.healthcarepreauthorization.enums.Gender;
import org.atulspatil1.healthcarepreauthorization.enums.PolicyStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String memberNumber;
    private String name;
    private LocalDate dob;
    private Gender gender;
    private String policyNumber;
    private PolicyStatus policyStatus;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
