package org.atulspatil1.healthcarepreauthorization.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atulspatil1.healthcarepreauthorization.dto.request.ReviewRequestDto;
import org.atulspatil1.healthcarepreauthorization.dto.response.PreAuthorizationResponseDto;
import org.atulspatil1.healthcarepreauthorization.enums.Decision;
import org.atulspatil1.healthcarepreauthorization.enums.PreAuthStatus;
import org.atulspatil1.healthcarepreauthorization.service.ReviewService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSubmitReview() throws Exception {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setDecision(Decision.APPROVED);
        requestDto.setApprovedAmount(new BigDecimal("5000.00"));
        requestDto.setComments("Approved");

        PreAuthorizationResponseDto response = PreAuthorizationResponseDto.builder()
                .id(1L)
                .requestNumber("REQ-TEST")
                .status(PreAuthStatus.APPROVED)
                .build();

        when(reviewService.submitReview(eq(1L), any(ReviewRequestDto.class), eq("reviewer1")))
                .thenReturn(response);

        mockMvc.perform(patch("/api/v1/preauth/1/review")
                .param("reviewerId", "reviewer1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    public void testSubmitReview_DefaultReviewer() throws Exception {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setDecision(Decision.DENIED);
        requestDto.setComments("Denied");

        PreAuthorizationResponseDto response = PreAuthorizationResponseDto.builder()
                .id(1L)
                .status(PreAuthStatus.DENIED)
                .build();

        when(reviewService.submitReview(eq(1L), any(ReviewRequestDto.class), eq("SYSTEM_REVIEWER")))
                .thenReturn(response);

        mockMvc.perform(patch("/api/v1/preauth/1/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DENIED"));
    }
}
