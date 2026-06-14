package org.atulspatil1.healthcarepreauthorization.service;

import lombok.RequiredArgsConstructor;
import org.atulspatil1.healthcarepreauthorization.dto.request.ProviderRequestDto;
import org.atulspatil1.healthcarepreauthorization.dto.response.ProviderResponseDto;
import org.atulspatil1.healthcarepreauthorization.entity.Provider;
import org.atulspatil1.healthcarepreauthorization.exception.ResourceNotFoundException;
import org.atulspatil1.healthcarepreauthorization.repository.ProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderRepository providerRepository;

    @Transactional
    public ProviderResponseDto registerProvider(ProviderRequestDto request) {
        String providerCode = "PROV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDateTime now = LocalDateTime.now();

        Provider provider = Provider.builder()
                .providerCode(providerCode)
                .hospitalName(request.getHospitalName())
                .city(request.getCity())
                .networkStatus(request.getNetworkStatus())
                .contactEmail(request.getContactEmail())
                .createdAt(now)
                .updatedAt(now)
                .build();

        Provider savedProvider = providerRepository.save(provider);
        return mapToResponseDto(savedProvider);
    }

    public ProviderResponseDto getProviderById(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with id: " + id));
        return mapToResponseDto(provider);
    }

    public List<ProviderResponseDto> lookupByCity(String city) {
        return providerRepository.findByCity(city).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private ProviderResponseDto mapToResponseDto(Provider provider) {
        return ProviderResponseDto.builder()
                .id(provider.getId())
                .providerCode(provider.getProviderCode())
                .hospitalName(provider.getHospitalName())
                .city(provider.getCity())
                .networkStatus(provider.getNetworkStatus())
                .contactEmail(provider.getContactEmail())
                .createdAt(provider.getCreatedAt())
                .updatedAt(provider.getUpdatedAt())
                .build();
    }
}
