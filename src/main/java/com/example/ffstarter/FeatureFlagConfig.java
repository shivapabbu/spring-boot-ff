package com.example.ffstarter;

import io.harness.cf.client.api.CfClient;
import io.harness.cf.client.api.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Caffeine;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Configuration
public class FeatureFlagConfig {

    private static final Logger log = LoggerFactory.getLogger(FeatureFlagConfig.class);

    @Value("${featureflags.serverSdkKey}")
    private String serverSdkKey;

    @Value("${featureflags.streamEnabled:true}")
    private boolean streamEnabled;

    @Value("${featureflags.pollIntervalSeconds:60}")
    private int pollIntervalSeconds;

    @Value("${featureflags.analyticsEnabled:true}")
    private boolean analyticsEnabled;

    @Value("${featureflags.configUrl:https://config.ff.harness.io/api/1.0}")
    private String configUrl;

    @Value("${featureflags.eventUrl:https://events.ff.harness.io/api/1.0}")
    private String eventUrl;

    private CfClient client;

    @Bean
    public CfClient cfClient() throws Exception {
        // Check if using dummy key
        boolean isDummyKey = serverSdkKey.equals("DUMMY_API_KEY_FOR_TESTING_ONLY");
        if (isDummyKey) {
            log.warn("=================================================================");
            log.warn("WARNING: Using DUMMY API Key for testing purposes!");
            log.warn("The application will start but will NOT connect to Harness.");
            log.warn("Feature flag evaluations will fail or return default values.");
            log.warn("Please set a real API key via FF_SERVER_SDK_KEY env variable.");
            log.warn("=================================================================");
        }

        Config baseConfig = Config.builder()
                .configUrl(configUrl)
                .eventUrl(eventUrl)
                .streamEnabled(streamEnabled)          // SSE real-time updates
                .pollIntervalInSeconds(pollIntervalSeconds) // fallback polling
                .analyticsEnabled(analyticsEnabled)    // impressions & metrics
                .build();

        this.client = new CfClient(serverSdkKey, baseConfig);
        
        // For dummy key, don't wait for initialization (it will fail)
        if (!isDummyKey) {
            this.client.waitForInitialization(); // block until local cache is ready
            log.info("Harness Feature Flags SDK initialized successfully");
        } else {
            log.warn("Skipping SDK initialization wait (using dummy key)");
        }
        
        return this.client;
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager mgr = new CaffeineCacheManager("flags");
        mgr.setCaffeine(Caffeine.newBuilder()
                .maximumSize(10_000)
                // Shorter TTL (10s) to allow faster propagation of flag changes
                // SDK's internal cache updates in real-time via SSE, this is an optimization layer
                .expireAfterWrite(10, TimeUnit.SECONDS));
        return mgr;
    }

    @PreDestroy
    public void shutdown() {
        if (client != null) {
            try {
                log.info("Shutting down Harness Feature Flags SDK...");
                client.close();
                log.info("Harness Feature Flags SDK closed successfully");
            } catch (Exception e) {
                log.error("Error closing Harness Feature Flags SDK", e);
            }
        }
    }
}

