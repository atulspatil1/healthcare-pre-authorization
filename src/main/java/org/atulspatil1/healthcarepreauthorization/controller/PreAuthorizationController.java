package org.atulspatil1.healthcarepreauthorization.controller;

import lombok.RequiredArgsConstructor;
import org.atulspatil1.healthcarepreauthorization.dto.PreAuthorizationRequestDto;
import org.atulspatil1.healthcarepreauthorization.dto.PreAuthorizationResponseDto;
import org.atulspatil1.healthcarepreauthorization.entity.Document;
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
    public ResponseEntity<PreAuthorizationResponseDto> createPreAuthRequest(@RequestBody PreAuthorizationRequestDto requestDto) {
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

    @PatchMapping("/preauth/{id}/review")
    public ResponseEntity<PreAuthorizationResponseDto> submitPreAuthRequestReview(@PathVariable Long id) {
        PreAuthorizationResponseDto response = preAuthorizationService.submitPreAuthRequestReview(id);
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

    // Document and history methods left unimplemented for now
    @GetMapping("/preauth/{id}/history")
    public ResponseEntity<Object> getPreAuthRequestHistory(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/preauth/{id}/documents")
    public ResponseEntity<Document> uploadPreAuthRequestDocuments(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/preauth/{id}/documents")
    public ResponseEntity<List<Document>> getPreAuthRequestDocuments(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
