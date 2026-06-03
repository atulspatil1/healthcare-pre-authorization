package org.atulspatil1.healthcarepreauthorization.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.atulspatil1.healthcarepreauthorization.enums.PreAuthStatus;
import org.atulspatil1.healthcarepreauthorization.enums.Priority;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pre_authorization")
@Data
public class PreAuthorization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "request_number", nullable = false, unique = true)
    private String requestNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @Column(name = "diagnosis_code", nullable = false)
    private String diagnosisCode;

    @Column(name = "procedure_code")
    private String procedureCode;

    @Column(name = "requested_amount", nullable = false)
    private BigDecimal requestedAmount;

    @Column(name = "approved_amount")
    private BigDecimal approvedAmount;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PreAuthStatus status;

    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "sla_deadline")
    private LocalDateTime slaDeadline;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "updated_by", nullable = false)
    private String updatedBy;

    @OneToMany(mappedBy = "preAuthorization", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Document> documents;

    @OneToMany(mappedBy = "preAuthorization", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Review> reviews;

}
