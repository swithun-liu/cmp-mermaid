#!/usr/bin/env bash

set -euo pipefail

serial="${ANDROID_SERIAL:?Set ANDROID_SERIAL to an emulator serial such as emulator-5554}"
if [[ "$serial" != emulator-* ]]; then
  echo "Refusing to run against non-emulator device: $serial" >&2
  exit 2
fi

apk="${APK_PATH:-sample/androidApp/build/outputs/apk/debug/androidApp-debug.apk}"
output_dir="${OUTPUT_DIR:-captures/local/load-test-android}"
maximum_pss_kb="${MAXIMUM_PSS_KB:-300000}"
maximum_scroll_seconds="${MAXIMUM_SCROLL_SECONDS:-45}"
swipe_count="${SWIPE_COUNT:-90}"
case_count=106
last_case_id="prod_pie_themed_many_services"
package_name="com.swithun.cmpmermaid.sample"
activity_name="com.swithun.cmpmermaid.debugui.MermaidDebugActivity"
adb="${ANDROID_HOME:?ANDROID_HOME is required}/platform-tools/adb"

mkdir -p "$output_dir"
android install --device="$serial" --apks="$apk"
"$adb" -s "$serial" shell am force-stop "$package_name"
"$adb" -s "$serial" shell am start -W \
  -n "$package_name/$activity_name" \
  --ez openLoadTest true \
  >"$output_dir/launch.txt"
sleep 2

read_pss_kb() {
  "$adb" -s "$serial" shell dumpsys meminfo "$package_name" |
    awk '/TOTAL PSS:/ {print $3; exit}'
}

initial_pss_kb="$(read_pss_kb)"
peak_pss_kb="$initial_pss_kb"
android screen capture --device="$serial" -o "$output_dir/top.png" >/dev/null
started_at="$SECONDS"

for index in $(seq 1 "$swipe_count"); do
  "$adb" -s "$serial" shell input swipe 540 2050 540 450 220 >/dev/null
  if ((index % 4 == 0)); then
    current_pss_kb="$(read_pss_kb)"
    if ((current_pss_kb > peak_pss_kb)); then
      peak_pss_kb="$current_pss_kb"
    fi
  fi
done

scroll_seconds="$((SECONDS - started_at))"
android layout --device="$serial" -o "$output_dir/layout.json" >/dev/null
android screen capture --device="$serial" -o "$output_dir/bottom.png" >/dev/null
final_pss_kb="$(read_pss_kb)"
reached_last_case=false
if grep -q "$last_case_id" "$output_dir/layout.json"; then
  reached_last_case=true
fi

cat >"$output_dir/metrics.json" <<EOF
{
  "schemaVersion": 1,
  "device": "$serial",
  "caseCount": $case_count,
  "lastCaseId": "$last_case_id",
  "initialPssKb": $initial_pss_kb,
  "peakPssKb": $peak_pss_kb,
  "finalPssKb": $final_pss_kb,
  "scrollSeconds": $scroll_seconds,
  "reachedLastCase": $reached_last_case,
  "thresholds": {
    "maximumPssKb": $maximum_pss_kb,
    "maximumScrollSeconds": $maximum_scroll_seconds
  }
}
EOF

cat "$output_dir/metrics.json"
if [[ "$reached_last_case" != true ]]; then
  echo "Android load test did not reach the final corpus case" >&2
  exit 1
fi
if ((peak_pss_kb > maximum_pss_kb)); then
  echo "Android peak PSS $peak_pss_kb KiB exceeded $maximum_pss_kb KiB" >&2
  exit 1
fi
if ((scroll_seconds > maximum_scroll_seconds)); then
  echo "Android scrolling took ${scroll_seconds}s; budget is ${maximum_scroll_seconds}s" >&2
  exit 1
fi
