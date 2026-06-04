package org.atulspatil1.healthcarepreauthorization.dto;

import lombok.Data;
import org.atulspatil1.healthcarepreauthorization.enums.NetworkStatus;

@Data
public class ProviderRequestDto {
    private String hospitalName;
    private String city;
    private NetworkStatus networkStatus;
    private String contactEmail;
}
