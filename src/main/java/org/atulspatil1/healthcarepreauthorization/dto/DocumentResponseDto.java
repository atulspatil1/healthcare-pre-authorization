package org.atulspatil1.healthcarepreauthorization.dto;

import lombok.Data;
import org.atulspatil1.healthcarepreauthorization.enums.DocumentType;

import java.time.LocalDateTime;

@Data
public class DocumentResponseDto {
    private Long id;
    private String fileName;
    private DocumentType documentType;
    private Long fileSize;
    private String contentType;
    private LocalDateTime uploadedAt;
}
