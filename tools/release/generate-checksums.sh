#!/usr/bin/env bash

set -euo pipefail

release_dir="${1:?usage: generate-checksums.sh <release-directory>}"
checksum_file="$release_dir/SHA256SUMS"

: > "$checksum_file"
for file in "$release_dir"/*; do
  if [[ -f "$file" && "$(basename "$file")" != "SHA256SUMS" ]]; then
    (
      cd "$release_dir"
      shasum -a 256 "$(basename "$file")"
    )
  fi
done | sort -k 2 > "$checksum_file"
