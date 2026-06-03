package org.atulspatil1.healthcarepreauthorization.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.atulspatil1.healthcarepreauthorization.enums.DocumentType;

import java.time.LocalDateTime;

@Entity
@Table(name = "document")
@Data
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_auth_id")
    private PreAuthorization preAuthorization;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "document_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "uploaded_by")
    private String uploadedBy;
}
