package org.atulspatil1.healthcarepreauthorization.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.atulspatil1.healthcarepreauthorization.enums.NetworkStatus;

@Data
public class ProviderRequestDto {
    @NotBlank(message = "Hospital name is required")
    private String hospitalName;

    @NotBlank(message = "City is required")
    private String city;

    @NotNull(message = "Network status is required")
    private NetworkStatus networkStatus;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Contact email must be valid")
    private String contactEmail;
}
