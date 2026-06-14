package org.atulspatil1.healthcarepreauthorization.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atulspatil1.healthcarepreauthorization.dto.request.PreAuthorizationRequestDto;
import org.atulspatil1.healthcarepreauthorization.dto.response.PreAuthorizationResponseDto;
import org.atulspatil1.healthcarepreauthorization.dto.response.ReviewResponseDto;
import org.atulspatil1.healthcarepreauthorization.service.PreAuthorizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PreAuthorizationController {

    private final PreAuthorizationService preAuthorizationService;

    @PostMapping("/preauth")
    public ResponseEntity<PreAuthorizationResponseDto> createPreAuthRequest(@Valid @RequestBody PreAuthorizationRequestDto requestDto) {
        PreAuthorizationResponseDto response = preAuthorizationService.createPreAuthRequest(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/preauth/{id}")
    public ResponseEntity<PreAuthorizationResponseDto> getPreAuthRequest(@PathVariable Long id) {
        PreAuthorizationResponseDto response = preAuthorizationService.getPreAuthRequestById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/preauth/{id}/submit")
    public ResponseEntity<PreAuthorizationResponseDto> submitPreAuthRequestDraft(@PathVariable Long id) {
        PreAuthorizationResponseDto response = preAuthorizationService.submitPreAuthRequestDraft(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/preauth/{id}/start-review")
    public ResponseEntity<PreAuthorizationResponseDto> startReview(@PathVariable Long id) {
        PreAuthorizationResponseDto response = preAuthorizationService.startReview(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/preauth/{id}/resubmit")
    public ResponseEntity<PreAuthorizationResponseDto> resubmitPreAuthRequest(@PathVariable Long id) {
        PreAuthorizationResponseDto response = preAuthorizationService.resubmitPreAuthRequest(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/preauth/{id}/appeal")
    public ResponseEntity<PreAuthorizationResponseDto> submitPreAuthRequestAppeal(@PathVariable Long id) {
        PreAuthorizationResponseDto response = preAuthorizationService.submitPreAuthRequestAppeal(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/preauth")
    public ResponseEntity<List<PreAuthorizationResponseDto>> getPreAuthRequests(
            @RequestParam(required = false) org.atulspatil1.healthcarepreauthorization.enums.PreAuthStatus status,
            @RequestParam(required = false) Long providerId) {
        List<PreAuthorizationResponseDto> response = preAuthorizationService.getPreAuthRequests(status, providerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/preauth/{id}/history")
    public ResponseEntity<List<ReviewResponseDto>> getPreAuthRequestHistory(@PathVariable Long id) {
        return ResponseEntity.ok(preAuthorizationService.getPreAuthRequestHistory(id));
    }

    @PatchMapping("/preauth/{id}/close")
    public ResponseEntity<PreAuthorizationResponseDto> closePreAuthRequest(@PathVariable Long id) {
        PreAuthorizationResponseDto response = preAuthorizationService.closePreAuthRequest(id);
        return ResponseEntity.ok(response);
    }
}
