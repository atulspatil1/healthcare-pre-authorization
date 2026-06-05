package org.atulspatil1.healthcarepreauthorization.controller;

import lombok.RequiredArgsConstructor;
import org.atulspatil1.healthcarepreauthorization.dto.PreAuthorizationResponseDto;
import org.atulspatil1.healthcarepreauthorization.dto.ReviewRequestDto;
import org.atulspatil1.healthcarepreauthorization.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PatchMapping("/preauth/{id}/review")
    public ResponseEntity<PreAuthorizationResponseDto> submitReview(
            @PathVariable Long id,
            @RequestBody ReviewRequestDto requestDto,
            @RequestParam(value = "reviewerId", defaultValue = "SYSTEM_REVIEWER") String reviewerId) {
        
        PreAuthorizationResponseDto response = reviewService.submitReview(id, requestDto, reviewerId);
        return ResponseEntity.ok(response);
    }
}
