#!/usr/bin/env bash

set -euo pipefail

root_dir="$(
  cd "${CMP_MERMAID_ROOT_DIR:-$(dirname "${BASH_SOURCE[0]}")/../..}"
  pwd
)"
version="${1:-$(sed -n 's/^VERSION_NAME=//p' "$root_dir/gradle.properties")}"
maven_repo="$root_dir/build/maven-repository"
pod_publish_dir="$root_dir/mermaid-compose/build/cocoapods/publish/release"
release_dir="$root_dir/build/release/$version"
group_path="io/github/swithun-liu"

required_maven_modules=(
  "mermaid-core"
  "mermaid-compose"
  "mermaid-core-android-kotlin17"
  "mermaid-compose-android-kotlin17"
)
legacy_maven_modules=(
  "mermaid-core-android-kotlin17"
  "mermaid-compose-android-kotlin17"
)

for module in "${required_maven_modules[@]}"; do
  pom="$maven_repo/$group_path/$module/$version/$module-$version.pom"
  if [[ ! -f "$pom" ]]; then
    echo "Missing Maven publication: $pom" >&2
    exit 1
  fi
done

xcframework="$pod_publish_dir/CMPMermaid.xcframework"
podspec="$pod_publish_dir/CMPMermaid.podspec"
if [[ ! -d "$xcframework" || ! -f "$podspec" ]]; then
  echo "Missing CocoaPods publication under $pod_publish_dir" >&2
  exit 1
fi
compose_resources="$xcframework/ios-arm64/CMPMermaid.framework/composeResources"
if [[ ! -d "$compose_resources" ]]; then
  echo "Missing Compose resources under $compose_resources" >&2
  exit 1
fi

rm -rf "$release_dir"
mkdir -p "$release_dir"

temporary_dir="$(mktemp -d "$root_dir/build/cmp-mermaid-release.XXXXXX")"
trap 'rm -rf "$temporary_dir"' EXIT

publication_dirs=()
while IFS= read -r publication_dir; do
  publication_dirs+=("$publication_dir")
done < <(
  find "$maven_repo/$group_path" \
    -mindepth 2 \
    -maxdepth 2 \
    -type d \
    -name "$version" \
    -print |
    sort
)
if (( ${#publication_dirs[@]} == 0 )); then
  echo "No Maven publications found for $version" >&2
  exit 1
fi

is_legacy_module() {
  local candidate="$1"
  local legacy_module
  for legacy_module in "${legacy_maven_modules[@]}"; do
    if [[ "$candidate" == "$legacy_module" ]]; then
      return 0
    fi
  done
  return 1
}

copy_publication() {
  local source_version_dir="$1"
  local target_root="$2"
  local module
  local target_version_dir

  module="$(basename "$(dirname "$source_version_dir")")"
  target_version_dir="$target_root/$group_path/$module/$version"
  mkdir -p "$target_version_dir"
  while IFS= read -r -d '' artifact; do
    cp "$artifact" "$target_version_dir/"
  done < <(find "$source_version_dir" -maxdepth 1 -type f -print0)
}

prepare_central_bundle() {
  local bundle_root="$1"
  local artifact

  find "$bundle_root" -type f \
    \( \
      -name '*.md5' \
      -o -name '*.sha1' \
      -o -name '*.sha256' \
      -o -name '*.sha512' \
    \) \
    -delete
  while IFS= read -r -d '' artifact; do
    openssl dgst -md5 -r "$artifact" | awk '{ print $1 }' \
      > "$artifact.md5"
    openssl dgst -sha1 -r "$artifact" | awk '{ print $1 }' \
      > "$artifact.sha1"
  done < <(
    find "$bundle_root" -type f \
      ! -name '*.md5' \
      ! -name '*.sha1' \
      -print0
  )
}

pod_archive_root="$temporary_dir/CMPMermaid-$version"
mkdir -p "$pod_archive_root"
cp -R "$xcframework" "$pod_archive_root/CMPMermaid.xcframework"
mkdir -p "$pod_archive_root/compose-resources"
cp -R "$compose_resources" "$pod_archive_root/compose-resources/composeResources"
cp "$root_dir/LICENSE" "$pod_archive_root/LICENSE"
cp "$root_dir/THIRD_PARTY_NOTICES.md" "$pod_archive_root/THIRD_PARTY_NOTICES.md"

(
  cd "$pod_archive_root"
  COPYFILE_DISABLE=1 \
    zip -X -q -r "$release_dir/CMPMermaid-$version.zip" .
)

maven_archive_root="$temporary_dir/maven-repository"
modern_bundle_root="$temporary_dir/modern-central"
legacy_bundle_root="$temporary_dir/legacy-central"
for source_version_dir in "${publication_dirs[@]}"; do
  module="$(basename "$(dirname "$source_version_dir")")"
  copy_publication "$source_version_dir" "$maven_archive_root"
  if is_legacy_module "$module"; then
    copy_publication "$source_version_dir" "$legacy_bundle_root"
  else
    copy_publication "$source_version_dir" "$modern_bundle_root"
  fi
done

(
  cd "$maven_archive_root"
  COPYFILE_DISABLE=1 \
    zip -X -q -r \
      "$release_dir/cmp-mermaid-maven-$version.zip" \
      "$group_path"
)

prepare_central_bundle "$modern_bundle_root"
prepare_central_bundle "$legacy_bundle_root"

(
  cd "$modern_bundle_root"
  COPYFILE_DISABLE=1 \
    zip -X -q -r \
      "$release_dir/cmp-mermaid-maven-central-$version.zip" \
      "$group_path"
)

(
  cd "$legacy_bundle_root"
  COPYFILE_DISABLE=1 \
    zip -X -q -r \
      "$release_dir/cmp-mermaid-android-kotlin17-central-$version.zip" \
      "$group_path"
)

sed \
  "s#spec\\.resources.*build/compose/cocoapods/compose-resources.*#    spec.resources                = ['compose-resources']#" \
  "$podspec" \
  > "$release_dir/CMPMermaid.podspec"
cp "$root_dir/LICENSE" "$release_dir/LICENSE"
cp "$root_dir/THIRD_PARTY_NOTICES.md" "$release_dir/THIRD_PARTY_NOTICES.md"

printf '%s\n' "$release_dir"
