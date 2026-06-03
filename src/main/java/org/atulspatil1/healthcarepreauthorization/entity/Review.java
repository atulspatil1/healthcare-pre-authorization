package org.atulspatil1.healthcarepreauthorization.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.atulspatil1.healthcarepreauthorization.enums.Decision;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_auth_id")
    private PreAuthorization preAuthorization;

    @Column(name = "reviewer_id", nullable = false)
    private String reviewerId;

    @Column(name = "decision", nullable = false)
    @Enumerated(EnumType.STRING)
    private Decision decision;

    @Column(name = "approved_amount")
    private BigDecimal approvedAmount;

    @Column(name = "comments", nullable = false)
    private String comments;

    @Column(name = "review_date")
    private LocalDateTime reviewDate;
}
