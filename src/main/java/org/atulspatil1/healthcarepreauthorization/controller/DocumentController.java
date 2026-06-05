package org.atulspatil1.healthcarepreauthorization.controller;

import lombok.RequiredArgsConstructor;
import org.atulspatil1.healthcarepreauthorization.dto.DocumentResponseDto;
import org.atulspatil1.healthcarepreauthorization.entity.Document;
import org.atulspatil1.healthcarepreauthorization.enums.DocumentType;
import org.atulspatil1.healthcarepreauthorization.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(value = "/preauth/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponseDto> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") DocumentType documentType,
            @RequestParam(value = "uploadedBy", defaultValue = "SYSTEM") String uploadedBy) {
        return ResponseEntity.ok(documentService.uploadDocument(id, file, documentType, uploadedBy));
    }

    @GetMapping("/preauth/{id}/documents")
    public ResponseEntity<List<DocumentResponseDto>> getDocumentsByPreAuth(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentsByPreAuthId(id));
    }

    @GetMapping("/documents/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        Resource resource = documentService.downloadDocument(id);
        Document document = documentService.getDocumentEntity(id);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getContentType() != null ? document.getContentType() : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
