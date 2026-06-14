package org.atulspatil1.healthcarepreauthorization.repository;

import org.atulspatil1.healthcarepreauthorization.entity.PreAuthorization;
import org.atulspatil1.healthcarepreauthorization.enums.PreAuthStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PreAuthorizationRepository extends JpaRepository<PreAuthorization, Long> {
    Optional<PreAuthorization> findByRequestNumber(String requestNumber);
    List<PreAuthorization> findByStatus(PreAuthStatus status);
    List<PreAuthorization> findByProviderId(Long providerId);
    List<PreAuthorization> findByStatusAndProviderId(PreAuthStatus status, Long providerId);

    // For auto-expiration scheduled task
    List<PreAuthorization> findByExpiresAtBeforeAndStatusNotIn(LocalDateTime dateTime, List<PreAuthStatus> excludedStatuses);

    // For SLA escalation scheduled task
    List<PreAuthorization> findByStatusInAndSlaDeadlineBefore(List<PreAuthStatus> statuses, LocalDateTime dateTime);
}
