# Jison parser table size report

Release **0.1.21**, Mermaid.js **12.0.0**.

The 12 generated Jison parsers now share immutable actions and symbol lists,
following upstream's `$V*` arrays and `o(k, v, o2, l)` construction. A generic
Kotlin helper fills the same ordered Map rows with sparse overrides.
Parsing, layout, renderer exports, fonts and diagram coverage are unchanged.
The [source inventory](upstream-jison-table-generation-map.md) records each stage.

## Linked iOS measurements

All values below use decimal MB. Both the 0.1.20 baseline and the candidate
were freshly compiled with the same Kotlin/Native release task, Java 17,
arm64 target and export settings. The historical 0.1.10 framework is the
published binary; it was downloaded, not recompiled.
All three frameworks were linked into the same minimal Objective-C consumer
using Apple clang 21, `-O2`, `-ObjC` and `-dead_strip`, targeting iOS 14.
The published 0.1.20 XCFramework was independently downloaded and linked as
well: every linked byte count below exactly matches the rebuilt baseline.
Its static archive differs by 88 bytes; both framework hashes are recorded.

| Metric | Historical 0.1.10 | Before: 0.1.20 | After: 0.1.21 | Reduction from 0.1.20 |
| --- | ---: | ---: | ---: | ---: |
| Framework file-backed symbols | 35.83 MB | 38.86 MB | 35.24 MB | 3.61 MB |
| Mermaid named file-backed symbols | 13.31 MB | 13.58 MB | 9.96 MB | 3.63 MB |
| Jison table file-backed symbols | 5.20 MB | 5.20 MB | 1.57 MB | 3.63 MB |
| Complete linked probe executable | 44.06 MB | 48.08 MB | 44.47 MB | 3.61 MB |
| Compressed probe executable | 12.94 MB | 13.97 MB | 13.53 MB | 0.44 MB |
| Unlinked static framework archive | 129.76 MB | 136.73 MB | 128.13 MB | 8.60 MB |

The optimization removes about **69.8%** of table-attributed code and about
**9.3%** of the current framework's attributed file-backed symbols.
The state-chunk methods alone shrink from 4,686,912 to 79,660 bytes.
Shared action and symbol initialization still occupies space.

Relative to the historical 0.1.10 binary, framework attribution decreases by
0.58 MB; the whole linked probe grows by 0.41 MB. Changes between 0.1.10
and 0.1.20, including runtime and formula support, remain in the candidate.
Subtracting the 3.61 MB optimization from a historical application's size
would omit those intervening changes.

These are reproducible library/probe measurements, not an IPA rebuild.
Framework-attributed symbols exclude zero-fill sections, linker padding and
LINKEDIT; the executable row includes file overhead and LINKEDIT.
Fonts are external resources and are excluded from all executable rows.
Compressed probe bytes are not compressed IPA bytes.
The [machine-readable measurements](assets/jison-table-size.json) preserve
the exact byte counts and framework hashes.

## Initialization and equivalence

Eight fresh JVM processes per version, interleaved, initialize all 12 table
objects, including their lexer and production data. Median initialization
is **35.79 ms before / 37.85 ms after**: an observed 2.07 ms increase.
This is a JVM measurement, not an iOS launch-time measurement.

The independent official oracle checks **1,611 states / 20,857 entries**,
including missing keys, key order and every goto/shift/reduce/accept value.
All 12 exhaustive family tests and three construction tests pass.
The full JVM suites pass: **Core 1,123 / Compose 75**, zero failures.
Emitter tests pass 2/2. Existing unsupported capabilities and visual Review
states retain their status.
All 12 generated files also retain byte-identical symbol, terminal, production
and lexer data outside the table-construction section.
Wasm suites pass Core 1,109 / Compose 62; iOS Simulator Arm64 suites pass
Core 1,109 / Compose 76. Modern Android debug/release, Kotlin 1.7 Android and
its external consumer, iOS arm64 release linking and 0.1.21 publication
coordinates pass. Release tooling passes 13 tests.

Release packaging also corrects Arimo's notice to the bundled fonts' OFL 1.1
metadata, includes all referenced license files in the CocoaPods archive and
its resource directory, and provides a separate license bundle for hosts.
No font binary changes are included.

The large historical 33-family, 8,448-case visual matrix and device reports
retain their recorded versions. Current platform, production-corpus visual,
malformed-input, publication and privacy checks run through the
[Quality Gate](https://github.com/swithun-liu/cmp-mermaid/actions/workflows/quality.yml)
and immutable-tag
[Release workflow](https://github.com/swithun-liu/cmp-mermaid/actions/workflows/release.yml).
The [0.1.21 release assets](https://github.com/swithun-liu/cmp-mermaid/releases/tag/v0.1.21)
preserve the exact-commit verification summary and fresh production-corpus
visual evidence without replacing the historical full-family matrix.

## Reproduction

Build each checkout with `:mermaid-compose:linkPodReleaseFrameworkIosArm64`
and run:

```bash
bash tools/release/measure-ios-linked-size.sh \
  /path/to/CMPMermaid.framework /path/to/measurement-output
```

Raw LinkMaps remain local; the published JSON contains no absolute paths.
Run `JisonInitialization.java` in fresh JVMs with each core compilation's
runtime classpath to reproduce the separate initialization measurement.
