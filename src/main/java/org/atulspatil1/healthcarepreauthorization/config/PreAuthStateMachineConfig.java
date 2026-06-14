package org.atulspatil1.healthcarepreauthorization.config;

import lombok.extern.slf4j.Slf4j;
import org.atulspatil1.healthcarepreauthorization.enums.PreAuthEvent;
import org.atulspatil1.healthcarepreauthorization.enums.PreAuthStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class PreAuthStateMachineConfig
        extends StateMachineConfigurerAdapter<PreAuthStatus, PreAuthEvent> {

    // ─── States ───────────────────────────────────────────────────────

    @Override
    public void configure(StateMachineStateConfigurer<PreAuthStatus, PreAuthEvent> states)
            throws Exception {
        states
                .withStates()
                .initial(PreAuthStatus.DRAFT)
                .end(PreAuthStatus.CLOSED)
                .states(EnumSet.allOf(PreAuthStatus.class));
    }

    // ─── Transitions ──────────────────────────────────────────────────

    @Override
    public void configure(StateMachineTransitionConfigurer<PreAuthStatus, PreAuthEvent> transitions)
            throws Exception {
        transitions
                // DRAFT → SUBMITTED
                .withExternal()
                    .source(PreAuthStatus.DRAFT)
                    .target(PreAuthStatus.SUBMITTED)
                    .event(PreAuthEvent.SUBMIT)
                .and()

                // SUBMITTED → UNDER_REVIEW
                .withExternal()
                    .source(PreAuthStatus.SUBMITTED)
                    .target(PreAuthStatus.UNDER_REVIEW)
                    .event(PreAuthEvent.START_REVIEW)
                .and()

                // UNDER_REVIEW → APPROVED
                .withExternal()
                    .source(PreAuthStatus.UNDER_REVIEW)
                    .target(PreAuthStatus.APPROVED)
                    .event(PreAuthEvent.APPROVE)
                .and()

                // UNDER_REVIEW → DENIED
                .withExternal()
                    .source(PreAuthStatus.UNDER_REVIEW)
                    .target(PreAuthStatus.DENIED)
                    .event(PreAuthEvent.DENY)
                .and()

                // UNDER_REVIEW → ADDITIONAL_INFO_REQUIRED
                .withExternal()
                    .source(PreAuthStatus.UNDER_REVIEW)
                    .target(PreAuthStatus.ADDITIONAL_INFO_REQUIRED)
                    .event(PreAuthEvent.REQUEST_INFO)
                .and()

                // ADDITIONAL_INFO_REQUIRED → UNDER_REVIEW (resubmit)
                .withExternal()
                    .source(PreAuthStatus.ADDITIONAL_INFO_REQUIRED)
                    .target(PreAuthStatus.UNDER_REVIEW)
                    .event(PreAuthEvent.RESUBMIT)
                .and()

                // DENIED → APPEAL_REVIEW
                .withExternal()
                    .source(PreAuthStatus.DENIED)
                    .target(PreAuthStatus.APPEAL_REVIEW)
                    .event(PreAuthEvent.APPEAL)
                .and()

                // APPEAL_REVIEW → APPROVED
                .withExternal()
                    .source(PreAuthStatus.APPEAL_REVIEW)
                    .target(PreAuthStatus.APPROVED)
                    .event(PreAuthEvent.APPROVE)
                .and()

                // APPEAL_REVIEW → DENIED
                .withExternal()
                    .source(PreAuthStatus.APPEAL_REVIEW)
                    .target(PreAuthStatus.DENIED)
                    .event(PreAuthEvent.DENY)
                .and()

                // APPROVED → CLOSED
                .withExternal()
                    .source(PreAuthStatus.APPROVED)
                    .target(PreAuthStatus.CLOSED)
                    .event(PreAuthEvent.CLOSE)
                .and()

                // DENIED → CLOSED
                .withExternal()
                    .source(PreAuthStatus.DENIED)
                    .target(PreAuthStatus.CLOSED)
                    .event(PreAuthEvent.CLOSE);
    }

    // ─── Configuration (logging listener) ─────────────────────────────

    @Override
    public void configure(StateMachineConfigurationConfigurer<PreAuthStatus, PreAuthEvent> config)
            throws Exception {
        config
                .withConfiguration()
                .autoStartup(false)
                .listener(new StateMachineListenerAdapter<>() {
                    @Override
                    public void stateChanged(State<PreAuthStatus, PreAuthEvent> from,
                                             State<PreAuthStatus, PreAuthEvent> to) {
                        if (from != null && to != null) {
                            log.info("State transition: {} → {}", from.getId(), to.getId());
                        }
                    }
                });
    }
}
