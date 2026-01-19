package com.example.ffstarter.web;

import com.example.ffstarter.FeatureFlagService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/flags")
public class FlagsAdminController {

    private final FeatureFlagService ff;

    public FlagsAdminController(FeatureFlagService ff) {
        this.ff = ff;
    }

    /** POST /api/admin/flags/{flagId}/refresh */
    @PostMapping("/{flagId}/refresh")
    public boolean refresh(@PathVariable("flagId") String flagId) {
        return ff.refreshNow(flagId);
    }
}

