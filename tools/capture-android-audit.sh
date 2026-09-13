#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUTPUT_DIR="${OUTPUT_DIR:-${ROOT_DIR}/captures/local/audit/current}"
CAPTURE_ATTEMPTS="${CAPTURE_ATTEMPTS:-3}"
READY_TIMEOUT_SECONDS="${READY_TIMEOUT_SECONDS:-45}"
READY_POLL_SECONDS="${READY_POLL_SECONDS:-1}"
POST_READY_WAIT_SECONDS="${POST_READY_WAIT_SECONDS:-1}"
MIN_CAPTURE_BYTES="${MIN_CAPTURE_BYTES:-24000}"
CONTENT_LUMA_THRESHOLD="${CONTENT_LUMA_THRESHOLD:-220}"
CAPTURE_PREVIEWS="${CAPTURE_PREVIEWS:-Native Official}"
CAPTURE_CASE_IDS="${CAPTURE_CASE_IDS:-}"
CAPTURE_LAYOUT="${CAPTURE_LAYOUT:-elk}"
PACKAGE_NAME="io.github.cmpmermaid.sample"
ACTIVITY_NAME="${PACKAGE_NAME}/io.github.cmpmermaid.debugui.MermaidDebugActivity"
AUDIT_READY_MARKER="cmp-mermaid-audit:ready"
AUDIT_ERROR_MARKER="cmp-mermaid-audit:error:"

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

if ! command -v ffmpeg >/dev/null; then
    echo "ffmpeg is required for screenshot content validation." >&2
    exit 1
fi

mkdir -p "${OUTPUT_DIR}"
case_file="$(mktemp)"
ui_dump_file="$(mktemp)"
remote_ui_dump="/sdcard/cmp-mermaid-audit-$$.xml"
trap 'rm -f "${case_file}" "${ui_dump_file}"; adb -s "${ANDROID_SERIAL}" shell rm -f "${remote_ui_dump}" >/dev/null 2>&1 || true' EXIT

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

capture_has_content() {
    local output_file="$1"
    local min_luma

    if [[ "$(stat -f%z "${output_file}")" -lt "${MIN_CAPTURE_BYTES}" ]]; then
        return 1
    fi
    min_luma="$(
        ffmpeg \
            -hide_banner \
            -loglevel error \
            -i "${output_file}" \
            -vf "crop=iw:ih-200:0:100,signalstats,metadata=print:file=-" \
            -frames:v 1 \
            -f null \
            - |
            awk -F= '/lavfi.signalstats.YMIN/ { print $2; exit }'
    )"
    [[ -n "${min_luma}" && "${min_luma}" -lt "${CONTENT_LUMA_THRESHOLD}" ]]
}

wait_for_audit_ready() {
    local output_file="$1"
    local deadline=$((SECONDS + READY_TIMEOUT_SECONDS))
    local error_status
    local renderer_ready=false

    while ((SECONDS < deadline)); do
        if adb -s "${ANDROID_SERIAL}" shell uiautomator dump "${remote_ui_dump}" \
            </dev/null \
            >/dev/null 2>&1 &&
            adb -s "${ANDROID_SERIAL}" pull "${remote_ui_dump}" "${ui_dump_file}" \
                </dev/null \
                >/dev/null 2>&1; then
            if rg -q "content-desc=\"${AUDIT_ERROR_MARKER}" "${ui_dump_file}"; then
                error_status="$(
                    rg -o "content-desc=\"${AUDIT_ERROR_MARKER}[^\"]*" "${ui_dump_file}" |
                        head -n 1
                )"
                echo "Audit renderer failed: ${error_status}" >&2
                return 1
            fi
            if rg -q "content-desc=\"${AUDIT_READY_MARKER}\"" "${ui_dump_file}"; then
                if [[ "${renderer_ready}" == false ]]; then
                    renderer_ready=true
                    sleep "${POST_READY_WAIT_SECONDS}"
                fi
                android screen capture \
                    --device="${ANDROID_SERIAL}" \
                    -o "${output_file}" \
                    </dev/null \
                    >/dev/null
                if capture_has_content "${output_file}"; then
                    return 0
                fi
            fi
        fi
        sleep "${READY_POLL_SECONDS}"
    done

    echo "Audit renderer did not produce visible content within ${READY_TIMEOUT_SECONDS}s." >&2
    return 1
}

capture_preview() {
    local demo_id="$1"
    local preview="$2"
    local suffix
    local output_file
    local diagnostic_file
    suffix="$(printf '%s' "${preview}" | tr '[:upper:]' '[:lower:]')"
    output_file="${OUTPUT_DIR}/${demo_id}_${suffix}.png"
    diagnostic_file="${OUTPUT_DIR}/${demo_id}_${suffix}_failed.png"

    for ((attempt = 1; attempt <= CAPTURE_ATTEMPTS; attempt++)); do
        adb -s "${ANDROID_SERIAL}" shell am force-stop "${PACKAGE_NAME}" </dev/null
        adb -s "${ANDROID_SERIAL}" shell am start \
            -n "${ACTIVITY_NAME}" \
            --es auditDemoId "${demo_id}" \
            --es auditPreview "${preview}" \
            --es auditLayout "${CAPTURE_LAYOUT}" \
            >/dev/null </dev/null
        if ! wait_for_audit_ready "${output_file}"; then
            android screen capture \
                --device="${ANDROID_SERIAL}" \
                -o "${diagnostic_file}" \
                </dev/null || true
            echo \
                "Capture ${demo_id}/${preview} was not ready on attempt ${attempt}; retrying." \
                >&2
            continue
        fi
        rm -f "${diagnostic_file}"
        return
    done

    echo \
        "Capture ${demo_id}/${preview} did not produce a valid screenshot." \
        >&2
    return 1
}

while IFS= read -r demo_id; do
    for preview in "${preview_names[@]}"; do
        capture_preview "${demo_id}" "${preview}"
    done
done < "${case_file}"

echo "Captured Android audit images in ${OUTPUT_DIR}"
