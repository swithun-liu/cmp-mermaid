#!/usr/bin/env bash
set -euo pipefail

framework="${1:?usage: measure-ios-linked-size.sh <CMPMermaid.framework> <output-directory>}"
output="${2:?usage: measure-ios-linked-size.sh <CMPMermaid.framework> <output-directory>}"
root="$(dirname "${BASH_SOURCE[0]}")"
mkdir -p "$output"
sdk="$(xcrun --sdk iphoneos --show-sdk-path)"
xcrun clang \
  -target arm64-apple-ios14.0 -isysroot "$sdk" -O2 -fobjc-arc \
  -F "$(dirname "$framework")" -framework CMPMermaid \
  -framework Foundation -framework UIKit -framework Metal -framework QuartzCore \
  -framework CoreText -framework CoreGraphics -framework Security -framework CoreFoundation \
  -lc++ -lz -liconv -lsqlite3 \
  -Wl,-ObjC -Wl,-dead_strip -Wl,-map,"$output/linked.map" \
  "$root/ios-smoke/CMPMermaidSize.m" -o "$output/linked"
xcrun strip -S "$output/linked"
xcrun size -m "$output/linked" > "$output/sections.txt"
python3 - "$framework" "$output" <<'PY'
import hashlib
import json
import pathlib
import re
import sys
import zipfile

framework, output = map(pathlib.Path, sys.argv[1:])
binary = output / "linked"
objects = {}
symbols = []
zero_fill_ranges = []
phase = ""
for line in (output / "linked.map").read_text(errors="surrogateescape").splitlines():
    if line.startswith("# Object files:"):
        phase = "objects"
    elif line.startswith("# Sections:"):
        phase = "sections"
    elif line.startswith("# Symbols:"):
        phase = "symbols"
    elif line.startswith("# Dead Stripped Symbols:"):
        phase = "dead"
    elif phase == "objects":
        match = re.match(r"\[\s*(\d+)\]\s+(.+)", line)
        if match:
            objects[int(match[1])] = match[2]
    elif phase == "sections":
        match = re.match(r"0x([0-9A-Fa-f]+)\s+0x([0-9A-Fa-f]+)\s+\S+\s+(\S+)", line)
        if match and match[3] in ("__bss", "__common", "__thread_bss"):
            start, size = int(match[1], 16), int(match[2], 16)
            zero_fill_ranges.append((start, start + size))
    elif phase == "symbols":
        match = re.match(r"0x([0-9A-Fa-f]+)\s+0x([0-9A-Fa-f]+)\s+\[\s*(\d+)\]\s+(.+)", line)
        if match:
            symbols.append((int(match[2], 16), int(match[3]), match[4], int(match[1], 16)))

framework_objects = {index for index, name in objects.items() if "CMPMermaid(" in name}
attributed = [(size, name) for size, index, name, _ in symbols if index in framework_objects]
file_backed = [
    (size, name) for size, index, name, address in symbols
    if index in framework_objects and not any(start <= address < end for start, end in zero_fill_ranges)
]
sections = {
    name: int(size) for name, size in re.findall(
        r"^Segment\s+(\S+):\s+(\d+)", (output / "sections.txt").read_text(), re.M
    ) if name != "__PAGEZERO"
}
with zipfile.ZipFile(output / "linked.zip", "w", compression=zipfile.ZIP_DEFLATED, compresslevel=9) as archive:
    archive.write(binary, "linked")
report = {
    "target": "arm64-apple-ios14.0",
    "compilerFlags": ["-O2", "-fobjc-arc", "-ObjC", "-dead_strip"],
    "frameworkSha256": hashlib.sha256((framework / "CMPMermaid").read_bytes()).hexdigest(),
    "frameworkArchiveBytes": (framework / "CMPMermaid").stat().st_size,
    "linkedExecutableBytes": binary.stat().st_size,
    "compressedExecutableBytes": (output / "linked.zip").stat().st_size,
    "frameworkAttributedSymbolBytes": sum(size for size, _ in attributed),
    "fileBackedFrameworkAttributedSymbolBytes": sum(size for size, _ in file_backed),
    "mermaidNamedSymbolBytes": sum(size for size, name in attributed if "com.swithun.cmpmermaid" in name),
    "fileBackedMermaidNamedSymbolBytes": sum(size for size, name in file_backed if "com.swithun.cmpmermaid" in name),
    "jisonNamedSymbolBytes": sum(size for size, name in attributed if "Jison" in name),
    "jisonTableNamedSymbolBytes": sum(size for size, name in attributed if "JisonTables" in name),
    "fileBackedJisonTableNamedSymbolBytes": sum(size for size, name in file_backed if "JisonTables" in name),
    "jisonStateChunkSymbolBytes": sum(size for size, name in attributed if "JisonTables.stateChunk" in name),
    "sections": sections,
}
(output / "measurement.json").write_text(json.dumps(report, indent=2) + "\n")
print(json.dumps(report, indent=2))
PY
