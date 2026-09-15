# Third-Party Notices

## Mermaid

The KMP runtime contains Kotlin translations of selected Mermaid Flowchart,
XY Chart, Sequence, Class, State, Entity Relationship, Gantt, Pie, User
Journey, Requirement, and Git Graph parsing, layout-adapter, and rendering
algorithms. The reference-rendering tool also downloads Mermaid.js from npm.
Mermaid.js itself is not embedded in the runtime libraries.

The Android sample bundles Mermaid.js `12.0.0` only for on-demand official
WebView comparisons. The npm distribution is syntax transpiled to Chrome 87
by esbuild without changing Mermaid's rendering semantics. It is not included
in `mermaid-core` or `mermaid-compose`.

Upstream npm bundle SHA-256:

`28fca7ae6ebc7ed7bb63bde63136a74bfef14f296a57e403657eeb8b32836073`

Bundled Chrome 87-compatible file SHA-256:

`9d886dff8ae78dc6f9579732c0aa33695d7371b8206826689bf7bdaf8568960a`

Mermaid is distributed under the MIT License:

Copyright (c) 2014 - 2022 Knut Sveidqvist

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## @mermaid-js/parser

The Git Graph parser is a Kotlin translation of the Git Graph grammar and
parser-adapter behavior in `@mermaid-js/parser 2.0.0`. The upstream grammar is
generated with Langium `4.2.1`; neither `@mermaid-js/parser` nor the Langium
runtime is embedded in the production libraries.

`@mermaid-js/parser` is distributed under the MIT License:

Copyright (c) 2023 Yokozuna59

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## Kaml

The KMP runtime uses Kaml `0.104.0` to parse Flowchart metadata with a
multiplatform YAML 1.2 implementation, replacing Mermaid's JavaScript-only
`js-yaml` dependency.

Kaml is distributed under the Apache License 2.0:

https://www.apache.org/licenses/LICENSE-2.0

Project:

https://github.com/charleskorn/kaml

## kotlinx-datetime

The KMP runtime uses kotlinx-datetime `0.7.1` for portable Gantt date parsing,
calendar arithmetic, and formatting.

kotlinx-datetime is distributed under the Apache License 2.0:

https://www.apache.org/licenses/LICENSE-2.0

Project:

https://github.com/Kotlin/kotlinx-datetime

## dagre-d3-es

The KMP runtime contains Kotlin translations of the Graphlib and Dagre
production modules from `dagre-d3-es 7.0.14`.

Original dagre-d3 copyright: Copyright (c) 2013 Chris Pettitt

Original dagre copyright: Copyright (c) 2012-2014 Chris Pettitt

Original graphlib copyright: Copyright (c) 2012-2014 Chris Pettitt

Copyright (c) 2022-2024 Thibaut Lassalle, David Newell, Alois Klink,
Sidharth Vinod and dagre-es contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## D3 Array, Scale, And Shape

The KMP runtime contains Kotlin translations of the tick, linear/band scale,
line, and curve implementations used by Mermaid from `d3-array 3.2.4`,
`d3-scale 4.0.2`, and `d3-shape 3.2.0`.

Copyright 2010-2022 Mike Bostock

Permission to use, copy, modify, and/or distribute this software for any purpose
with or without fee is hereby granted, provided that the above copyright notice
and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS
OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
THIS SOFTWARE.

## Jison

The KMP runtime contains Kotlin translations of the Jison `0.4.18` generated
lexer/parser runtime used by Mermaid's Flowchart, XY Chart, Sequence, Class,
State, Entity Relationship, Gantt, User Journey, and Requirement grammars.

Copyright (c) Zach Carter

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## Marked

The KMP runtime contains a Kotlin translation of the Marked `16.4.2` lexer and
tokenizer behavior consumed by Mermaid Flowchart, Class, State, and Entity
Relationship labels.

