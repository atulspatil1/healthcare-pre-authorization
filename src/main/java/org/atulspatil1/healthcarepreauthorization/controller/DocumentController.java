package org.atulspatil1.healthcarepreauthorization.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class DocumentController {

    @GetMapping("/documents/{id}/download")
    public ResponseEntity<Void> downloadDocument(@PathVariable Long id) {
        return null;
    }

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        return null;
    }
}
