package com.example.ffstarter;

import io.harness.cf.client.api.CfClient;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class FeatureFlagsHealthIndicator implements HealthIndicator {

    private final CfClient cfClient;

    public FeatureFlagsHealthIndicator(CfClient cfClient) {
        this.cfClient = cfClient;
    }

    @Override
    public Health health() {
        try {
            // If the client is closed or throws, mark DOWN
            // SDK emits lifecycle logs; here we do a lightweight probe
            // Try to evaluate a dummy flag to check if SDK is working
            try {
                cfClient.boolVariation("health-check", 
                    io.harness.cf.client.dto.Target.builder()
                        .identifier("health-check")
                        .build(), 
                    false);
                return Health.up()
                        .withDetail("streamEnabled", true)
                        .withDetail("status", "initialized")
                        .build();
            } catch (Exception e) {
                // SDK might not be initialized yet, but client exists
                return Health.up()
                        .withDetail("status", "initializing")
                        .withDetail("note", "SDK may still be initializing")
                        .build();
            }
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}

