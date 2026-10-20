package com.swithun.cmpmermaid.core

// Mermaid.js 12.0.0:
// packages/mermaid/src/diagrams/common/common.ts -> katexRegex/hasKatex.
internal val MERMAID_KATEX_PATTERN = Regex("""\$\$(.*?)\$\$""")
