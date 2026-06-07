package org.atulspatil1.healthcarepreauthorization.dto.request;

import lombok.Data;
import org.atulspatil1.healthcarepreauthorization.enums.Gender;
import org.atulspatil1.healthcarepreauthorization.enums.PolicyStatus;

import java.time.LocalDate;

@Data
public class MemberRequestDto {
    private String name;
    private LocalDate dob;
    private Gender gender;
    private String policyNumber;
    private PolicyStatus policyStatus;
    private String email;
    private String phone;
}