Copyright (c) 2018+, MarkedJS (https://github.com/markedjs/)

Copyright (c) 2011-2018, Christopher Jeffrey (https://github.com/chjj/)

Marked is distributed under the MIT License:

https://github.com/markedjs/marked/blob/v16.4.2/LICENSE.md

## elkjs

The KMP runtime embeds the generated `elk-worker.min.js` from `elkjs 0.9.3`.
It is the only JavaScript algorithm bundle executed by the runtime.

elkjs is distributed under the Eclipse Public License 2.0:

https://github.com/kieler/elkjs

The complete license text is included at:

`third_party/licenses/ELKJS-EPL-2.0.txt`

The corresponding source is available from the upstream `elkjs 0.9.3`
release:

https://github.com/kieler/elkjs/tree/0.9.3

## quickjs-kt And QuickJS

The KMP runtime uses `quickjs-kt 1.0.5` to execute the locked elkjs worker in
an isolated runtime.

quickjs-kt is distributed under the Apache License 2.0:

https://github.com/dokar3/quickjs-kt

QuickJS is distributed under the MIT License:

Copyright (c) 2017-2021 Fabrice Bellard

Copyright (c) 2017-2021 Charlie Gordon

https://bellard.org/quickjs/

## AndroidSVG

The Android asset provider uses AndroidSVG `1.4` for SVG image nodes.

AndroidSVG is distributed under the Apache License 2.0:

https://github.com/BigBadaboom/androidsvg

## Arimo

The Compose adapter bundles Arimo Regular, Bold, Italic, and Bold Italic as an
Arial-compatible default font family.

Arimo is distributed under the Apache License 2.0. The complete license text
is included at:

`third_party/licenses/ARIMO-APACHE-2.0.txt`

Project:

https://fonts.google.com/specimen/Arimo

## Droid Sans Fallback

The Compose adapter bundles Droid Sans Fallback for CJK text runs that are
not covered by Arimo. The font is loaded only when a diagram contains CJK
characters.

Digitized data copyright Google Corporation (c) 2006

Droid Sans Fallback is distributed under the Apache License 2.0. The complete
license text is included at:

`third_party/licenses/DROID-SANS-FALLBACK-APACHE-2.0.txt`

Upstream:

https://github.com/aosp-mirror/platform_frameworks_base/blob/main/data/fonts/DroidSansFallback.ttf

Bundled file SHA-256:

`21b96a0377f067833a93af3082eb28d4ffab7a8cd46bfd513286f1d64b7b0949`

## Droid Sans Mono

The Compose adapter bundles Droid Sans Mono for deterministic HTML
`code`/`kbd`/`samp`/`tt` label spans across platforms.

Digitized data copyright Google Corporation (c) 2007

Droid Sans Mono is distributed under the Apache License 2.0. The complete
license text is included at:

`third_party/licenses/DROID-SANS-FALLBACK-APACHE-2.0.txt`

Upstream:

https://android.googlesource.com/platform/frameworks/base/+/android-10.0.0_r1/data/fonts/

Bundled file SHA-256:

`0361584c155c1a80c0c259eb69cc42ffaed4ffbacdf2cc862fd77c1e20d8b33e`

## Noto Sans Symbols 2

The Compose adapter bundles Noto Sans Symbols 2 for Dingbat glyphs that are
not covered by Arimo, such as the check mark used in Mermaid labels.

Copyright 2018 The Noto Project Authors

Noto Sans Symbols 2 is distributed under the SIL Open Font License 1.1. The
complete license text is included at:

`third_party/licenses/NOTO-SANS-SYMBOLS-2-OFL-1.1.txt`

Upstream revision:

https://github.com/notofonts/noto-fonts/tree/ffebf8c1ee449e544955a7e813c54f9b73848eac

Bundled file SHA-256:

`630846d528dbe4c4981370a4d0a9475a1fd1491a129bb411f8e157cdb5de13c6`

## AndroidX WebKit

The Android sample uses AndroidX WebKit `1.17.0` to serve bundled Mermaid
comparison assets to its isolated WebView through `WebViewAssetLoader`.

AndroidX WebKit is distributed under the Apache License 2.0:

https://www.apache.org/licenses/LICENSE-2.0

Project:

https://developer.android.com/jetpack/androidx/releases/webkit

## esbuild

The official-reference generator uses esbuild `0.28.2` to lower the bundled
Mermaid.js syntax for the Android test device's WebView. esbuild is a
development tool and is not distributed in the app.

esbuild is distributed under the MIT License:

https://github.com/evanw/esbuild

## WHATWG HTML Named Character References

`Html5NamedEntities.kt` is generated from the WHATWG HTML Standard
`entities.json` data:

https://html.spec.whatwg.org/entities.json

The generator pins the source by SHA-256. WHATWG specification licensing
information is available at:

https://whatwg.org/ipr-policy
