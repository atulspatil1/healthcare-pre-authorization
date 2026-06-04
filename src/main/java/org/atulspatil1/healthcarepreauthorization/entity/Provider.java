package org.atulspatil1.healthcarepreauthorization.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.atulspatil1.healthcarepreauthorization.enums.NetworkStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "provider")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "provider_code", nullable = false, unique = true)
    private String providerCode;

    @Column(name = "hospital_name", nullable = false)
    private String hospitalName;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "network_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private NetworkStatus networkStatus;

    @Column(name = "contact_email", nullable = false)
    private String contactEmail;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "provider", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<PreAuthorization> preAuthorizations;
}
