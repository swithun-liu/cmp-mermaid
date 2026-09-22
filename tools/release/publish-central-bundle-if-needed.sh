#!/usr/bin/env bash

set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
bundle="${1:?usage: publish-central-bundle-if-needed.sh <bundle.zip> [deployment-name]}"
deployment_name="${2:-$(basename "$bundle" .zip)}"
state="$("$script_dir/central-bundle-state.sh" "$bundle")"

case "$state" in
  published)
    echo "Skipping Central Portal upload; $(basename "$bundle") is already published"
    ;;
  missing)
    "$script_dir/upload-central-bundle.sh" \
      "$bundle" \
      "$deployment_name"
    ;;
  partial)
    echo "Refusing to upload over a partially published immutable Maven version" >&2
    exit 1
    ;;
  *)
    echo "Unexpected Maven Central publication state: $state" >&2
    exit 1
    ;;
esac
