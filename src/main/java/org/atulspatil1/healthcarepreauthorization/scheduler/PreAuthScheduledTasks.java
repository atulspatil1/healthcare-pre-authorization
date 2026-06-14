package org.atulspatil1.healthcarepreauthorization.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atulspatil1.healthcarepreauthorization.entity.PreAuthorization;
import org.atulspatil1.healthcarepreauthorization.enums.PreAuthEvent;
import org.atulspatil1.healthcarepreauthorization.enums.PreAuthStatus;
import org.atulspatil1.healthcarepreauthorization.repository.PreAuthorizationRepository;
import org.atulspatil1.healthcarepreauthorization.service.StateMachineService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled tasks for automatic workflow actions.
 *
 * <ul>
 *     <li><b>Auto-expire:</b> Closes requests that have passed their expiration date.</li>
 *     <li><b>SLA escalation:</b> Logs warnings for requests that have breached their SLA deadline.</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PreAuthScheduledTasks {

    private final PreAuthorizationRepository preAuthorizationRepository;
    private final StateMachineService stateMachineService;

    /**
     * Auto-expire stale requests — runs daily at 2 AM.
     * Finds requests where expiresAt has passed and status is not already in a terminal state,
     * then sends a CLOSE event via the state machine.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void expireStaleRequests() {
        List<PreAuthStatus> terminalStatuses = List.of(PreAuthStatus.CLOSED);

        List<PreAuthorization> expired = preAuthorizationRepository
                .findByExpiresAtBeforeAndStatusNotIn(LocalDateTime.now(), terminalStatuses);

        log.info("Auto-expire check: found {} expired requests", expired.size());

        for (PreAuthorization preAuth : expired) {
            try {
                PreAuthStatus newStatus = stateMachineService.sendEvent(preAuth, PreAuthEvent.CLOSE);
                preAuth.setStatus(newStatus);
                preAuth.setUpdatedAt(LocalDateTime.now());
                preAuth.setUpdatedBy("SYSTEM_SCHEDULER");
                preAuthorizationRepository.save(preAuth);
                log.info("Auto-expired preAuth {} (was {})", preAuth.getRequestNumber(), preAuth.getStatus());
            } catch (IllegalStateException e) {
                // CLOSE is only valid from APPROVED or DENIED — skip others
                log.debug("Cannot auto-close preAuth {} in status {}: {}",
                        preAuth.getRequestNumber(), preAuth.getStatus(), e.getMessage());
            }
        }
    }

    /**
     * SLA escalation — runs every hour.
     * Finds SUBMITTED or UNDER_REVIEW requests where the SLA deadline has passed
     * and logs a warning. Full escalation (e.g., reassign to senior reviewer,
     * send notification) will be added when the Notification Service is built.
     */
    @Scheduled(fixedRate = 3600000) // Every hour
    public void escalateBreachedSLA() {
        List<PreAuthStatus> reviewStatuses = List.of(
                PreAuthStatus.SUBMITTED,
                PreAuthStatus.UNDER_REVIEW
        );

        List<PreAuthorization> breached = preAuthorizationRepository
                .findByStatusInAndSlaDeadlineBefore(reviewStatuses, LocalDateTime.now());

        if (!breached.isEmpty()) {
            log.warn("SLA escalation: {} requests have breached their SLA deadline", breached.size());
            for (PreAuthorization preAuth : breached) {
                log.warn("SLA BREACHED — preAuth: {}, status: {}, slaDeadline: {}, provider: {}",
                        preAuth.getRequestNumber(),
                        preAuth.getStatus(),
                        preAuth.getSlaDeadline(),
                        preAuth.getProvider().getId());
            }
        }
    }
}
