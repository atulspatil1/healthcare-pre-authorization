package org.atulspatil1.healthcarepreauthorization.service;

import lombok.RequiredArgsConstructor;
import org.atulspatil1.healthcarepreauthorization.dto.response.DocumentResponseDto;
import org.atulspatil1.healthcarepreauthorization.entity.Document;
import org.atulspatil1.healthcarepreauthorization.entity.PreAuthorization;
import org.atulspatil1.healthcarepreauthorization.enums.DocumentType;
import org.atulspatil1.healthcarepreauthorization.exception.ResourceNotFoundException;
import org.atulspatil1.healthcarepreauthorization.repository.DocumentRepository;
import org.atulspatil1.healthcarepreauthorization.repository.PreAuthorizationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PreAuthorizationRepository preAuthorizationRepository;

    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;

    @Transactional
    public DocumentResponseDto uploadDocument(Long preAuthId, MultipartFile file, DocumentType documentType, String uploadedBy) {
        PreAuthorization preAuth = preAuthorizationRepository.findById(preAuthId)
                .orElseThrow(() -> new ResourceNotFoundException("PreAuthorization request not found with id: " + preAuthId));

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown");
        String extension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            extension = originalFileName.substring(i);
        }
        String storedFileName = UUID.randomUUID().toString() + extension;

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            Path targetLocation = uploadPath.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            Document document = new Document();
            document.setPreAuthorization(preAuth);
            document.setFileName(originalFileName);
            document.setDocumentType(documentType);
            document.setStoragePath(targetLocation.toString());
            document.setFileSize(file.getSize());
            document.setContentType(file.getContentType());
            document.setUploadedAt(LocalDateTime.now());
            document.setUploadedBy(uploadedBy);

            Document savedDoc = documentRepository.save(document);
            return mapToResponseDto(savedDoc);

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    public List<DocumentResponseDto> getDocumentsByPreAuthId(Long preAuthId) {
        // verify preAuth exists
        if (!preAuthorizationRepository.existsById(preAuthId)) {
            throw new ResourceNotFoundException("PreAuthorization request not found with id: " + preAuthId);
        }

        return documentRepository.findByPreAuthorizationId(preAuthId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public Resource downloadDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        try {
            Path filePath = Paths.get(document.getStoragePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found " + document.getFileName());
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found " + document.getFileName());
        }
    }

    public Document getDocumentEntity(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        try {
            Path filePath = Paths.get(document.getStoragePath()).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            // Log error but proceed with DB deletion
            System.err.println("Failed to delete file from disk: " + document.getStoragePath());
        }

        documentRepository.delete(document);
    }

    private DocumentResponseDto mapToResponseDto(Document document) {
        DocumentResponseDto dto = new DocumentResponseDto();
        dto.setId(document.getId());
        dto.setFileName(document.getFileName());
        dto.setDocumentType(document.getDocumentType());
        dto.setFileSize(document.getFileSize());
        dto.setContentType(document.getContentType());
        dto.setUploadedAt(document.getUploadedAt());
        return dto;
    }
}
