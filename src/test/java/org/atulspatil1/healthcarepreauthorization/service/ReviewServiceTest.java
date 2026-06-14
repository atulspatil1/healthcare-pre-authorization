package org.atulspatil1.healthcarepreauthorization.service;

import org.atulspatil1.healthcarepreauthorization.dto.request.ReviewRequestDto;
import org.atulspatil1.healthcarepreauthorization.dto.response.PreAuthorizationResponseDto;
import org.atulspatil1.healthcarepreauthorization.entity.Member;
import org.atulspatil1.healthcarepreauthorization.entity.PreAuthorization;
import org.atulspatil1.healthcarepreauthorization.entity.Provider;
import org.atulspatil1.healthcarepreauthorization.entity.Review;
import org.atulspatil1.healthcarepreauthorization.enums.Decision;
import org.atulspatil1.healthcarepreauthorization.enums.PreAuthStatus;
import org.atulspatil1.healthcarepreauthorization.enums.Priority;
import org.atulspatil1.healthcarepreauthorization.exception.ResourceNotFoundException;
import org.atulspatil1.healthcarepreauthorization.repository.PreAuthorizationRepository;
import org.atulspatil1.healthcarepreauthorization.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private PreAuthorizationRepository preAuthorizationRepository;

    @InjectMocks
    private ReviewService reviewService;

    private PreAuthorization createPreAuth(PreAuthStatus status) {
        return PreAuthorization.builder()
                .id(1L)
                .requestNumber("REQ-TEST")
                .member(Member.builder().id(1L).build())
                .provider(Provider.builder().id(1L).build())
                .diagnosisCode("D001")
                .requestedAmount(new BigDecimal("5000.00"))
                .status(status)
                .priority(Priority.NORMAL)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();
    }

    @Test
    public void testSubmitReview_Approved() {
        PreAuthorization preAuth = createPreAuth(PreAuthStatus.UNDER_REVIEW);
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setDecision(Decision.APPROVED);
        requestDto.setApprovedAmount(new BigDecimal("5000.00"));
        requestDto.setComments("Looks good");

        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));
        when(preAuthorizationRepository.save(any(PreAuthorization.class))).thenAnswer(inv -> inv.getArgument(0));

        PreAuthorizationResponseDto response = reviewService.submitReview(1L, requestDto, "reviewer1");

        assertThat(response.getStatus()).isEqualTo(PreAuthStatus.APPROVED);
        assertThat(response.getApprovedAmount()).isEqualTo(new BigDecimal("5000.00"));

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewCaptor.capture());
        assertThat(reviewCaptor.getValue().getReviewerId()).isEqualTo("reviewer1");
    }

    @Test
    public void testSubmitReview_PartiallyApproved() {
        PreAuthorization preAuth = createPreAuth(PreAuthStatus.UNDER_REVIEW);
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setDecision(Decision.PARTIALLY_APPROVED);
        requestDto.setApprovedAmount(new BigDecimal("3000.00"));
        requestDto.setComments("Partial approval");

        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));
        when(preAuthorizationRepository.save(any(PreAuthorization.class))).thenAnswer(inv -> inv.getArgument(0));

        PreAuthorizationResponseDto response = reviewService.submitReview(1L, requestDto, "reviewer1");

        assertThat(response.getStatus()).isEqualTo(PreAuthStatus.APPROVED);
        assertThat(response.getApprovedAmount()).isEqualTo(new BigDecimal("3000.00"));
    }

    @Test
    public void testSubmitReview_Denied() {
        PreAuthorization preAuth = createPreAuth(PreAuthStatus.UNDER_REVIEW);
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setDecision(Decision.DENIED);
        requestDto.setComments("Does not meet criteria");

        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));
        when(preAuthorizationRepository.save(any(PreAuthorization.class))).thenAnswer(inv -> inv.getArgument(0));

        PreAuthorizationResponseDto response = reviewService.submitReview(1L, requestDto, "reviewer1");

        assertThat(response.getStatus()).isEqualTo(PreAuthStatus.DENIED);
    }

    @Test
    public void testSubmitReview_InfoRequested() {
        PreAuthorization preAuth = createPreAuth(PreAuthStatus.UNDER_REVIEW);
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setDecision(Decision.INFO_REQUESTED);
        requestDto.setComments("Need more documents");

        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));
        when(preAuthorizationRepository.save(any(PreAuthorization.class))).thenAnswer(inv -> inv.getArgument(0));

        PreAuthorizationResponseDto response = reviewService.submitReview(1L, requestDto, "reviewer1");

        assertThat(response.getStatus()).isEqualTo(PreAuthStatus.ADDITIONAL_INFO_REQUIRED);
    }

    @Test
    public void testSubmitReview_FromAppealReview() {
        PreAuthorization preAuth = createPreAuth(PreAuthStatus.APPEAL_REVIEW);
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setDecision(Decision.APPROVED);
        requestDto.setApprovedAmount(new BigDecimal("5000.00"));
        requestDto.setComments("Appeal accepted");

        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));
        when(preAuthorizationRepository.save(any(PreAuthorization.class))).thenAnswer(inv -> inv.getArgument(0));

        PreAuthorizationResponseDto response = reviewService.submitReview(1L, requestDto, "reviewer1");

        assertThat(response.getStatus()).isEqualTo(PreAuthStatus.APPROVED);
    }

    @Test
    public void testSubmitReview_InvalidState() {
        PreAuthorization preAuth = createPreAuth(PreAuthStatus.DRAFT);
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setDecision(Decision.APPROVED);
        requestDto.setComments("Invalid");

        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));

        assertThrows(IllegalStateException.class, () ->
                reviewService.submitReview(1L, requestDto, "reviewer1"));
    }

    @Test
    public void testSubmitReview_PreAuthNotFound() {
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setDecision(Decision.APPROVED);

        when(preAuthorizationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                reviewService.submitReview(99L, requestDto, "reviewer1"));
    }
}
