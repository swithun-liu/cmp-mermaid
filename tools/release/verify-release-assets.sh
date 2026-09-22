#!/usr/bin/env bash

set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
root_dir="$(
  cd "${CMP_MERMAID_ROOT_DIR:-$script_dir/../..}"
  pwd
)"
version="${2:-$(sed -n 's/^VERSION_NAME=//p' "$root_dir/gradle.properties")}"
release_dir="${1:-$root_dir/build/release/$version}"
maven_repo="$root_dir/build/maven-repository"
podspec="$release_dir/CMPMermaid.podspec"
group_path="io/github/swithun-liu"

required_release_files=(
  "CMPMermaid-$version.zip"
  "CMPMermaid.podspec"
  "LICENSE"
  "THIRD_PARTY_NOTICES.md"
  "cmp-mermaid-$version.cdx.json"
  "cmp-mermaid-android-kotlin17-central-$version.zip"
  "cmp-mermaid-maven-central-$version.zip"
  "cmp-mermaid-maven-$version.zip"
  "SHA256SUMS"
)
for name in "${required_release_files[@]}"; do
  if [[ ! -f "$release_dir/$name" ]]; then
    echo "Missing release asset: $release_dir/$name" >&2
    exit 1
  fi
done

(
  cd "$release_dir"
  shasum -a 256 -c SHA256SUMS
)

grep -Eq '"bomFormat"[[:space:]]*:[[:space:]]*"CycloneDX"' \
  "$release_dir/cmp-mermaid-$version.cdx.json"
grep -Fq \
  "releases/download/v$version/CMPMermaid-$version.zip" \
  "$podspec"
grep -Fq "CMPMermaid.xcframework" "$podspec"
grep -Fq "spec.resources                = ['compose-resources']" "$podspec"
grep -Fq "spec.ios.deployment_target    = '14.0'" "$podspec"
if grep -Eq 'vnull|CMPMermaid-null|build/compose/cocoapods' "$podspec"; then
  echo "Podspec contains a build-local or unresolved release path" >&2
  exit 1
fi

if command -v pod >/dev/null; then
  pod ipc spec "$podspec" >/dev/null
fi

unzip -tq "$release_dir/CMPMermaid-$version.zip" >/dev/null
pod_archive_entries="$(unzip -Z1 "$release_dir/CMPMermaid-$version.zip")"
grep -Fq "CMPMermaid.xcframework" <<<"$pod_archive_entries"
grep -Fq "THIRD_PARTY_NOTICES.md" <<<"$pod_archive_entries"
for font in \
  arimo_bold.ttf \
  arimo_bolditalic.ttf \
  arimo_italic.ttf \
  arimo_regular.ttf \
  droid_sans_fallback.ttf \
  droid_sans_mono.ttf \
  noto_sans_symbols2_regular.ttf
do
  grep -Fq \
    "compose-resources/composeResources/com.swithun.cmpmermaid.compose.generated.resources/font/$font" \
    <<<"$pod_archive_entries"
done

temporary_dir="$(mktemp -d "$root_dir/build/cmp-mermaid-verify.XXXXXX")"
trap 'rm -rf "$temporary_dir"' EXIT
unzip -q "$release_dir/CMPMermaid-$version.zip" -d "$temporary_dir"
"$script_dir/verify-ios-xcframework.sh" \
  "$temporary_dir/CMPMermaid.xcframework"

unzip -tq "$release_dir/cmp-mermaid-maven-$version.zip" >/dev/null
unzip -tq \
  "$release_dir/cmp-mermaid-maven-central-$version.zip" \
  >/dev/null
unzip -tq \
  "$release_dir/cmp-mermaid-android-kotlin17-central-$version.zip" \
  >/dev/null

verify_central_bundle() {
  local bundle="$1"
  local label="$2"
  local extracted_dir="$temporary_dir/$label"
  local artifact
  local checksum_extension

  mkdir -p "$extracted_dir"
  unzip -q "$bundle" -d "$extracted_dir"
  if find "$extracted_dir" -type f -name 'maven-metadata*' | grep -q .; then
    echo "$label Central bundle must not contain Maven metadata" >&2
    exit 1
  fi
  while IFS= read -r -d '' artifact; do
    for checksum_extension in md5 sha1; do
      if [[ ! -s "$artifact.$checksum_extension" ]]; then
        echo "Missing $checksum_extension checksum for $artifact" >&2
        exit 1
      fi
    done
    if [[ \
      "${REQUIRE_SIGNATURES:-false}" == "true" \
      && "$artifact" != *.asc \
      && ! -s "$artifact.asc" \
    ]]; then
      echo "Missing signature for $artifact" >&2
      exit 1
    fi
  done < <(
    find "$extracted_dir" -type f \
      ! -name '*.md5' \
      ! -name '*.sha1' \
      -print0
  )
}

verify_central_bundle \
  "$release_dir/cmp-mermaid-maven-central-$version.zip" \
  "modern"
verify_central_bundle \
  "$release_dir/cmp-mermaid-android-kotlin17-central-$version.zip" \
  "legacy"

for module in \
  mermaid-core \
  mermaid-compose \
  mermaid-core-android-kotlin17 \
  mermaid-compose-android-kotlin17
do
  pom="$maven_repo/$group_path/$module/$version/$module-$version.pom"
  grep -Fq "<groupId>io.github.swithun-liu</groupId>" "$pom"
  grep -Fq "<artifactId>$module</artifactId>" "$pom"
  grep -Fq "<version>$version</version>" "$pom"
  grep -Fq "<name>MIT License</name>" "$pom"
done

legacy_aar="$maven_repo/$group_path/mermaid-compose-android-kotlin17/$version/mermaid-compose-android-kotlin17-$version.aar"
legacy_aar_entries="$(unzip -Z1 "$legacy_aar")"
for font in \
  arimo_bold.ttf \
  arimo_bolditalic.ttf \
  arimo_italic.ttf \
  arimo_regular.ttf \
  droid_sans_fallback.ttf \
  droid_sans_mono.ttf \
  noto_sans_symbols2_regular.ttf
do
  grep -Fq "res/font/$font" <<<"$legacy_aar_entries"
done

if [[ "${REQUIRE_SIGNATURES:-false}" == "true" ]]; then
  for bundle in \
    "$release_dir/cmp-mermaid-maven-central-$version.zip" \
    "$release_dir/cmp-mermaid-android-kotlin17-central-$version.zip"
  do
    signature_count="$(unzip -Z1 "$bundle" | grep -cE '\.asc$')"
    if (( signature_count < 6 )); then
      echo "Central bundle has too few signatures: $bundle ($signature_count)" >&2
      exit 1
    fi
  done
fi

CMP_MERMAID_ROOT_DIR="$root_dir" \
  "$script_dir/verify-release-privacy.sh" \
  "$release_dir" \
  "$version"

echo "Verified release assets for $version"
