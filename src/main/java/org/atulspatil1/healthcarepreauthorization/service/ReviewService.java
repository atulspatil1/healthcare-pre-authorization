package org.atulspatil1.healthcarepreauthorization.service;

import lombok.RequiredArgsConstructor;
import org.atulspatil1.healthcarepreauthorization.dto.response.PreAuthorizationResponseDto;
import org.atulspatil1.healthcarepreauthorization.dto.request.ReviewRequestDto;
import org.atulspatil1.healthcarepreauthorization.entity.PreAuthorization;
import org.atulspatil1.healthcarepreauthorization.entity.Review;
import org.atulspatil1.healthcarepreauthorization.enums.PreAuthStatus;
import org.atulspatil1.healthcarepreauthorization.exception.ResourceNotFoundException;
import org.atulspatil1.healthcarepreauthorization.repository.PreAuthorizationRepository;
import org.atulspatil1.healthcarepreauthorization.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PreAuthorizationRepository preAuthorizationRepository;

    @Transactional
    public PreAuthorizationResponseDto submitReview(Long preAuthId, ReviewRequestDto requestDto, String reviewerId) {
        PreAuthorization preAuth = preAuthorizationRepository.findById(preAuthId)
                .orElseThrow(() -> new ResourceNotFoundException("PreAuthorization request not found with id: " + preAuthId));

        if (preAuth.getStatus() != PreAuthStatus.UNDER_REVIEW && preAuth.getStatus() != PreAuthStatus.APPEAL_REVIEW) {
            throw new IllegalStateException("Cannot review a request in status: " + preAuth.getStatus());
        }

        Review review = new Review();
        review.setPreAuthorization(preAuth);
        review.setReviewerId(reviewerId);
        review.setDecision(requestDto.getDecision());
        review.setApprovedAmount(requestDto.getApprovedAmount());
        review.setComments(requestDto.getComments());
        review.setReviewDate(LocalDateTime.now());

        reviewRepository.save(review);

        // Update PreAuthorization status based on decision
        switch (requestDto.getDecision()) {
            case APPROVED:
            case PARTIALLY_APPROVED:
                preAuth.setStatus(PreAuthStatus.APPROVED);
                preAuth.setApprovedAmount(requestDto.getApprovedAmount());
                break;
            case DENIED:
                preAuth.setStatus(PreAuthStatus.DENIED);
                break;
            case INFO_REQUESTED:
                preAuth.setStatus(PreAuthStatus.ADDITIONAL_INFO_REQUIRED);
                break;
        }

        preAuth.setUpdatedAt(LocalDateTime.now());
        preAuth.setUpdatedBy(reviewerId);
        
        PreAuthorization savedPreAuth = preAuthorizationRepository.save(preAuth);
        return mapToResponseDto(savedPreAuth);
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
