#!/bin/bash

BASE_URL="http://localhost:8080"
FLAG_ID="${1:-darkmode}"  # Use provided flag ID or default to 'darkmode'

echo "=== Feature Flags Testing ==="
echo "Testing flag: $FLAG_ID"
echo ""

echo "1. Health Check:"
curl -s $BASE_URL/actuator/health | python3 -m json.tool 2>/dev/null || curl -s $BASE_URL/actuator/health
echo ""
echo ""

echo "2. Feature Flags Health:"
curl -s $BASE_URL/actuator/health/featureflags | python3 -m json.tool 2>/dev/null || curl -s $BASE_URL/actuator/health/featureflags
echo ""
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
echo ""
echo "Usage: ./test-flags.sh [flag-id]"
echo "Example: ./test-flags.sh darkmode"
