package org.atulspatil1.healthcarepreauthorization.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.atulspatil1.healthcarepreauthorization.enums.Gender;
import org.atulspatil1.healthcarepreauthorization.enums.PolicyStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Date of birth is required")
    private LocalDate dob;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Policy number is required")
    private String policyNumber;

    @NotNull(message = "Policy status is required")
    private PolicyStatus policyStatus;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private String phone;
}
