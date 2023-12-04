#!/bin/bash

# Set your Slack webhook URL
SLACK_WEBHOOK_URL="https://hooks.slack.com/triggers/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"

# Set your error text
error_text="Something went wrong!"

# Construct the JSON payload
json_payload="{\"message\": \"error: ${error_text}\"}"

# Send the request using curl
curl -X POST -H "Content-type: application/json" --data "$json_payload" "$SLACK_WEBHOOK_URL"
