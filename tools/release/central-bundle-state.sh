#!/usr/bin/env bash

set -euo pipefail

bundle="${1:?usage: central-bundle-state.sh <bundle.zip>}"
base_url="${MAVEN_CENTRAL_BASE_URL:-https://repo1.maven.org/maven2}"
attempts="${MAVEN_STATUS_ATTEMPTS:-1}"
poll_interval="${MAVEN_STATUS_INTERVAL_SECONDS:-15}"

if [[ ! -f "$bundle" ]]; then
  echo "Missing Central Portal bundle: $bundle" >&2
  exit 1
fi
unzip -tq "$bundle" >/dev/null

pom_entries=()
while IFS= read -r entry; do
  pom_entries+=("$entry")
done < <(unzip -Z1 "$bundle" | grep -E '\.pom$' | sort)
if (( ${#pom_entries[@]} == 0 )); then
  echo "Central Portal bundle contains no POM files: $bundle" >&2
  exit 1
fi

for ((attempt = 1; attempt <= attempts; attempt++)); do
  published=0
  missing=0
  for entry in "${pom_entries[@]}"; do
    status="$(
      curl \
        --location \
        --silent \
        --show-error \
        --output /dev/null \
        --write-out '%{http_code}' \
        --retry 3 \
        --retry-all-errors \
        "$base_url/$entry"
    )"
    case "$status" in
      200)
        published=$((published + 1))
        ;;
      404)
        missing=$((missing + 1))
        ;;
      *)
        echo "Unexpected Maven Central status $status for $entry" >&2
        exit 1
        ;;
    esac
  done

  if (( published == ${#pom_entries[@]} )); then
    echo "Maven Central contains all $published publication(s) from $(basename "$bundle")" >&2
    echo "published"
    exit 0
  fi
  if (( missing == ${#pom_entries[@]} )); then
    if (( attempt < attempts )); then
      echo "Maven Central has not exposed $(basename "$bundle") yet; waiting" >&2
      sleep "$poll_interval"
      continue
    fi
    echo "Maven Central contains none of the ${#pom_entries[@]} publication(s) from $(basename "$bundle")" >&2
    echo "missing"
    exit 0
  fi

  echo "Maven Central is partially published for $(basename "$bundle"): $published present, $missing missing" >&2
  echo "partial"
  exit 0
done
