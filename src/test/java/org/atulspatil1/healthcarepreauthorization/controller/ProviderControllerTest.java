package org.atulspatil1.healthcarepreauthorization.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atulspatil1.healthcarepreauthorization.dto.request.ProviderRequestDto;
import org.atulspatil1.healthcarepreauthorization.dto.response.ProviderResponseDto;
import org.atulspatil1.healthcarepreauthorization.enums.NetworkStatus;
import org.atulspatil1.healthcarepreauthorization.service.ProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProviderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProviderService providerService;

    @InjectMocks
    private ProviderController providerController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(providerController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testRegisterProvider() throws Exception {
        ProviderRequestDto request = new ProviderRequestDto();
        request.setHospitalName("Apollo");
        request.setCity("Mumbai");
        request.setNetworkStatus(NetworkStatus.IN_NETWORK);
        request.setContactEmail("apollo@test.com");

        ProviderResponseDto response = ProviderResponseDto.builder()
                .id(1L)
                .providerCode("PROV-ABC")
                .hospitalName("Apollo")
                .city("Mumbai")
                .networkStatus(NetworkStatus.IN_NETWORK)
                .contactEmail("apollo@test.com")
                .build();

        when(providerService.registerProvider(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hospitalName").value("Apollo"));
    }

    @Test
    public void testGetProvider() throws Exception {
        ProviderResponseDto response = ProviderResponseDto.builder()
                .id(1L)
                .providerCode("PROV-ABC")
                .hospitalName("Max")
                .build();

        when(providerService.getProviderById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/providers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hospitalName").value("Max"));
    }

    @Test
    public void testLookupByCity() throws Exception {
        ProviderResponseDto response = ProviderResponseDto.builder()
                .id(1L)
                .hospitalName("Pune Hospital")
                .city("Pune")
                .build();

        when(providerService.lookupByCity(eq("Pune"))).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/providers")
                .param("city", "Pune"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Pune"));
    }
}
