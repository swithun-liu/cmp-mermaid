#!/usr/bin/env bash

set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
podspec="${1:?usage: publish-cocoapods-if-needed.sh <podspec> <version>}"
version="${2:?usage: publish-cocoapods-if-needed.sh <podspec> <version>}"
pod_name="CMPMermaid"
push_attempts="${COCOAPODS_PUSH_ATTEMPTS:-3}"
retry_delay_seconds="${COCOAPODS_RETRY_DELAY_SECONDS:-30}"

if [[ ! "$push_attempts" =~ ^[1-3]$ ]]; then
  echo "COCOAPODS_PUSH_ATTEMPTS must be an integer from 1 to 3" >&2
  exit 1
fi
if [[ ! "$retry_delay_seconds" =~ ^[0-9]+$ ]]; then
  echo "COCOAPODS_RETRY_DELAY_SECONDS must be a non-negative integer" >&2
  exit 1
fi

is_published() {
  "$script_dir/cocoapods-version-published.sh" \
    "$pod_name" \
    "$version"
}

is_transient_push_failure() {
  local log_file="$1"

  LC_ALL=C grep -Eiq \
    '(HTTP[^0-9]*5[0-9]{2}|(^|[^0-9])(500|502|503|504)([^0-9]|$)|internal server error|bad gateway|service unavailable|gateway time-?out|timed out|timeout|execution expired|temporary failure|temporarily unavailable|connection (reset|refused|closed)|failed to open TCP connection|could not resolve host|network is unreachable|SSL_connect|unexpected EOF|EOFError)' \
    "$log_file"
}

if is_published; then
  echo "Skipping CocoaPods trunk push; $pod_name $version is already published"
  exit 0
fi

: "${COCOAPODS_TRUNK_TOKEN:?COCOAPODS_TRUNK_TOKEN is required for an unpublished CocoaPods version}"

temporary_dir="$(mktemp -d)"
trap 'rm -rf "$temporary_dir"' EXIT

for ((attempt = 1; attempt <= push_attempts; attempt++)); do
  if (( attempt > 1 )) && is_published; then
    echo "Skipping CocoaPods trunk push; $pod_name $version became visible before attempt $attempt"
    exit 0
  fi

  attempt_log="$temporary_dir/pod-trunk-push-$attempt.log"
  echo "Publishing $pod_name $version to CocoaPods trunk (attempt $attempt/$push_attempts)"
  if pod trunk push "$podspec" --allow-warnings 2>&1 | tee "$attempt_log"; then
    if ! is_published; then
      echo "CocoaPods push succeeded; trunk visibility will be checked by the final release gate"
    fi
    exit 0
  else
    push_status="${PIPESTATUS[0]}"
  fi

  echo "CocoaPods push returned status $push_status; checking whether trunk committed the version" >&2
  if is_published; then
    echo "$pod_name $version is published despite the client-side error"
    exit 0
  fi

  if ! is_transient_push_failure "$attempt_log"; then
    echo "CocoaPods push failed permanently; refusing to retry" >&2
    exit "$push_status"
  fi
  if (( attempt == push_attempts )); then
    echo "CocoaPods trunk did not publish $pod_name $version after $push_attempts transient failures" >&2
    exit "$push_status"
  fi

  delay=$((retry_delay_seconds * (1 << (attempt - 1))))
  echo "Transient CocoaPods failure; retrying in $delay second(s)" >&2
  sleep "$delay"
done

echo "CocoaPods trunk did not publish $pod_name $version" >&2
exit 1
