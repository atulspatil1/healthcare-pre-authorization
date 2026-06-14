package org.atulspatil1.healthcarepreauthorization.service;

import org.atulspatil1.healthcarepreauthorization.dto.request.ProviderRequestDto;
import org.atulspatil1.healthcarepreauthorization.dto.response.ProviderResponseDto;
import org.atulspatil1.healthcarepreauthorization.entity.Provider;
import org.atulspatil1.healthcarepreauthorization.enums.NetworkStatus;
import org.atulspatil1.healthcarepreauthorization.exception.ResourceNotFoundException;
import org.atulspatil1.healthcarepreauthorization.repository.ProviderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProviderServiceTest {

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ProviderService providerService;

    @Test
    public void testRegisterProvider() {
        ProviderRequestDto request = new ProviderRequestDto();
        request.setHospitalName("Apollo Hospital");
        request.setCity("Mumbai");
        request.setNetworkStatus(NetworkStatus.IN_NETWORK);
        request.setContactEmail("apollo@example.com");

        Provider savedProvider = Provider.builder()
                .id(1L)
                .providerCode("PROV-ABCDEFGH")
                .hospitalName("Apollo Hospital")
                .city("Mumbai")
                .networkStatus(NetworkStatus.IN_NETWORK)
                .contactEmail("apollo@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(providerRepository.save(any(Provider.class))).thenReturn(savedProvider);

        ProviderResponseDto response = providerService.registerProvider(request);

        assertThat(response).isNotNull();
        assertThat(response.getHospitalName()).isEqualTo("Apollo Hospital");
        assertThat(response.getProviderCode()).startsWith("PROV-");
        verify(providerRepository).save(any(Provider.class));
    }

    @Test
    public void testGetProviderById_Success() {
        Provider provider = Provider.builder()
                .id(1L)
                .providerCode("PROV-XYZ")
                .hospitalName("Max Hospital")
                .city("Delhi")
                .networkStatus(NetworkStatus.IN_NETWORK)
                .contactEmail("max@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(providerRepository.findById(1L)).thenReturn(Optional.of(provider));

        ProviderResponseDto response = providerService.getProviderById(1L);

        assertThat(response.getHospitalName()).isEqualTo("Max Hospital");
    }

    @Test
    public void testGetProviderById_NotFound() {
        when(providerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                providerService.getProviderById(99L));
    }

    @Test
    public void testLookupByCity() {
        Provider provider = Provider.builder()
                .id(1L)
                .providerCode("PROV-001")
                .hospitalName("Pune Hospital")
                .city("Pune")
                .networkStatus(NetworkStatus.OUT_OF_NETWORK)
                .contactEmail("pune@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(providerRepository.findByCity("Pune")).thenReturn(List.of(provider));

        List<ProviderResponseDto> result = providerService.lookupByCity("Pune");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCity()).isEqualTo("Pune");
    }

    @Test
    public void testLookupByCity_Empty() {
        when(providerRepository.findByCity("Unknown")).thenReturn(Collections.emptyList());

        List<ProviderResponseDto> result = providerService.lookupByCity("Unknown");

        assertThat(result).isEmpty();
    }
}
