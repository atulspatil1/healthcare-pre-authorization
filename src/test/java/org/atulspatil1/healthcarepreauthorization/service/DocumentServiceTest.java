package org.atulspatil1.healthcarepreauthorization.service;

import org.atulspatil1.healthcarepreauthorization.dto.response.DocumentResponseDto;
import org.atulspatil1.healthcarepreauthorization.entity.Document;
import org.atulspatil1.healthcarepreauthorization.entity.PreAuthorization;
import org.atulspatil1.healthcarepreauthorization.enums.DocumentType;
import org.atulspatil1.healthcarepreauthorization.exception.ResourceNotFoundException;
import org.atulspatil1.healthcarepreauthorization.repository.DocumentRepository;
import org.atulspatil1.healthcarepreauthorization.repository.PreAuthorizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private PreAuthorizationRepository preAuthorizationRepository;

    @InjectMocks
    private DocumentService documentService;

    @TempDir
    Path tempDir;

    @Test
    public void testUploadDocument_Success() throws IOException {
        ReflectionTestUtils.setField(documentService, "uploadDir", tempDir.toString());

        PreAuthorization preAuth = PreAuthorization.builder().id(1L).build();
        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("report.pdf");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("test content".getBytes()));
        when(file.getSize()).thenReturn(12L);
        when(file.getContentType()).thenReturn("application/pdf");

        Document savedDoc = new Document();
        savedDoc.setId(1L);
        savedDoc.setFileName("report.pdf");
        savedDoc.setDocumentType(DocumentType.MEDICAL_REPORT);
        savedDoc.setFileSize(12L);
        savedDoc.setContentType("application/pdf");
        savedDoc.setUploadedAt(LocalDateTime.now());

        when(documentRepository.save(any(Document.class))).thenReturn(savedDoc);

        DocumentResponseDto response = documentService.uploadDocument(1L, file, DocumentType.MEDICAL_REPORT, "user1");

        assertThat(response).isNotNull();
        assertThat(response.getFileName()).isEqualTo("report.pdf");
        assertThat(response.getDocumentType()).isEqualTo(DocumentType.MEDICAL_REPORT);
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    public void testUploadDocument_PreAuthNotFound() {
        when(preAuthorizationRepository.findById(99L)).thenReturn(Optional.empty());

        MultipartFile file = mock(MultipartFile.class);

        assertThrows(ResourceNotFoundException.class, () ->
                documentService.uploadDocument(99L, file, DocumentType.BILL, "user1"));
    }

    @Test
    public void testUploadDocument_NullFilename() throws IOException {
        ReflectionTestUtils.setField(documentService, "uploadDir", tempDir.toString());

        PreAuthorization preAuth = PreAuthorization.builder().id(1L).build();
        when(preAuthorizationRepository.findById(1L)).thenReturn(Optional.of(preAuth));

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(null);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));
        when(file.getSize()).thenReturn(4L);

        Document savedDoc = new Document();
        savedDoc.setId(1L);
        savedDoc.setFileName("unknown");
        savedDoc.setDocumentType(DocumentType.BILL);
        savedDoc.setUploadedAt(LocalDateTime.now());

        when(documentRepository.save(any(Document.class))).thenReturn(savedDoc);

        DocumentResponseDto response = documentService.uploadDocument(1L, file, DocumentType.BILL, "user1");

        assertThat(response).isNotNull();
    }

    @Test
    public void testGetDocumentsByPreAuthId_Success() {
        when(preAuthorizationRepository.existsById(1L)).thenReturn(true);

        Document doc = new Document();
        doc.setId(1L);
        doc.setFileName("doc.pdf");
        doc.setDocumentType(DocumentType.PRESCRIPTION);
        doc.setUploadedAt(LocalDateTime.now());

        when(documentRepository.findByPreAuthorizationId(1L)).thenReturn(List.of(doc));

        List<DocumentResponseDto> result = documentService.getDocumentsByPreAuthId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFileName()).isEqualTo("doc.pdf");
    }

    @Test
    public void testGetDocumentsByPreAuthId_PreAuthNotFound() {
        when(preAuthorizationRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                documentService.getDocumentsByPreAuthId(99L));
    }

    @Test
    public void testDownloadDocument_Success() throws IOException {
        Path testFile = tempDir.resolve("testfile.pdf");
        Files.write(testFile, "file content".getBytes());

        Document document = new Document();
        document.setId(1L);
        document.setFileName("testfile.pdf");
        document.setStoragePath(testFile.toString());

        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        Resource resource = documentService.downloadDocument(1L);

        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();
    }

    @Test
    public void testDownloadDocument_NotFound() {
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                documentService.downloadDocument(99L));
    }

    @Test
    public void testGetDocumentEntity_Success() {
        Document document = new Document();
        document.setId(1L);
        document.setFileName("file.pdf");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        Document result = documentService.getDocumentEntity(1L);

        assertThat(result.getFileName()).isEqualTo("file.pdf");
    }

    @Test
    public void testGetDocumentEntity_NotFound() {
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                documentService.getDocumentEntity(99L));
    }

    @Test
    public void testDeleteDocument_Success() throws IOException {
        Path testFile = tempDir.resolve("deleteme.pdf");
        Files.write(testFile, "to delete".getBytes());

        Document document = new Document();
        document.setId(1L);
        document.setFileName("deleteme.pdf");
        document.setStoragePath(testFile.toString());

        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        documentService.deleteDocument(1L);

        verify(documentRepository).delete(document);
        assertThat(Files.exists(testFile)).isFalse();
    }

    @Test
    public void testDeleteDocument_NotFound() {
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                documentService.deleteDocument(99L));
    }
}
