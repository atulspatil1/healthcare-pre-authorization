package org.atulspatil1.healthcarepreauthorization.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.atulspatil1.healthcarepreauthorization.enums.Gender;
import org.atulspatil1.healthcarepreauthorization.enums.PolicyStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "member")
@Data
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "member_number", nullable = false, unique = true)
    private String memberNumber;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "policy_number", nullable = false, unique = true)
    private String policyNumber;

    @Column(name = "policy_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PolicyStatus policyStatus;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<PreAuthorization> preAuthorizations;
}
