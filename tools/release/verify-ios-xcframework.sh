#!/usr/bin/env bash

set -euo pipefail

root_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
xcframework="${1:-$root_dir/mermaid-compose/build/cocoapods/publish/release/CMPMermaid.xcframework}"

if [[ ! -d "$xcframework" ]]; then
  echo "Missing XCFramework: $xcframework" >&2
  exit 1
fi

device_framework="$xcframework/ios-arm64/CMPMermaid.framework"
simulator_framework="$xcframework/ios-arm64_x86_64-simulator/CMPMermaid.framework"
for framework in "$device_framework" "$simulator_framework"; do
  binary="$framework/CMPMermaid"
  header="$framework/Headers/CMPMermaid.h"
  file "$binary" | grep -Fq "current ar archive"
  grep -Fq 'swift_name("CMPMermaidViewControllerFactory")' "$header"
  grep -Fq 'swift_name("makeViewController(source:contentDescription:)")' "$header"
done

lipo -archs "$device_framework/CMPMermaid" | grep -Eq '(^| )arm64( |$)'
simulator_archs="$(lipo -archs "$simulator_framework/CMPMermaid")"
grep -Eq '(^| )arm64( |$)' <<<"$simulator_archs"
grep -Eq '(^| )x86_64( |$)' <<<"$simulator_archs"

for font in \
  arimo_bold.ttf \
  arimo_bolditalic.ttf \
  arimo_italic.ttf \
  arimo_regular.ttf \
  droid_sans_fallback.ttf \
  droid_sans_mono.ttf \
  noto_sans_symbols2_regular.ttf
do
  if [[ -z "$(find "$xcframework" -type f -name "$font" -print -quit)" ]]; then
    echo "Missing XCFramework font resource: $font" >&2
    exit 1
  fi
done

xcrun swiftc \
  -typecheck \
  -target arm64-apple-ios14.0-simulator \
  -sdk "$(xcrun --sdk iphonesimulator --show-sdk-path)" \
  -F "$(dirname "$simulator_framework")" \
  "$root_dir/tools/release/ios-smoke/CMPMermaidSmoke.swift"

echo "Verified CMPMermaid XCFramework"
