package com.example.ffstarter.demo;

import com.example.ffstarter.FeatureFlagService;
import org.springframework.stereotype.Service;

/**
 * Demo service showing how to use Feature Flags in your business logic
 */
@Service
public class DemoService {

    private final FeatureFlagService featureFlagService;

    public DemoService(FeatureFlagService featureFlagService) {
        this.featureFlagService = featureFlagService;
    }

    /**
     * Example method that uses a feature flag to control behavior
     * 
     * @param flagId The feature flag identifier
     * @return Response message based on flag value
     */
    public String checkFeatureFlag(String flagId) {
        // Check if the feature flag is enabled
        boolean isEnabled = featureFlagService.isEnabled(flagId);
        
        if (isEnabled) {
            return "wow flag is true";
        } else {
            return "flag is false";
        }
    }

    /**
     * Example: Using feature flag to control new feature rollout
     */
    public String processWithFeatureFlag(String flagId) {
        boolean isFeatureEnabled = featureFlagService.isEnabled(flagId);
        
        if (isFeatureEnabled) {
            // New feature logic
            return "wow flag is true - New feature is enabled!";
        } else {
            // Old/default behavior
            return "flag is false - Using default behavior";
        }
    }
}

