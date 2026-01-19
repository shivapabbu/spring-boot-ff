# Testing Guide for Spring Boot Feature Flags Application

This guide explains how to test the Harness Feature Flags integration.

## Prerequisites

1. **Start the Application**
   ```bash
   mvn spring-boot:run
   ```

2. **Verify Application Started**
   - Look for: `Harness Feature Flags SDK initialized successfully`
   - Look for: `SSE stream connected ok` (for streaming mode)
   - Application should be running on `http://localhost:8080`

## 1. Health Check Tests

### Check Application Health
```bash
curl http://localhost:8080/actuator/health
```

**Expected Response:**
```json
{
  "status": "UP",
  "components": {
    "featureFlags": {
      "status": "UP",
      "details": {
        "streamEnabled": true,
        "status": "initialized"
      }
    }
  }
}
```

### Check Feature Flags Health Specifically
```bash
curl http://localhost:8080/actuator/health/featureflags
```

## 2. Feature Flag Evaluation Tests

### Test 1: Read a Feature Flag (GET)
```bash
# Replace 'your-flag-id' with an actual flag identifier from your Harness project
curl http://localhost:8080/api/flags/your-flag-id
```

**Expected Response:**
- `true` or `false` (boolean value)

**Example:**
```bash
curl http://localhost:8080/api/flags/darkmode
# Returns: true or false
```

### Test 2: Read Multiple Flags
```bash
# Test different flags
curl http://localhost:8080/api/flags/flag1
curl http://localhost:8080/api/flags/flag2
curl http://localhost:8080/api/flags/flag3
```

## 3. Cache Refresh Tests

### Test 3: Force Cache Refresh (POST)
```bash
# This evicts the application cache and re-evaluates immediately
curl -X POST http://localhost:8080/api/admin/flags/your-flag-id/refresh
```

**Expected Response:**
- `true` or `false` (latest value from SDK)

**Example:**
```bash
curl -X POST http://localhost:8080/api/admin/flags/darkmode/refresh
```

## 4. Real-Time Flag Update Testing

### Test 4: Verify Streaming Updates

1. **Start the application** and note the current flag value:
   ```bash
   curl http://localhost:8080/api/flags/your-flag-id
   # Note the value (e.g., false)
   ```

2. **Change the flag in Harness UI:**
   - Go to: https://app.harness.io/cf/admin/features
   - Find your flag
   - Toggle it ON/OFF
   - Save the changes

3. **Wait a few seconds** (SSE updates are near real-time, usually < 1 second)

4. **Check the flag again** (without cache refresh):
   ```bash
   curl http://localhost:8080/api/flags/your-flag-id
   # Should show new value within 10 seconds (app cache TTL)
   ```

5. **Force immediate refresh** to bypass app cache:
   ```bash
   curl -X POST http://localhost:8080/api/admin/flags/your-flag-id/refresh
   # Should show new value immediately
   ```

## 5. Testing with Different Scenarios

### Test 5: Non-Existent Flag
```bash
curl http://localhost:8080/api/flags/non-existent-flag
# Expected: false (default value)
```

### Test 6: Multiple Rapid Requests (Cache Test)
```bash
# Make multiple requests quickly - should be fast due to caching
for i in {1..10}; do
  curl http://localhost:8080/api/flags/your-flag-id
  echo ""
done
```

## 6. Monitoring and Logs

### Check Application Logs
Watch the application logs for:
- `SDKCODE(stream:5000): SSE stream connected ok` - Streaming active
- `SDKCODE(poll:4000): Polling started` - Polling fallback (if streaming fails)
- `SDKCODE(eval:6000): Evaluation was successful` - Flag evaluation

### Check SDK Status
```bash
# View detailed health information
curl http://localhost:8080/actuator/health | jq '.components.featureFlags'
```

## 7. Integration Testing Script

