package org.atulspatil1.healthcarepreauthorization.service;

import lombok.RequiredArgsConstructor;
import org.atulspatil1.healthcarepreauthorization.dto.request.PreAuthorizationRequestDto;
import org.atulspatil1.healthcarepreauthorization.dto.response.PreAuthorizationResponseDto;
import org.atulspatil1.healthcarepreauthorization.dto.response.ReviewResponseDto;
import org.atulspatil1.healthcarepreauthorization.entity.Member;
import org.atulspatil1.healthcarepreauthorization.entity.PreAuthorization;
import org.atulspatil1.healthcarepreauthorization.entity.Review;
import org.atulspatil1.healthcarepreauthorization.entity.Provider;
import org.atulspatil1.healthcarepreauthorization.enums.PreAuthStatus;
import org.atulspatil1.healthcarepreauthorization.enums.Priority;
import org.atulspatil1.healthcarepreauthorization.exception.ResourceNotFoundException;
import org.atulspatil1.healthcarepreauthorization.repository.MemberRepository;
import org.atulspatil1.healthcarepreauthorization.repository.PreAuthorizationRepository;
import org.atulspatil1.healthcarepreauthorization.repository.ProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PreAuthorizationService {

    private final PreAuthorizationRepository preAuthorizationRepository;
    private final MemberRepository memberRepository;
    private final ProviderRepository providerRepository;
    private final org.atulspatil1.healthcarepreauthorization.repository.ReviewRepository reviewRepository;

    @Transactional
    public PreAuthorizationResponseDto createPreAuthRequest(PreAuthorizationRequestDto requestDto) {
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + requestDto.getMemberId()));
        
        Provider provider = providerRepository.findById(requestDto.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with id: " + requestDto.getProviderId()));

        String requestNumber = "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDateTime now = LocalDateTime.now();

        PreAuthorization preAuth = PreAuthorization.builder()
                .requestNumber(requestNumber)
                .member(member)
                .provider(provider)
                .diagnosisCode(requestDto.getDiagnosisCode())
                .procedureCode(requestDto.getProcedureCode())
                .requestedAmount(requestDto.getRequestedAmount())
                .status(PreAuthStatus.DRAFT)
                .priority(requestDto.getPriority() != null ? requestDto.getPriority() : Priority.NORMAL)
                .expiresAt(now.plusDays(90))
                .slaDeadline(now.plusDays(60))
                .createdAt(now)
                .updatedAt(now)
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();

        PreAuthorization savedPreAuth = preAuthorizationRepository.save(preAuth);
        return mapToResponseDto(savedPreAuth);
    }

    @Transactional
    public PreAuthorizationResponseDto submitPreAuthRequestDraft(Long id) {
        PreAuthorization preAuth = getPreAuthorizationEntity(id);
        
        if (preAuth.getStatus() != PreAuthStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT requests can be submitted. Current status: " + preAuth.getStatus());
        }
        
        preAuth.setStatus(PreAuthStatus.SUBMITTED);
        preAuth.setUpdatedAt(LocalDateTime.now());
        
        return mapToResponseDto(preAuthorizationRepository.save(preAuth));
    }


    @Transactional
    public PreAuthorizationResponseDto startReview(Long id) {
        PreAuthorization preAuth = getPreAuthorizationEntity(id);
        
        if (preAuth.getStatus() != PreAuthStatus.SUBMITTED && preAuth.getStatus() != PreAuthStatus.ADDITIONAL_INFO_REQUIRED) {
            throw new IllegalStateException("Request cannot be picked up for review from current status: " + preAuth.getStatus());
        }
        
        preAuth.setStatus(PreAuthStatus.UNDER_REVIEW);
        preAuth.setUpdatedAt(LocalDateTime.now());
        
        return mapToResponseDto(preAuthorizationRepository.save(preAuth));
    }

    @Transactional
    public PreAuthorizationResponseDto resubmitPreAuthRequest(Long id) {
        PreAuthorization preAuth = getPreAuthorizationEntity(id);
        
        if (preAuth.getStatus() != PreAuthStatus.ADDITIONAL_INFO_REQUIRED) {
            throw new IllegalStateException("Only requests requiring additional info can be resubmitted. Current status: " + preAuth.getStatus());
        }
        
        preAuth.setStatus(PreAuthStatus.UNDER_REVIEW);
        preAuth.setUpdatedAt(LocalDateTime.now());
        
        return mapToResponseDto(preAuthorizationRepository.save(preAuth));
    }

    @Transactional
    public PreAuthorizationResponseDto submitPreAuthRequestAppeal(Long id) {
        PreAuthorization preAuth = getPreAuthorizationEntity(id);
        
        if (preAuth.getStatus() != PreAuthStatus.DENIED) {
            throw new IllegalStateException("Only DENIED requests can be appealed. Current status: " + preAuth.getStatus());
        }
        
        preAuth.setStatus(PreAuthStatus.APPEAL_REVIEW);
        preAuth.setUpdatedAt(LocalDateTime.now());
        
        return mapToResponseDto(preAuthorizationRepository.save(preAuth));
    }

    public PreAuthorizationResponseDto getPreAuthRequestById(Long id) {
        return mapToResponseDto(getPreAuthorizationEntity(id));
    }

    public List<PreAuthorizationResponseDto> getPreAuthRequests(PreAuthStatus status, Long providerId) {
        List<PreAuthorization> requests;
        if (status != null && providerId != null) {
            requests = preAuthorizationRepository.findByStatusAndProviderId(status, providerId);
        } else if (status != null) {
            requests = preAuthorizationRepository.findByStatus(status);
        } else if (providerId != null) {
            requests = preAuthorizationRepository.findByProviderId(providerId);
        } else {
            requests = preAuthorizationRepository.findAll();
        }
        return requests.stream().map(this::mapToResponseDto).toList();
    }

    public List<ReviewResponseDto> getPreAuthRequestHistory(Long id) {
        if (!preAuthorizationRepository.existsById(id)) {
            throw new ResourceNotFoundException("PreAuthorization request not found with id: " + id);
        }
        return reviewRepository.findByPreAuthorizationId(id)
                .stream()
                .map(this::mapToReviewResponseDto)
                .toList();
    }

    private PreAuthorization getPreAuthorizationEntity(Long id) {
        return preAuthorizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PreAuthorization request not found with id: " + id));
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

    private ReviewResponseDto mapToReviewResponseDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .preAuthorizationId(review.getPreAuthorization().getId())
                .reviewerId(review.getReviewerId())
                .decision(review.getDecision())
                .approvedAmount(review.getApprovedAmount())
                .comments(review.getComments())
                .reviewDate(review.getReviewDate())
                .build();
    }
}
