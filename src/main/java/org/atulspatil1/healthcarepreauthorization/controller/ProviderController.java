package org.atulspatil1.healthcarepreauthorization.controller;

import org.atulspatil1.healthcarepreauthorization.entity.Provider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ProviderController {

    @PostMapping("/providers")
    public ResponseEntity<Provider> registerProvider(@RequestBody Provider provider) {
        return null;
    }

    @GetMapping("/providers/{id}")
    public ResponseEntity<Provider> getProvider(@PathVariable Long id) {
        return null;
    }
}