Create a test script `test-flags.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"
FLAG_ID="your-flag-id"  # Replace with your actual flag ID

echo "=== Feature Flags Testing ==="
echo ""

echo "1. Health Check:"
curl -s $BASE_URL/actuator/health | jq '.status'
echo ""

echo "2. Feature Flags Health:"
curl -s $BASE_URL/actuator/health/featureflags | jq '.'
echo ""

echo "3. Read Flag (Initial):"
INITIAL_VALUE=$(curl -s $BASE_URL/api/flags/$FLAG_ID)
echo "Value: $INITIAL_VALUE"
echo ""

echo "4. Force Refresh:"
REFRESH_VALUE=$(curl -s -X POST $BASE_URL/api/admin/flags/$FLAG_ID/refresh)
echo "Value: $REFRESH_VALUE"
echo ""

echo "5. Read Again (Cached):"
CACHED_VALUE=$(curl -s $BASE_URL/api/flags/$FLAG_ID)
echo "Value: $CACHED_VALUE"
echo ""

echo "=== Test Complete ==="
```

Make it executable and run:
```bash
chmod +x test-flags.sh
./test-flags.sh
```

## 8. Testing Streaming vs Polling

### Test Streaming Mode (Default)
1. Check logs for: `SSE stream connected ok`
2. Change flag in Harness UI
3. Flag should update within seconds

### Test Polling Mode
1. **Disable streaming** in `application.yml`:
   ```yaml
   featureflags:
     streamEnabled: false
   ```

2. **Restart application**

3. Check logs for: `Polling started, intervalMs: 60000`

4. Change flag in Harness UI
5. Flag will update within 60 seconds (polling interval)

## 9. Performance Testing

### Test Response Times
```bash
# Measure response time
time curl -s http://localhost:8080/api/flags/your-flag-id

# Multiple requests to test cache performance
for i in {1..100}; do
  curl -s -o /dev/null -w "%{time_total}\n" http://localhost:8080/api/flags/your-flag-id
done | awk '{sum+=$1; count++} END {print "Average:", sum/count, "seconds"}'
```

## 10. Troubleshooting

### If flags return default values:
1. Check API key is correct in `application.yml`
2. Verify flag identifier matches exactly (case-sensitive)
3. Check Harness UI to ensure flag exists and is enabled
4. Review application logs for SDK errors

### If streaming doesn't work:
1. Check network connectivity to `config.ff.harness.io`
2. Verify firewall allows SSE connections
3. Check logs for connection errors
4. SDK will automatically fall back to polling

### If cache doesn't refresh:
1. Use the `/refresh` endpoint to force update
2. Wait for app cache TTL (10 seconds)
3. Check SDK logs for update events

## 11. Using Postman/Insomnia

### Collection Setup

**GET Flag:**
- Method: `GET`
- URL: `http://localhost:8080/api/flags/{flagId}`
- Replace `{flagId}` with actual flag identifier

**POST Refresh:**
- Method: `POST`
- URL: `http://localhost:8080/api/admin/flags/{flagId}/refresh`

**GET Health:**
- Method: `GET`
- URL: `http://localhost:8080/actuator/health`

## 12. Example Test Scenarios

### Scenario 1: New Feature Rollout
1. Create flag `new-checkout-flow` in Harness (OFF)
2. Test: `curl http://localhost:8080/api/flags/new-checkout-flow` → `false`
3. Enable flag in Harness UI
4. Wait 10 seconds
5. Test again → `true`

### Scenario 2: A/B Testing
1. Create flag `enable-dark-mode` with percentage rollout
2. Make multiple requests with different targets
3. Verify different users get different values based on targeting rules

### Scenario 3: Emergency Kill Switch
1. Create flag `enable-payment-processing`
2. If issues occur, toggle OFF in Harness UI
3. Verify application respects the change within seconds

## Quick Reference

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/flags/{flagId}` | GET | Read flag value (cached) |
| `/api/admin/flags/{flagId}/refresh` | POST | Force cache refresh |
| `/actuator/health` | GET | Application health |
| `/actuator/health/featureflags` | GET | Feature flags health |

## Next Steps

1. Create flags in your Harness project
2. Test with real flag identifiers
3. Monitor logs during flag changes
4. Verify streaming updates work in real-time
5. Test cache refresh functionality

