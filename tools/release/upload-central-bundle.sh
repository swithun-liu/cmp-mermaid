#!/usr/bin/env bash

set -euo pipefail

bundle="${1:?usage: upload-central-bundle.sh <bundle.zip> [deployment-name]}"
deployment_name="${2:-$(basename "$bundle" .zip)}"
publishing_type="${CENTRAL_PUBLISHING_TYPE:-AUTOMATIC}"
poll_interval="${CENTRAL_POLL_INTERVAL_SECONDS:-15}"
poll_attempts="${CENTRAL_POLL_ATTEMPTS:-120}"
base_url="https://central.sonatype.com/api/v1/publisher"

if [[ ! -f "$bundle" ]]; then
  echo "Missing Central Portal bundle: $bundle" >&2
  exit 1
fi
unzip -tq "$bundle" >/dev/null

if [[ ! "$deployment_name" =~ ^[A-Za-z0-9._-]+$ ]]; then
  echo "Deployment name must contain only letters, digits, '.', '_', or '-'" >&2
  exit 1
fi
if [[ "$publishing_type" != "AUTOMATIC" && "$publishing_type" != "USER_MANAGED" ]]; then
  echo "Unsupported CENTRAL_PUBLISHING_TYPE: $publishing_type" >&2
  exit 1
fi

if [[ "${DRY_RUN:-false}" == "true" ]]; then
  echo "Would upload $bundle as $deployment_name ($publishing_type)"
  exit 0
fi

: "${CENTRAL_PORTAL_USERNAME:?CENTRAL_PORTAL_USERNAME is required}"
: "${CENTRAL_PORTAL_PASSWORD:?CENTRAL_PORTAL_PASSWORD is required}"
command -v jq >/dev/null

authorization="$(
  printf '%s:%s' "$CENTRAL_PORTAL_USERNAME" "$CENTRAL_PORTAL_PASSWORD" |
    base64 |
    tr -d '\r\n'
)"
deployment_id="$(
  curl \
    --fail-with-body \
    --silent \
    --show-error \
    --request POST \
    --header "Authorization: Bearer $authorization" \
    --form "bundle=@$bundle;type=application/octet-stream" \
    "$base_url/upload?name=$deployment_name&publishingType=$publishing_type"
)"

if [[ ! "$deployment_id" =~ ^[0-9a-fA-F-]{36}$ ]]; then
  echo "Unexpected Central Portal deployment ID: $deployment_id" >&2
  exit 1
fi
echo "Central Portal deployment: $deployment_id"

for ((attempt = 1; attempt <= poll_attempts; attempt++)); do
  status="$(
    curl \
      --fail-with-body \
      --silent \
      --show-error \
      --request POST \
      --header "Authorization: Bearer $authorization" \
      "$base_url/status?id=$deployment_id"
  )"
  state="$(jq -r '.deploymentState // empty' <<<"$status")"
  echo "Central Portal state: $state"

  case "$state" in
    PUBLISHED)
      exit 0
      ;;
    FAILED)
      jq . <<<"$status" >&2
      exit 1
      ;;
  esac

  sleep "$poll_interval"
done

echo "Timed out waiting for Central Portal deployment $deployment_id" >&2
exit 1
