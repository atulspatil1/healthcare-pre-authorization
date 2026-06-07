package org.atulspatil1.healthcarepreauthorization.service;

import org.atulspatil1.healthcarepreauthorization.dto.request.PreAuthorizationRequestDto;
import org.atulspatil1.healthcarepreauthorization.dto.response.PreAuthorizationResponseDto;
import org.atulspatil1.healthcarepreauthorization.entity.Member;
import org.atulspatil1.healthcarepreauthorization.entity.PreAuthorization;
import org.atulspatil1.healthcarepreauthorization.entity.Provider;
import org.atulspatil1.healthcarepreauthorization.entity.Review;
import org.atulspatil1.healthcarepreauthorization.enums.PreAuthStatus;
import org.atulspatil1.healthcarepreauthorization.enums.Priority;
import org.atulspatil1.healthcarepreauthorization.exception.ResourceNotFoundException;
import org.atulspatil1.healthcarepreauthorization.repository.MemberRepository;
import org.atulspatil1.healthcarepreauthorization.repository.PreAuthorizationRepository;
import org.atulspatil1.healthcarepreauthorization.repository.ProviderRepository;
import org.atulspatil1.healthcarepreauthorization.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PreAuthorizationServiceTest {

    @Mock
    private PreAuthorizationRepository preAuthorizationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private PreAuthorizationService preAuthorizationService;

    private Member createMember() {
        return Member.builder().id(1L).memberNumber("MEM-001").name("Test Member").build();
    }

    private Provider createProvider() {
        return Provider.builder().id(1L).providerCode("PROV-001").hospitalName("Test Hospital").build();
    }

    private PreAuthorization createPreAuth(PreAuthStatus status) {
        return PreAuthorization.builder()
                .id(1L)
                .requestNumber("REQ-12345678")
                .member(createMember())
                .provider(createProvider())
                .diagnosisCode("D001")
                .procedureCode("P001")
                .requestedAmount(new BigDecimal("5000.00"))
                .status(status)
                .priority(Priority.NORMAL)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();
    }

    // --- createPreAuthRequest ---

    @Test
    public void testCreatePreAuthRequest_Success() {
        PreAuthorizationRequestDto requestDto = new PreAuthorizationRequestDto();
        requestDto.setMemberId(1L);
        requestDto.setProviderId(1L);
        requestDto.setDiagnosisCode("D001");
        requestDto.setProcedureCode("P001");
        requestDto.setRequestedAmount(new BigDecimal("5000.00"));
        requestDto.setPriority(Priority.URGENT);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(createMember()));
        when(providerRepository.findById(1L)).thenReturn(Optional.of(createProvider()));
        when(preAuthorizationRepository.save(any(PreAuthorization.class))).thenAnswer(inv -> {
            PreAuthorization pa = inv.getArgument(0);
            pa.setId(1L);
            return pa;
        });

        PreAuthorizationResponseDto response = preAuthorizationService.createPreAuthRequest(requestDto);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(PreAuthStatus.DRAFT);
        assertThat(response.getPriority()).isEqualTo(Priority.URGENT);
        assertThat(response.getDiagnosisCode()).isEqualTo("D001");
        verify(preAuthorizationRepository).save(any(PreAuthorization.class));
    }

    @Test
    public void testCreatePreAuthRequest_DefaultPriority() {
        PreAuthorizationRequestDto requestDto = new PreAuthorizationRequestDto();
        requestDto.setMemberId(1L);
        requestDto.setProviderId(1L);
        requestDto.setDiagnosisCode("D001");
        requestDto.setRequestedAmount(new BigDecimal("3000.00"));
        // priority is null — should default to NORMAL

        when(memberRepository.findById(1L)).thenReturn(Optional.of(createMember()));
        when(providerRepository.findById(1L)).thenReturn(Optional.of(createProvider()));
        when(preAuthorizationRepository.save(any(PreAuthorization.class))).thenAnswer(inv -> {
            PreAuthorization pa = inv.getArgument(0);
            pa.setId(1L);
            return pa;
        });

        PreAuthorizationResponseDto response = preAuthorizationService.createPreAuthRequest(requestDto);

        assertThat(response.getPriority()).isEqualTo(Priority.NORMAL);
    }

    @Test
    public void testCreatePreAuthRequest_MemberNotFound() {
        PreAuthorizationRequestDto requestDto = new PreAuthorizationRequestDto();
        requestDto.setMemberId(99L);
        requestDto.setProviderId(1L);

        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                preAuthorizationService.createPreAuthRequest(requestDto));
    }

    @Test
    public void testCreatePreAuthRequest_ProviderNotFound() {
        PreAuthorizationRequestDto requestDto = new PreAuthorizationRequestDto();
        requestDto.setMemberId(1L);
        requestDto.setProviderId(99L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(createMember()));
        when(providerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                preAuthorizationService.createPreAuthRequest(requestDto));
    }

    // --- submitPreAuthRequestDraft ---

    @Test
    public void testSubmitDraft_Success() {
        PreAuthorization preAuth = createPreAuth(PreAuthStatus.DRAFT);
        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));
        when(preAuthorizationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PreAuthorizationResponseDto response = preAuthorizationService.submitPreAuthRequestDraft(1L);

        assertThat(response.getStatus()).isEqualTo(PreAuthStatus.SUBMITTED);
    }

    @Test
    public void testSubmitDraft_InvalidState() {
        PreAuthorization preAuth = createPreAuth(PreAuthStatus.APPROVED);
        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));

        assertThrows(IllegalStateException.class, () ->
                preAuthorizationService.submitPreAuthRequestDraft(1L));
    }

    // --- startReview ---

    @Test
    public void testStartReview_FromSubmitted() {
        PreAuthorization preAuth = createPreAuth(PreAuthStatus.SUBMITTED);
        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));
        when(preAuthorizationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PreAuthorizationResponseDto response = preAuthorizationService.startReview(1L);

        assertThat(response.getStatus()).isEqualTo(PreAuthStatus.UNDER_REVIEW);
    }

    @Test
    public void testStartReview_FromAdditionalInfoRequired() {
        PreAuthorization preAuth = createPreAuth(PreAuthStatus.ADDITIONAL_INFO_REQUIRED);
        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));
        when(preAuthorizationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PreAuthorizationResponseDto response = preAuthorizationService.startReview(1L);

        assertThat(response.getStatus()).isEqualTo(PreAuthStatus.UNDER_REVIEW);
    }

    @Test
    public void testStartReview_InvalidState() {
        PreAuthorization preAuth = createPreAuth(PreAuthStatus.DRAFT);
        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));

        assertThrows(IllegalStateException.class, () ->
                preAuthorizationService.startReview(1L));
    }

    // --- resubmitPreAuthRequest ---

    @Test
    public void testResubmit_Success() {
        PreAuthorization preAuth = createPreAuth(PreAuthStatus.ADDITIONAL_INFO_REQUIRED);
        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));
        when(preAuthorizationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PreAuthorizationResponseDto response = preAuthorizationService.resubmitPreAuthRequest(1L);

        assertThat(response.getStatus()).isEqualTo(PreAuthStatus.UNDER_REVIEW);
    }

    @Test
    public void testResubmit_InvalidState() {
        PreAuthorization preAuth = createPreAuth(PreAuthStatus.SUBMITTED);
        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));

        assertThrows(IllegalStateException.class, () ->
                preAuthorizationService.resubmitPreAuthRequest(1L));
    }

    // --- submitPreAuthRequestAppeal ---

    @Test
    public void testAppeal_Success() {
        PreAuthorization preAuth = createPreAuth(PreAuthStatus.DENIED);
        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));
        when(preAuthorizationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PreAuthorizationResponseDto response = preAuthorizationService.submitPreAuthRequestAppeal(1L);

        assertThat(response.getStatus()).isEqualTo(PreAuthStatus.APPEAL_REVIEW);
    }

    @Test
    public void testAppeal_InvalidState() {
        PreAuthorization preAuth = createPreAuth(PreAuthStatus.APPROVED);
        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));

        assertThrows(IllegalStateException.class, () ->
                preAuthorizationService.submitPreAuthRequestAppeal(1L));
    }

    // --- getPreAuthRequestById ---

    @Test
    public void testGetById_Success() {
        PreAuthorization preAuth = createPreAuth(PreAuthStatus.DRAFT);
        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));

        PreAuthorizationResponseDto response = preAuthorizationService.getPreAuthRequestById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getRequestNumber()).isEqualTo("REQ-12345678");
    }

    @Test
    public void testGetById_NotFound() {
        when(preAuthorizationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                preAuthorizationService.getPreAuthRequestById(99L));
    }

    // --- getPreAuthRequests (query branches) ---

    @Test
    public void testGetPreAuthRequests_ByStatusAndProvider() {
        when(preAuthorizationRepository.findByStatusAndProviderId(PreAuthStatus.DRAFT, 1L))
                .thenReturn(Collections.singletonList(createPreAuth(PreAuthStatus.DRAFT)));

        List<PreAuthorizationResponseDto> result = preAuthorizationService.getPreAuthRequests(PreAuthStatus.DRAFT, 1L);

        assertThat(result).hasSize(1);
        verify(preAuthorizationRepository).findByStatusAndProviderId(PreAuthStatus.DRAFT, 1L);
    }

    @Test
    public void testGetPreAuthRequests_ByStatusOnly() {
        when(preAuthorizationRepository.findByStatus(PreAuthStatus.SUBMITTED))
                .thenReturn(Collections.singletonList(createPreAuth(PreAuthStatus.SUBMITTED)));

        List<PreAuthorizationResponseDto> result = preAuthorizationService.getPreAuthRequests(PreAuthStatus.SUBMITTED, null);

        assertThat(result).hasSize(1);
        verify(preAuthorizationRepository).findByStatus(PreAuthStatus.SUBMITTED);
    }

    @Test
    public void testGetPreAuthRequests_ByProviderOnly() {
        when(preAuthorizationRepository.findByProviderId(1L))
                .thenReturn(Collections.singletonList(createPreAuth(PreAuthStatus.DRAFT)));

        List<PreAuthorizationResponseDto> result = preAuthorizationService.getPreAuthRequests(null, 1L);

        assertThat(result).hasSize(1);
        verify(preAuthorizationRepository).findByProviderId(1L);
    }

    @Test
    public void testGetPreAuthRequests_All() {
        when(preAuthorizationRepository.findAll())
                .thenReturn(Collections.singletonList(createPreAuth(PreAuthStatus.DRAFT)));

        List<PreAuthorizationResponseDto> result = preAuthorizationService.getPreAuthRequests(null, null);

        assertThat(result).hasSize(1);
        verify(preAuthorizationRepository).findAll();
    }

    // --- getPreAuthRequestHistory ---

    @Test
    public void testGetHistory_Success() {
        when(preAuthorizationRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.findByPreAuthorizationId(1L)).thenReturn(Collections.emptyList());

        List<Review> result = preAuthorizationService.getPreAuthRequestHistory(1L);

        assertThat(result).isEmpty();
    }

    @Test
    public void testGetHistory_NotFound() {
        when(preAuthorizationRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                preAuthorizationService.getPreAuthRequestHistory(99L));
    }
}
