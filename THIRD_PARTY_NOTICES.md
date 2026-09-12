# Third-Party Notices

## Mermaid

The KMP runtime contains Kotlin translations of selected Mermaid Flowchart,
Sequence, Class, State, Entity Relationship, Gantt, and Pie parsing,
layout-adapter, and rendering algorithms. The reference-rendering tool also
downloads Mermaid.js from npm. Mermaid.js itself is not embedded in the
runtime libraries.

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

## D3 Shape

The KMP runtime contains Kotlin translations of the line curve implementations
used by Mermaid from `d3-shape 3.2.0`.

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
lexer/parser runtime used by Mermaid's Flowchart, Sequence, Class, State,
Entity Relationship, and Gantt grammars.

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
