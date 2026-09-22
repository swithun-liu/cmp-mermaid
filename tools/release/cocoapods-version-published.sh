#!/usr/bin/env bash

set -euo pipefail

pod_name="${1:?usage: cocoapods-version-published.sh <pod-name> <version>}"
version="${2:?usage: cocoapods-version-published.sh <pod-name> <version>}"
trunk_url="${COCOAPODS_TRUNK_BASE_URL:-https://trunk.cocoapods.org/api/v1/pods}"

curl \
  --fail-with-body \
  --silent \
  --show-error \
  --retry 5 \
  --retry-all-errors \
  "$trunk_url/$pod_name" |
  jq --exit-status --arg version "$version" \
    'any(.versions[]?; .name == $version)' \
    >/dev/null
