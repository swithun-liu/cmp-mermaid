#!/usr/bin/env bash

set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
podspec="${1:?usage: publish-cocoapods-if-needed.sh <podspec> <version>}"
version="${2:?usage: publish-cocoapods-if-needed.sh <podspec> <version>}"
pod_name="CMPMermaid"

if "$script_dir/cocoapods-version-published.sh" \
  "$pod_name" \
  "$version"
then
  echo "Skipping CocoaPods trunk push; $pod_name $version is already published"
  exit 0
fi

: "${COCOAPODS_TRUNK_TOKEN:?COCOAPODS_TRUNK_TOKEN is required for an unpublished CocoaPods version}"

if pod trunk push "$podspec" --allow-warnings; then
  exit 0
fi

echo "CocoaPods push returned an error; checking whether trunk committed the version" >&2
if "$script_dir/cocoapods-version-published.sh" \
  "$pod_name" \
  "$version"
then
  echo "$pod_name $version is published despite the client-side error"
  exit 0
fi

echo "CocoaPods trunk did not publish $pod_name $version" >&2
exit 1
