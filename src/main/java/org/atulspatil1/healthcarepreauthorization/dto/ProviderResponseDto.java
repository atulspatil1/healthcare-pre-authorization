package org.atulspatil1.healthcarepreauthorization.dto;

import lombok.Data;
import org.atulspatil1.healthcarepreauthorization.enums.NetworkStatus;

import java.time.LocalDateTime;

@Data
public class ProviderResponseDto {
    private Long id;
    private String providerCode;
    private String hospitalName;
    private String city;
    private NetworkStatus networkStatus;
    private String contactEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
