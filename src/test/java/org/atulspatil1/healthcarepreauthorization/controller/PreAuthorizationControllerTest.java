package org.atulspatil1.healthcarepreauthorization.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atulspatil1.healthcarepreauthorization.dto.request.PreAuthorizationRequestDto;
import org.atulspatil1.healthcarepreauthorization.dto.response.PreAuthorizationResponseDto;
import org.atulspatil1.healthcarepreauthorization.entity.Review;
import org.atulspatil1.healthcarepreauthorization.enums.PreAuthStatus;
import org.atulspatil1.healthcarepreauthorization.enums.Priority;
import org.atulspatil1.healthcarepreauthorization.service.PreAuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PreAuthorizationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PreAuthorizationService preAuthorizationService;

    @InjectMocks
    private PreAuthorizationController preAuthorizationController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(preAuthorizationController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    private PreAuthorizationResponseDto createResponse(PreAuthStatus status) {
        return PreAuthorizationResponseDto.builder()
                .id(1L)
                .requestNumber("REQ-12345678")
                .memberId(1L)
                .providerId(1L)
                .diagnosisCode("D001")
                .requestedAmount(new BigDecimal("5000.00"))
                .status(status)
                .priority(Priority.NORMAL)
                .build();
    }

    @Test
    public void testCreatePreAuthRequest() throws Exception {
        PreAuthorizationRequestDto requestDto = new PreAuthorizationRequestDto();
        requestDto.setMemberId(1L);
        requestDto.setProviderId(1L);
        requestDto.setDiagnosisCode("D001");
        requestDto.setRequestedAmount(new BigDecimal("5000.00"));

        when(preAuthorizationService.createPreAuthRequest(any())).thenReturn(createResponse(PreAuthStatus.DRAFT));

        mockMvc.perform(post("/api/v1/preauth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.requestNumber").value("REQ-12345678"));
    }

    @Test
    public void testGetPreAuthRequest() throws Exception {
        when(preAuthorizationService.getPreAuthRequestById(1L)).thenReturn(createResponse(PreAuthStatus.DRAFT));

        mockMvc.perform(get("/api/v1/preauth/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testSubmitDraft() throws Exception {
        when(preAuthorizationService.submitPreAuthRequestDraft(1L)).thenReturn(createResponse(PreAuthStatus.SUBMITTED));

        mockMvc.perform(patch("/api/v1/preauth/1/submit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    @Test
    public void testStartReview() throws Exception {
        when(preAuthorizationService.startReview(1L)).thenReturn(createResponse(PreAuthStatus.UNDER_REVIEW));

        mockMvc.perform(patch("/api/v1/preauth/1/start-review"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UNDER_REVIEW"));
    }

    @Test
    public void testResubmit() throws Exception {
        when(preAuthorizationService.resubmitPreAuthRequest(1L)).thenReturn(createResponse(PreAuthStatus.UNDER_REVIEW));

        mockMvc.perform(patch("/api/v1/preauth/1/resubmit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UNDER_REVIEW"));
    }

    @Test
    public void testAppeal() throws Exception {
        when(preAuthorizationService.submitPreAuthRequestAppeal(1L)).thenReturn(createResponse(PreAuthStatus.APPEAL_REVIEW));

        mockMvc.perform(patch("/api/v1/preauth/1/appeal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPEAL_REVIEW"));
    }

    @Test
    public void testGetPreAuthRequests() throws Exception {
        when(preAuthorizationService.getPreAuthRequests(eq(PreAuthStatus.DRAFT), eq(1L)))
                .thenReturn(List.of(createResponse(PreAuthStatus.DRAFT)));

        mockMvc.perform(get("/api/v1/preauth")
                .param("status", "DRAFT")
                .param("providerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("DRAFT"));
    }

    @Test
    public void testGetHistory() throws Exception {
        when(preAuthorizationService.getPreAuthRequestHistory(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/preauth/1/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
