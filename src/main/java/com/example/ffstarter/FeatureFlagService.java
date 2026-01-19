package com.example.ffstarter;

import io.harness.cf.client.api.CfClient;
import io.harness.cf.client.dto.Target;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class FeatureFlagService {

    private final CfClient cfClient;

    public FeatureFlagService(CfClient cfClient) {
        this.cfClient = cfClient;
    }

    private Target defaultTarget() {
        // For simple true/false toggles, a static target is fine.
        // Add attributes here if you introduce audience targeting later.
        return Target.builder()
                .identifier("backend-monolith")
                .name("Backend Monolith")
                .build();
    }

    /** Cached read of a boolean flag. */
    @Cacheable(cacheNames = "flags", key = "#flagId")
    public boolean isEnabled(String flagId) {
        // If the flag cannot be evaluated, defaultValue=false
        return cfClient.boolVariation(flagId, defaultTarget(), false);
    }

    /** Ad-hoc refresh: evict app cache and re-evaluate immediately. */
    @CacheEvict(cacheNames = "flags", key = "#flagId")
    public boolean refreshNow(String flagId) {
        return cfClient.boolVariation(flagId, defaultTarget(), false);
    }
}

