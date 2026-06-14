package org.atulspatil1.healthcarepreauthorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HealthcarePreAuthorizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthcarePreAuthorizationApplication.class, args);
    }

}
