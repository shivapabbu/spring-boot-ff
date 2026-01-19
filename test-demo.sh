#!/bin/bash

BASE_URL="http://localhost:8080"
FLAG_ID="${1:-darkmode}"  # Use provided flag ID or default to 'darkmode'

echo "=== Demo Application Testing ==="
echo "Testing flag: $FLAG_ID"
echo ""

echo "1. Check Flag (Query Parameter):"
RESPONSE=$(curl -s "$BASE_URL/api/demo/check?flagId=$FLAG_ID")
echo "Response: $RESPONSE"
echo ""

echo "2. Check Flag (Path Variable):"
RESPONSE=$(curl -s "$BASE_URL/api/demo/check/$FLAG_ID")
echo "Response: $RESPONSE"
echo ""

echo "3. Process with Flag:"
RESPONSE=$(curl -s "$BASE_URL/api/demo/process?flagId=$FLAG_ID")
echo "Response: $RESPONSE"
echo ""

echo "=== Expected Responses ==="
echo "If flag is TRUE:  'wow flag is true'"
echo "If flag is FALSE: 'flag is false'"
echo ""
echo "Usage: ./test-demo.sh [flag-id]"
echo "Example: ./test-demo.sh darkmode"

