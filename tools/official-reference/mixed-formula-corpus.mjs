// Mermaid.js 12.0.0 common.ts -> renderKatexUnsanitized and createText.ts.
// These sources are shared verbatim by the official and native audit tools.
export const mixedFormulaCases = [
  ['flow-single', 'flowchart LR\nA["$$x^2$$"]'],
  ['flow-text', 'flowchart LR\nA["energy $$E=mc^2$$ joules"]'],
  ['flow-multiple', 'flowchart LR\nA["$$a^2$$ + $$b^2$$ = c"]'],
  ['flow-fraction', String.raw`flowchart LR
A["left $$\frac{a}{b}$$ right $$x$$ end"]`],
  ['flow-br', 'flowchart LR\nA["energy<br/>$$E=mc^2$$"]'],
  ['flow-newline', 'flowchart LR\nA["energy\n$$E=mc^2$$"]'],
  ['flow-formula-br', 'flowchart LR\nA["$$x$$<br/>$$y$$"]'],
  ['flow-repeated', 'flowchart LR\nA["$$x$$ + $$x$$"]'],
  ['flow-spaces', 'flowchart LR\nA["  A   $$x$$   B  "]'],
  ['flow-entities', 'flowchart LR\nA["A&nbsp; $$x$$ &amp; B"]'],
  ['flow-html', 'flowchart LR\nA["<b>energy</b> $$E=mc^2$$ <i>joules</i>"]'],
  ['flow-neutral-html', 'flowchart LR\nA["before<span> left <b>middle</b> right </span>after $$x$$"]'],
  ['flow-adjacent-html', 'flowchart LR\nA["<span> a </span><span> b </span>$$x$$"]'],
  ['flow-small-html', 'flowchart LR\nA["<small>small</small> $$x$$ large"]'],
  ['flow-color-html', 'flowchart LR\nA["<span>plain <i style=\'color:red\'>red</i> plain</span> $$x$$"]'],
  ['flow-tall', String.raw`flowchart LR
A["sum $$\sum_{i=1}^{n}\frac{i}{i+1}$$ done"]`],
  ['flow-wide', String.raw`flowchart LR
A["identity $$a+b+c+d+e+f+g+h+i+j+k+l=m+n+o+p+q+r+s+t$$ result"]`],
  ['flow-edge', 'flowchart LR\nA -->|"mass $$E=mc^2$$ units"| B'],
  ['flow-rotation', 'flowchart BT\nA["value $$x^2$$"] --> B["$$x$$ + $$y$$"]'],
  ['state-edge', 'stateDiagram-v2\nPending --> Approved : sum $$x^2$$ plus $$y^2$$'],
  ['class-title', 'classDiagram\nclass A["value $$x^2$$"]'],
  ['er-title', 'erDiagram\nA["value $$x^2$$"] { string name }'],
  ['er-relation', 'erDiagram\nA ||--o{ B : "value $$x^2$$"'],
  ['block', 'block-beta\nA["value $$x^2$$"]'],
  ['kanban', 'kanban\ntodo[value $$x^2$$]\n  task[Review]'],
  ['mindmap', 'mindmap\nroot["value $$x^2$$"]'],
  ['usecase', 'usecase-beta\nA -- "`value $$x^2$$`" --> B'],
  ['requirement', 'requirementDiagram\nrequirement formula_label {\nid: 1\ntext: "value $$x^2$$ plus $$y$$"\nrisk: low\nverifymethod: test\n}'],
].map(([id, source]) => ({ id, source }));
