package com.example.ffstarter.demo;

import org.springframework.web.bind.annotation.*;

/**
 * Demo controller showing how to use Feature Flags in REST endpoints
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    /**
     * Check a feature flag and return response based on flag value
     * 
     * GET /api/demo/check?flagId=your-flag-id
     */
    @GetMapping("/check")
    public String checkFlag(@RequestParam("flagId") String flagId) {
        return demoService.checkFeatureFlag(flagId);
    }

    /**
     * Check a feature flag with path variable
     * 
     * GET /api/demo/check/{flagId}
     */
    @GetMapping("/check/{flagId}")
    public String checkFlagPath(@PathVariable("flagId") String flagId) {
        return demoService.checkFeatureFlag(flagId);
    }

    /**
     * Process with feature flag control
     * 
     * GET /api/demo/process?flagId=your-flag-id
     */
    @GetMapping("/process")
    public String processWithFlag(@RequestParam("flagId") String flagId) {
        return demoService.processWithFeatureFlag(flagId);
    }
}

