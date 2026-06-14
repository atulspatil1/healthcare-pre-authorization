package org.atulspatil1.healthcarepreauthorization.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atulspatil1.healthcarepreauthorization.dto.response.PreAuthorizationResponseDto;
import org.atulspatil1.healthcarepreauthorization.dto.request.ReviewRequestDto;
import org.atulspatil1.healthcarepreauthorization.entity.PreAuthorization;
import org.atulspatil1.healthcarepreauthorization.entity.Review;
import org.atulspatil1.healthcarepreauthorization.enums.PreAuthEvent;
import org.atulspatil1.healthcarepreauthorization.enums.PreAuthStatus;
import org.atulspatil1.healthcarepreauthorization.exception.ResourceNotFoundException;
import org.atulspatil1.healthcarepreauthorization.repository.PreAuthorizationRepository;
import org.atulspatil1.healthcarepreauthorization.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PreAuthorizationRepository preAuthorizationRepository;
    private final StateMachineService stateMachineService;

    @Transactional
    public PreAuthorizationResponseDto submitReview(Long preAuthId, ReviewRequestDto requestDto, String reviewerId) {
        PreAuthorization preAuth = preAuthorizationRepository.findById(preAuthId)
                .orElseThrow(() -> new ResourceNotFoundException("PreAuthorization request not found with id: " + preAuthId));

        // Map the reviewer's decision to a state machine event
        PreAuthEvent event = mapDecisionToEvent(requestDto);

        // State machine validates whether this transition is allowed
        // (e.g., only UNDER_REVIEW or APPEAL_REVIEW can receive APPROVE/DENY/REQUEST_INFO)
        PreAuthStatus newStatus = stateMachineService.sendEvent(preAuth, event);

        // Save the review record
        Review review = new Review();
        review.setPreAuthorization(preAuth);
        review.setReviewerId(reviewerId);
        review.setDecision(requestDto.getDecision());
        review.setApprovedAmount(requestDto.getApprovedAmount());
        review.setComments(requestDto.getComments());
        review.setReviewDate(LocalDateTime.now());
        reviewRepository.save(review);

        // Update the pre-authorization with the new status
        preAuth.setStatus(newStatus);
        if (newStatus == PreAuthStatus.APPROVED) {
            preAuth.setApprovedAmount(requestDto.getApprovedAmount());
        }
        preAuth.setUpdatedAt(LocalDateTime.now());
        preAuth.setUpdatedBy(reviewerId);

        PreAuthorization savedPreAuth = preAuthorizationRepository.save(preAuth);
        return mapToResponseDto(savedPreAuth);
    }

    /**
     * Maps a reviewer's Decision to the corresponding state machine event.
     */
    private PreAuthEvent mapDecisionToEvent(ReviewRequestDto requestDto) {
        return switch (requestDto.getDecision()) {
            case APPROVED, PARTIALLY_APPROVED -> PreAuthEvent.APPROVE;
            case DENIED -> PreAuthEvent.DENY;
            case INFO_REQUESTED -> PreAuthEvent.REQUEST_INFO;
        };
    }

    private PreAuthorizationResponseDto mapToResponseDto(PreAuthorization preAuth) {
        return PreAuthorizationResponseDto.builder()
                .id(preAuth.getId())
                .requestNumber(preAuth.getRequestNumber())
                .memberId(preAuth.getMember().getId())
                .providerId(preAuth.getProvider().getId())
                .diagnosisCode(preAuth.getDiagnosisCode())
                .procedureCode(preAuth.getProcedureCode())
                .requestedAmount(preAuth.getRequestedAmount())
                .approvedAmount(preAuth.getApprovedAmount())
                .status(preAuth.getStatus())
                .priority(preAuth.getPriority())
                .expiresAt(preAuth.getExpiresAt())
                .slaDeadline(preAuth.getSlaDeadline())
                .createdAt(preAuth.getCreatedAt())
                .updatedAt(preAuth.getUpdatedAt())
                .build();
    }
}
