package org.atulspatil1.healthcarepreauthorization.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.atulspatil1.healthcarepreauthorization.enums.NetworkStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
