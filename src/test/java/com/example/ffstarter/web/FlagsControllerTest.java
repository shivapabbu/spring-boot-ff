package com.example.ffstarter.web;

import com.example.ffstarter.FeatureFlagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FlagsController.class)
@TestPropertySource(properties = {
        "featureflags.serverSdkKey=DUMMY_KEY_FOR_TESTING"
})
class FlagsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeatureFlagService featureFlagService;

    @Test
    void getFlagReturnsTrue() throws Exception {
        when(featureFlagService.isEnabled("darkmode")).thenReturn(true);

        mockMvc.perform(get("/api/flags/darkmode"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void getFlagReturnsFalse() throws Exception {
        when(featureFlagService.isEnabled("darkmode")).thenReturn(false);

        mockMvc.perform(get("/api/flags/darkmode"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}

