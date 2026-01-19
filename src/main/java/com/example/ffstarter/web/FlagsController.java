package com.example.ffstarter.web;

import com.example.ffstarter.FeatureFlagService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flags")
public class FlagsController {

    private final FeatureFlagService ff;

    public FlagsController(FeatureFlagService ff) {
        this.ff = ff;
    }

    @GetMapping("/{flagId}")
    public boolean get(@PathVariable("flagId") String flagId) {
        return ff.isEnabled(flagId);
    }
}

