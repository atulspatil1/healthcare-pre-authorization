package org.atulspatil1.healthcarepreauthorization.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atulspatil1.healthcarepreauthorization.dto.request.ProviderRequestDto;
import org.atulspatil1.healthcarepreauthorization.dto.response.ProviderResponseDto;
import org.atulspatil1.healthcarepreauthorization.service.ProviderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping("/providers")
    public ResponseEntity<ProviderResponseDto> registerProvider(@Valid @RequestBody ProviderRequestDto request) {
        ProviderResponseDto response = providerService.registerProvider(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/providers/{id}")
    public ResponseEntity<ProviderResponseDto> getProvider(@PathVariable Long id) {
        ProviderResponseDto response = providerService.getProviderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/providers")
    public ResponseEntity<List<ProviderResponseDto>> lookupByCity(@RequestParam String city) {
        List<ProviderResponseDto> response = providerService.lookupByCity(city);
        return ResponseEntity.ok(response);
    }
}
