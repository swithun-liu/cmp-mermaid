#!/usr/bin/env bash

set -euo pipefail

root_dir="$(
  cd "${CMP_MERMAID_ROOT_DIR:-$(dirname "${BASH_SOURCE[0]}")/../..}"
  pwd
)"
version="${2:-$(sed -n 's/^VERSION_NAME=//p' "$root_dir/gradle.properties")}"
release_dir="${1:-$root_dir/build/release/$version}"

if [[ ! -d "$release_dir" ]]; then
  echo "Missing release directory: $release_dir" >&2
  exit 1
fi

temporary_dir="$(mktemp -d "$root_dir/build/cmp-mermaid-privacy.XXXXXX")"
trap 'rm -rf "$temporary_dir"' EXIT

current_layer="$release_dir"
scan_roots=("$release_dir")
for depth in {1..8}; do
  next_layer="$temporary_dir/layer-$depth"
  archive_count=0
  mkdir -p "$next_layer"

  while IFS= read -r -d '' archive; do
    archive_count=$((archive_count + 1))
    destination="$next_layer/$archive_count"
    mkdir -p "$destination"
    if ! unzip -q "$archive" -d "$destination"; then
      echo "Failed to inspect nested release archive: $archive" >&2
      exit 1
    fi
  done < <(
    find "$current_layer" -type f \
      \( -name '*.aar' -o -name '*.jar' -o -name '*.klib' -o -name '*.zip' \) \
      -print0
  )

  if (( archive_count == 0 )); then
    break
  fi
  scan_roots+=("$next_layer")
  current_layer="$next_layer"

  if (( depth == 8 )); then
    echo "Release archives exceed the supported inspection depth" >&2
    exit 1
  fi
done

credential_pattern='(AKIA[0-9A-Z]{16}|-----BEGIN (RSA|EC|OPENSSH|PRIVATE) KEY-----|gh[pousr]_[A-Za-z0-9_]{20,}|github_pat_[A-Za-z0-9_]{20,}|sk-[A-Za-z0-9]{20,})'
private_pattern="$credential_pattern"
private_patterns_file="${CMP_MERMAID_PRIVATE_PATTERNS_FILE:-}"
if [[ -n "$private_patterns_file" && ! -r "$private_patterns_file" ]]; then
  echo "Private patterns file is not readable: $private_patterns_file" >&2
  exit 1
fi
hits_file="$temporary_dir/hits.txt"
: > "$hits_file"

set +e
LC_ALL=C grep -a -r -l -E "$private_pattern" \
  "${scan_roots[@]}" \
  >> "$hits_file"
content_status=$?
private_content_status=1
if [[ -n "$private_patterns_file" ]]; then
  LC_ALL=C grep -a -r -l -i -E -f "$private_patterns_file" \
    "${scan_roots[@]}" \
    >> "$hits_file"
  private_content_status=$?
fi
LC_ALL=C grep -a -r -l -F "$root_dir/" \
  "${scan_roots[@]}" \
  >> "$hits_file"
root_path_status=$?
home_path_status=1
if [[ "${CI:-false}" != "true" && -n "${HOME:-}" ]]; then
  LC_ALL=C grep -a -r -l -F "$HOME/" \
    "${scan_roots[@]}" \
    >> "$hits_file"
  home_path_status=$?
fi
set -e

for scan_status in \
  "$content_status" \
  "$private_content_status" \
  "$root_path_status" \
  "$home_path_status"
do
  if (( scan_status > 1 )); then
    echo "Release privacy scan failed with status $scan_status" >&2
    exit "$scan_status"
  fi
done

if [[ -s "$hits_file" ]]; then
  echo "Private material detected in release artifacts:" >&2
  sort -u "$hits_file" | sed -n '1,100p' >&2
  exit 1
fi

echo "Verified release artifact privacy for $version"
