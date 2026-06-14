package org.atulspatil1.healthcarepreauthorization.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atulspatil1.healthcarepreauthorization.entity.PreAuthorization;
import org.atulspatil1.healthcarepreauthorization.enums.PreAuthEvent;
import org.atulspatil1.healthcarepreauthorization.enums.PreAuthStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Orchestrates the hydrate → process → dehydrate pattern for the PreAuth state machine.
 *
 * <p>The state machine is stateless per request — the current state lives in the
 * {@code status} column of the {@code pre_authorization} table. This service:
 * <ol>
 *     <li>Creates a state machine instance from the factory</li>
 *     <li>Restores (hydrates) it to the entity's current status</li>
 *     <li>Sends an event and checks if the transition was accepted</li>
 *     <li>Returns the new status for the caller to persist (dehydrate)</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StateMachineService {

    private final StateMachineFactory<PreAuthStatus, PreAuthEvent> stateMachineFactory;

    /**
     * Sends an event to a state machine hydrated from the given entity's current status.
     *
     * @param preAuth the entity whose status is the current state
     * @param event   the event to send
     * @return the new status after transition
     * @throws IllegalStateException if the transition is rejected by the state machine
     */
    public PreAuthStatus sendEvent(PreAuthorization preAuth, PreAuthEvent event) {
        StateMachine<PreAuthStatus, PreAuthEvent> sm = build(preAuth);

        Message<PreAuthEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader("preAuthId", preAuth.getId())
                .build();

        // Send event reactively and block for result
        sm.sendEvent(Mono.just(message)).blockLast();

        // Check if the state actually changed
        PreAuthStatus newStatus = sm.getState().getId();

        if (newStatus == preAuth.getStatus()) {
            // State didn't change — transition was rejected
            log.warn("State machine rejected event {} from state {} for preAuth {}",
                    event, preAuth.getStatus(), preAuth.getId());
            throw new IllegalStateException(
                    String.format("Cannot %s a request in status %s", event, preAuth.getStatus()));
        }

        log.info("PreAuth {} transitioned: {} → {} via event {}",
                preAuth.getId(), preAuth.getStatus(), newStatus, event);

        sm.stopReactively().block();
        return newStatus;
    }

    /**
     * Builds a state machine and restores it to the entity's current status.
     */
    private StateMachine<PreAuthStatus, PreAuthEvent> build(PreAuthorization preAuth) {
        StateMachine<PreAuthStatus, PreAuthEvent> sm =
                stateMachineFactory.getStateMachine(preAuth.getId().toString());

        sm.stopReactively().block();

        sm.getStateMachineAccessor()
                .doWithAllRegions(accessor -> accessor
                        .resetStateMachineReactively(
                                new DefaultStateMachineContext<>(
                                        preAuth.getStatus(), null, null, null))
                        .block());

        sm.startReactively().block();
        return sm;
    }
}
