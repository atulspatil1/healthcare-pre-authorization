package org.atulspatil1.healthcarepreauthorization.controller;

import org.atulspatil1.healthcarepreauthorization.entity.Document;
import org.atulspatil1.healthcarepreauthorization.entity.PreAuthorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PreAuthorizationController {

    @PostMapping("/preauth")
    public ResponseEntity<PreAuthorization> createPreAuthRequest() {
        return null;
    }

    @GetMapping("/preauth/{id}")
    public ResponseEntity<PreAuthorization> getPreAuthRequest(@PathVariable Long id) {
        return null;
    }

    @GetMapping("/preauth")
    public ResponseEntity<List<PreAuthorization>> getPreAuthRequest(@RequestParam Map<String, String> params) {
        return null;
    }

    @PatchMapping("/preauth/{id}/submit")
    public ResponseEntity<PreAuthorization> submitPreAuthRequestDraft(@PathVariable Long id) {
        return null;
    }

    @PatchMapping("/preauth/{id}/review")
    public ResponseEntity<PreAuthorization> submitPreAuthRequestReview(@PathVariable Long id) {
        return null;
    }

    @PatchMapping("/preauth/{id}/resubmit")
    public ResponseEntity<PreAuthorization> resubmitPreAuthRequest(@PathVariable Long id) {
        return null;
    }

    @PatchMapping("/preauth/{id}/appeal")
    public ResponseEntity<PreAuthorization> submitPreAuthRequestAppeal(@PathVariable Long id) {
        return null;
    }

    @GetMapping("/preauth/{id}/history")
    public ResponseEntity<PreAuthorization> getPreAuthRequestHistory(@PathVariable Long id) {
        return null;
    }

    @PostMapping("/preauth/{id}/documents")
    public ResponseEntity<Document> uploadPreAuthRequestDocuments(@PathVariable Long id) {
        return null;
    }

    @GetMapping("/preauth/{id}/documents")
    public ResponseEntity<List<Document>> getPreAuthRequestDocuments(@PathVariable Long id) {
        return null;
    }
}
