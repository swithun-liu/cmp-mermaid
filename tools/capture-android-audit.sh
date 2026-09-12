#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUTPUT_DIR="${OUTPUT_DIR:-${ROOT_DIR}/captures/local/audit/current}"
WAIT_SECONDS="${WAIT_SECONDS:-6}"
CAPTURE_ATTEMPTS="${CAPTURE_ATTEMPTS:-3}"
MIN_CAPTURE_BYTES="${MIN_CAPTURE_BYTES:-24000}"
CAPTURE_PREVIEWS="${CAPTURE_PREVIEWS:-Native Official}"
CAPTURE_CASE_IDS="${CAPTURE_CASE_IDS:-}"
CAPTURE_LAYOUT="${CAPTURE_LAYOUT:-elk}"
PACKAGE_NAME="io.github.cmpmermaid.sample"
ACTIVITY_NAME="${PACKAGE_NAME}/.MainActivity"

if [[ "${CAPTURE_LAYOUT}" != "elk" && "${CAPTURE_LAYOUT}" != "dagre" ]]; then
    echo "CAPTURE_LAYOUT must be 'elk' or 'dagre'." >&2
    exit 1
fi

if [[ -z "${ANDROID_SERIAL:-}" ]]; then
    ANDROID_SERIAL="$(
        adb devices |
            awk 'NR > 1 && $2 == "device" { print $1; exit }'
    )"
fi

if [[ -z "${ANDROID_SERIAL}" ]]; then
    echo "No authorized Android device is connected." >&2
    exit 1
fi

mkdir -p "${OUTPUT_DIR}"
case_file="$(mktemp)"
trap 'rm -f "${case_file}"' EXIT

if [[ -n "${CAPTURE_CASE_IDS}" ]]; then
    printf '%s\n' "${CAPTURE_CASE_IDS}" |
        tr ', ' '\n\n' |
        sed '/^$/d' \
        > "${case_file}"
else
    node -e \
        "import('${ROOT_DIR}/tools/official-reference/cases.mjs').then(({ cases }) => cases.forEach(({ id }) => console.log(id)))" \
        > "${case_file}"
fi

read -r -a preview_names <<< "${CAPTURE_PREVIEWS}"

capture_preview() {
    local demo_id="$1"
    local preview="$2"
    local suffix
    local output_file
    suffix="$(printf '%s' "${preview}" | tr '[:upper:]' '[:lower:]')"
    output_file="${OUTPUT_DIR}/${demo_id}_${suffix}.png"

    for ((attempt = 1; attempt <= CAPTURE_ATTEMPTS; attempt++)); do
        adb -s "${ANDROID_SERIAL}" shell am force-stop "${PACKAGE_NAME}" </dev/null
        adb -s "${ANDROID_SERIAL}" shell am start \
            -n "${ACTIVITY_NAME}" \
            --es auditDemoId "${demo_id}" \
            --es auditPreview "${preview}" \
            --es auditLayout "${CAPTURE_LAYOUT}" \
            >/dev/null </dev/null
        sleep "${WAIT_SECONDS}"
        android screen capture \
            --device="${ANDROID_SERIAL}" \
            -o "${output_file}" \
            </dev/null
        if [[ "$(stat -f%z "${output_file}")" -ge "${MIN_CAPTURE_BYTES}" ]]; then
            return
        fi
        echo \
            "Capture ${demo_id}/${preview} was blank on attempt ${attempt}; retrying." \
            >&2
    done

    echo \
        "Capture ${demo_id}/${preview} remained below ${MIN_CAPTURE_BYTES} bytes." \
        >&2
    return 1
}

while IFS= read -r demo_id; do
    for preview in "${preview_names[@]}"; do
        capture_preview "${demo_id}" "${preview}"
    done
done < "${case_file}"

echo "Captured Android audit images in ${OUTPUT_DIR}"
