package com.example.ffstarter;

import io.harness.cf.client.api.CfClient;
import io.harness.cf.client.dto.Target;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class FeatureFlagServiceTest {

    @Test
    void isEnabledReturnsSdkValue() {
        CfClient mock = Mockito.mock(CfClient.class);
        Mockito.when(mock.boolVariation(Mockito.eq("darkmode"), Mockito.any(Target.class), Mockito.eq(false)))
                .thenReturn(true);

        FeatureFlagService svc = new FeatureFlagService(mock);
        assertTrue(svc.isEnabled("darkmode"));
    }

    @Test
    void refreshNowEvictsAndReevaluates() {
        CfClient mock = Mockito.mock(CfClient.class);
        Mockito.when(mock.boolVariation(Mockito.eq("darkmode"), Mockito.any(Target.class), Mockito.eq(false)))
                .thenReturn(false);

        FeatureFlagService svc = new FeatureFlagService(mock);
        assertFalse(svc.refreshNow("darkmode"));
    }
}

