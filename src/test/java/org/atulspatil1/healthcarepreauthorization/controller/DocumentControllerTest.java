package org.atulspatil1.healthcarepreauthorization.controller;

import org.atulspatil1.healthcarepreauthorization.dto.response.DocumentResponseDto;
import org.atulspatil1.healthcarepreauthorization.entity.Document;
import org.atulspatil1.healthcarepreauthorization.enums.DocumentType;
import org.atulspatil1.healthcarepreauthorization.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class DocumentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentController documentController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
    }

    @Test
    public void testUploadDocument() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "report.pdf", "application/pdf", "test content".getBytes());

        DocumentResponseDto response = new DocumentResponseDto();
        response.setId(1L);
        response.setFileName("report.pdf");
        response.setDocumentType(DocumentType.MEDICAL_REPORT);

        when(documentService.uploadDocument(eq(1L), any(), eq(DocumentType.MEDICAL_REPORT), eq("SYSTEM")))
                .thenReturn(response);

        mockMvc.perform(multipart("/api/v1/preauth/1/documents")
                .file(file)
                .param("documentType", "MEDICAL_REPORT")
                .param("uploadedBy", "SYSTEM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("report.pdf"));
    }

    @Test
    public void testGetDocumentsByPreAuth() throws Exception {
        DocumentResponseDto doc = new DocumentResponseDto();
        doc.setId(1L);
        doc.setFileName("doc.pdf");

        when(documentService.getDocumentsByPreAuthId(1L)).thenReturn(List.of(doc));

        mockMvc.perform(get("/api/v1/preauth/1/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fileName").value("doc.pdf"));
    }

    @Test
    public void testDownloadDocument() throws Exception {
        Resource resource = new ByteArrayResource("file content".getBytes());

        Document document = new Document();
        document.setId(1L);
        document.setFileName("download.pdf");
        document.setContentType("application/pdf");

        when(documentService.downloadDocument(1L)).thenReturn(resource);
        when(documentService.getDocumentEntity(1L)).thenReturn(document);

        mockMvc.perform(get("/api/v1/documents/1/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"download.pdf\""));
    }

    @Test
    public void testDownloadDocument_NullContentType() throws Exception {
        Resource resource = new ByteArrayResource("file content".getBytes());

        Document document = new Document();
        document.setId(1L);
        document.setFileName("download.pdf");
        document.setContentType(null);

        when(documentService.downloadDocument(1L)).thenReturn(resource);
        when(documentService.getDocumentEntity(1L)).thenReturn(document);

        mockMvc.perform(get("/api/v1/documents/1/download"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteDocument() throws Exception {
        mockMvc.perform(delete("/api/v1/documents/1"))
                .andExpect(status().isNoContent());

        verify(documentService).deleteDocument(1L);
    }
}
