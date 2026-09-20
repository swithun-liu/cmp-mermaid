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
read -r case_count last_case_id < <(
  node --input-type=module -e '
    import { cases } from "./tools/official-reference/production-corpus.mjs";
    const lastCase = cases.at(-1);
    if (lastCase == null) process.exit(1);
    console.log(`${cases.length} ${lastCase.id}`);
  '
)
package_name="com.swithun.cmpmermaid.sample"
activity_name="com.swithun.cmpmermaid.debugui.MermaidDebugActivity"
adb="${ANDROID_HOME:?ANDROID_HOME is required}/platform-tools/adb"
completion_marker="CMP_MERMAID_LOAD_TEST_COMPLETE $last_case_id"

mkdir -p "$output_dir"
android install --device="$serial" --apks="$apk"
"$adb" -s "$serial" shell am force-stop "$package_name"
"$adb" -s "$serial" logcat -c
"$adb" -s "$serial" shell am start -W \
  -n "$package_name/$activity_name" \
  --ez openLoadTest true \
  --ez autoRunLoadTest true \
  >"$output_dir/launch.txt"

read_pss_kb() {
  "$adb" -s "$serial" shell dumpsys meminfo "$package_name" |
    awk '/TOTAL PSS:/ {print $3; exit}'
}

initial_pss_kb="$(read_pss_kb)"
peak_pss_kb="$initial_pss_kb"
android screen capture --device="$serial" -o "$output_dir/top.png" >/dev/null
started_at="$SECONDS"
reached_last_case=false

while ((SECONDS - started_at <= maximum_scroll_seconds)); do
  current_pss_kb="$(read_pss_kb)"
  if ((current_pss_kb > peak_pss_kb)); then
    peak_pss_kb="$current_pss_kb"
  fi
  if "$adb" -s "$serial" logcat -d |
    grep -Fq "$completion_marker"; then
    reached_last_case=true
    break
  fi
  sleep 1
done

scroll_seconds="$((SECONDS - started_at))"
android layout --device="$serial" -o "$output_dir/layout.json" >/dev/null
android screen capture --device="$serial" -o "$output_dir/bottom.png" >/dev/null
final_pss_kb="$(read_pss_kb)"
if ((final_pss_kb > peak_pss_kb)); then
  peak_pss_kb="$final_pss_kb"
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
