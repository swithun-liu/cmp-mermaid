import { cases as releaseCandidateCases } from './stability-corpus.mjs';

export const requiredFeaturesByKind = {
  flowchart: [
    'directions',
    'classic-shapes',
    'advanced-shapes',
    'subgraphs',
    'nested-subgraphs',
    'subgraph-direction',
    'solid-edges',
    'dotted-thick-edges',
    'edge-labels',
    'circle-cross-markers',
    'bidirectional-edges',
    'minimum-length',
    'classes-styles',
    'markdown-html',
    'frontmatter-config',
    'unicode',
  ],
  xychart: [
    'vertical',
    'horizontal',
    'categorical-axis',
    'numeric-axis',
    'explicit-domain',
    'automatic-domain',
    'bars',
    'lines',
    'mixed-plots',
    'legend',
    'data-labels',
    'outside-labels',
    'point-labels',
    'axis-rotation',
    'theme-palette',
    'component-visibility',
  ],
  quadrant: [
    'title',
    'x-axis',
    'y-axis',
    'quadrant-labels',
    'points',
    'boundary-points',
    'empty-points',
    'point-radius',
    'point-color',
    'point-stroke',
    'class-styles',
    'inline-precedence',
    'frontmatter-config',
    'theme-colors',
    'metadata',
    'unicode',
    'comments',
  ],
  timeline: [
    'lr',
    'td',
    'title',
    'periods',
    'events',
    'continued-events',
    'sections',
    'sectionless-colors',
    'disable-multicolor',
    'html-breaks',
    'frontmatter-config',
    'theme-colors',
    'redux-theme',
    'metadata',
    'unicode',
    'comments',
  ],
  kanban: [
    'sections',
    'tasks',
    'anonymous-items',
    'explicit-ids',
    'node-forms',
    'deeper-indentation',
    'comments',
    'wrapped-labels',
    'empty-sections',
    'ticket',
    'assigned',
    'priorities',
    'ticket-links',
    'section-width',
    'theme-colors',
    'markdown',
    'unicode',
  ],
  sequence: [
    'participants',
    'actor-types',
    'aliases',
    'autonumber',
    'activations',
    'arrow-families',
    'notes',
    'loops',
    'alt-opt',
    'parallel',
    'critical',
    'break',
    'rect',
    'boxes',
    'create-destroy',
    'self-messages',
  ],
  class: [
    'members',
    'visibility',
    'generics',
    'annotations',
    'relation-markers',
    'two-way-relations',
    'relation-labels',
    'cardinalities',
    'lollipop',
    'namespaces',
    'nested-namespaces',
    'direction',
    'notes',
    'classes-styles',
    'markdown',
    'metadata',
  ],
  state: [
    'simple-states',
    'descriptions',
    'aliases',
    'start-end',
    'composites',
    'nested-composites',
    'choice',
    'fork-join',
    'concurrency',
    'direction',
    'notes',
    'classes-styles',
    'links',
    'markdown',
    'metadata',
    'unicode',
  ],
  er: [
    'entities',
    'aliases',
    'unicode',
    'attributes',
    'optional-types',
    'keys',
    'comments',
    'cardinalities',
    'identifying',
    'non-identifying',
    'direction',
    'subgraphs',
    'nested-subgraphs',
    'classes-styles',
    'layout-config',
    'metadata',
  ],
  gantt: [
    'date-formats',
    'duration-units',
    'dependencies',
    'task-states',
    'milestones',
    'excludes',
    'weekends',
    'axis-format',
    'tick-interval',
    'sections',
    'compact-mode',
    'top-axis',
    'vertical-markers',
    'links',
    'frontmatter-config',
    'unicode',
  ],
  pie: [
    'basic-slices',
    'show-data',
    'title',
    'escaped-labels',
    'duplicate-labels',
    'zero-values',
    'decimal-values',
    'donut',
    'legend-right',
    'legend-center',
    'legend-transforms',
    'static-highlight',
    'theme-colors',
    'many-slices',
    'metadata',
    'unicode',
  ],
  journey: [
    'sections',
    'scores',
    'decimal-scores',
    'single-actor',
    'multi-actor',
    'actor-order',
    'actor-reuse',
    'actorless-tasks',
    'title',
    'frontmatter-title',
    'accessibility',
    'comments',
    'configuration',
    'theme-colors',
    'long-task-text',
    'long-actor-text',
  ],
  requirement: [
    'requirement-types',
    'fields',
    'risk-levels',
    'verification-methods',
    'elements',
    'relationship-types',
    'reverse-relationships',
    'directions',
    'dagre-layout',
    'frontmatter-title',
    'accessibility',
    'markdown',
    'direct-styles',
    'classes',
    'theme-variables',
    'unicode',
    'comments',
  ],
  gitgraph: [
    'commits',
    'custom-ids',
    'messages',
    'tags',
    'commit-types',
    'branches',
    'quoted-branches',
    'checkout',
    'switch',
    'branch-order',
    'main-branch-config',
    'merges',
    'merge-customization',
    'cherry-pick',
    'merge-cherry-pick',
    'orientations',
    'parallel-commits',
    'visibility-config',
    'frontmatter-config',
    'title',
    'accessibility',
    'theme-variables',
    'unicode',
    'comments',
  ],
  mindmap: [
    'hierarchy',
    'irregular-indentation',
    'deep-hierarchy',
    'wide-hierarchy',
    'default-shape',
    'square-shape',
    'rounded-shape',
    'circle-shape',
    'cloud-shape',
    'bang-shape',
    'hexagon-shape',
    'markdown',
    'html-breaks',
    'entities',
    'comments',
    'frontmatter-title',
    'cose-bilkent-layout',
    'dagre-layout',
    'tidy-tree-layout',
    'sizing-config',
    'theme-variables',
    'unicode',
  ],
  packet: [
    'packet-header',
    'packet-beta-header',
    'explicit-ranges',
    'single-bit-fields',
    'bit-count-fields',
    'mixed-addressing',
    'row-splitting',
    'title',
    'frontmatter-title',
    'accessibility',
    'comments',
    'escaped-labels',
    'configuration',
    'show-bits',
    'hide-bits',
    'responsive-sizing',
    'unicode',
  ],
  radar: [
    'axis-declarations',
    'axis-labels',
    'positional-entries',
    'detailed-entries',
    'reference-reordering',
    'multiple-curves',
    'inferred-max',
    'explicit-range',
    'circle-graticule',
    'polygon-graticule',
    'legend',
    'hidden-legend',
    'ticks',
    'title',
    'frontmatter-title',
    'accessibility',
    'comments',
    'escaped-labels',
    'configuration',
    'responsive-sizing',
    'theme-variables',
    'unicode',
    'option-last-wins',
    'tick-cap',
    'curve-tension',
  ],
  sankey: [
    'sankey-header',
    'sankey-beta-header',
    'csv-records',
    'quoted-commas',
    'escaped-quotes',
    'blank-lines',
    'multi-stage-flow',
    'branching',
    'merging',
    'left-alignment',
    'right-alignment',
    'center-alignment',
    'justify-alignment',
    'gradient-links',
    'source-links',
    'target-links',
    'fixed-link-color',
    'show-values',
    'hide-values',
    'value-prefix',
    'value-suffix',
    'node-width',
    'node-padding',
    'outlined-labels',
    'custom-node-colors',
    'frontmatter-title',
    'responsive-sizing',
  ],
  treemap: [
    'treemap-header',
    'treemap-beta-header',
    'sections',
    'leaves',
    'numeric-values',
    'colon-values',
    'comma-values',
    'multiple-roots',
    'hierarchy',
    'deep-hierarchy',
    'irregular-indentation',
    'class-selectors',
    'class-definitions',
    'class-fill',
    'class-stroke',
    'class-stroke-width',
    'class-text-color',
    'class-font-style',
    'title',
    'frontmatter-title',
    'accessibility',
    'comments',
    'unicode',
    'theme',
    'responsive-sizing',
    'intrinsic-sizing',
    'padding',
    'diagram-padding',
    'show-values',
    'hide-values',
    'node-width',
    'node-height',
    'border-width',
    'value-font-size',
    'label-font-size',
    'thousands-format',
    'currency-format',
    'fixed-format',
    'percentage-format',
  ],
  venn: [
    'venn-beta-header',
    'title',
    'frontmatter-title',
    'sets',
    'quoted-identifiers',
    'bracket-labels',
    'unquoted-bracket-labels',
    'default-sizes',
    'explicit-sizes',
    'pairwise-unions',
    'multi-set-unions',
    'synthetic-pairwise-layout',
    'indented-text',
    'explicit-text',
    'labeled-text',
    'unlabeled-text',
    'numeric-text-identifiers',
    'set-styles',
    'intersection-styles',
    'text-styles',
    'fill',
    'stroke',
    'stroke-width',
    'fill-opacity',
    'text-color',
    'hex-colors',
    'rgb-colors',
    'rgba-colors',
    'responsive-sizing',
    'intrinsic-sizing',
    'width',
    'height',
    'padding',
    'debug-layout',
    'theme',
    'theme-variables',
    'comments',
    'unicode',
  ],
  ishikawa: [
    'ishikawa-header',
    'ishikawa-beta-header',
    'effect',
    'root-only',
    'top-level-causes',
    'alternating-causes',
    'nested-causes',
    'deep-hierarchy',
    'leaf-causes',
    'base-level-normalization',
    'irregular-indentation',
    'comments',
    'entities',
    'html-breaks',
    'frontmatter-title',
    'diagram-padding',
    'responsive-sizing',
    'intrinsic-sizing',
    'theme',
    'unicode',
    'long-wrapping',
  ],
  cynefin: [
    'cynefin-beta-header',
    'colon-header',
    'domains',
    'fixed-domain-layout',
    'quoted-items',
    'empty-domains',
    'duplicate-domain-replacement',
    'transitions',
    'transition-labels',
    'unlabelled-transitions',
    'self-loop-filtering',
    'confusion-items',
    'confusion-overflow',
    'domain-descriptions',
    'hidden-domain-descriptions',
    'wavy-boundaries',
    'straight-boundaries',
    'deterministic-seed',
    'width',
    'height',
    'padding',
    'responsive-sizing',
    'intrinsic-sizing',
    'theme',
    'theme-variables',
    'frontmatter-title',
    'accessibility',
    'comments',
    'entities',
    'unicode',
  ],
  agentflow: [
    'agentflow-beta-header',
    'directions',
    'shape-aliases',
    'canonical-shapes',
    'sequence-edges',
    'chained-edges',
    'fan-out',
    'labelled-edges',
    'reference-edges',
    'failure-edges',
    'flows',
    'nested-flows',
    'global-nodes',
    'collapsed-flows',
    'connectors',
    'connector-ref-bare',
    'connector-ref-dotted',
    'connector-ref-url',
    'single-line-metadata',
    'multiline-metadata',
    'custom-metadata',
    'container-metadata',
    'frontmatter-title',
    'accessibility',
    'comments',
    'entities',
    'unicode',
    'agentflow-config',
    'responsive-sizing',
    'intrinsic-sizing',
    'theme',
    'look',
  ],
};

const flowchartCases = [
  ...expandTemplate({
    kind: 'flowchart',
    id: 'orchestration',
    layout: 'dagre',
    aspectRatio: 1.65,
    features: [
      'directions',
      'classic-shapes',
      'subgraphs',
      'solid-edges',
      'edge-labels',
      'classes-styles',
    ],
    variants: [
      {
        slug: 'identity',
        title: 'Identity verification orchestration',
        scenario: 'Identity checks fan out to policy and risk services before approval.',
        request: 'Verification request',
        decision: 'Evidence complete',
        group: 'Decision services',
        first: 'Policy evaluation',
        second: 'Risk scoring',
        success: 'Identity approved',
        failure: 'Request remediation',
      },
      {
        slug: 'refund',
        title: 'Refund orchestration',
        scenario: 'A refund coordinates eligibility, ledger, payment, and notification work.',
        request: 'Refund request',
        decision: 'Eligible',
        group: 'Refund services',
        first: 'Reverse ledger',
        second: 'Payment reversal',
        success: 'Refund confirmed',
        failure: 'Manual review',
      },
    ],
    source: (value) => String.raw`
flowchart LR
  Start([${value.request}]) --> Check{${value.decision}?}
  subgraph Services["${value.group}"]
    direction TB
    First[${value.first}] --> Second[( ${value.second} )]
  end
  Check -- Yes --> First
  Check -- No --> Failure[${value.failure}]
  Second --> Success([${value.success}])
  classDef warning fill:#fff4dd,stroke:#a16207,color:#422006
  class Failure warning
`,
    expectedTexts: (value) => [value.request, value.success],
  }),
  ...expandTemplate({
    kind: 'flowchart',
    id: 'edge_matrix',
    layout: 'dagre',
    aspectRatio: 1.45,
    features: [
      'dotted-thick-edges',
      'circle-cross-markers',
      'bidirectional-edges',
      'minimum-length',
    ],
    variants: [
      {
        slug: 'replication',
        title: 'Replication edge semantics',
        scenario: 'Replication channels distinguish optional, failed, and priority paths.',
        labels: ['Primary', 'Relay', 'Replica', 'Archive', 'Audit', 'Recovery'],
      },
      {
        slug: 'delivery',
        title: 'Delivery edge semantics',
        scenario: 'Delivery routes distinguish probes, rejection, acknowledgement, and fallback.',
        labels: ['Ingress', 'Router', 'Worker', 'Partner', 'Ledger', 'Fallback'],
      },
    ],
    source: (value) => String.raw`
flowchart TB
  A((${value.labels[0]})) o--o B[${value.labels[1]}]
  B x--x C{${value.labels[2]}}
  C <--> D[${value.labels[3]}]
  D -. retry .-> E[( ${value.labels[4]} )]
  E ==>|priority| F([${value.labels[5]}])
  A ----> F
`,
    expectedTexts: (value) => [value.labels[0], value.labels[5]],
  }),
  ...expandTemplate({
    kind: 'flowchart',
    id: 'nested_domains',
    layout: 'dagre',
    aspectRatio: 1.65,
    features: [
      'nested-subgraphs',
      'subgraph-direction',
      'markdown-html',
      'unicode',
    ],
    variants: [
      {
        slug: 'regional_data',
        title: 'Regional data domains',
        scenario: 'Nested regional and storage domains preserve local layout directions.',
        outer: 'Global data plane',
        inner: 'Tokyo region',
        source: 'イベント intake',
        transform: 'Normalize<br/>and enrich',
        sink: 'Analytics store',
      },
      {
        slug: 'media_pipeline',
        title: 'Media processing domains',
        scenario: 'Nested ingest and processing domains coordinate media derivatives.',
        outer: 'Media platform',
        inner: 'Processing pool',
        source: 'Upload intake',
        transform: 'Transcode<br/>and inspect',
        sink: 'Delivery origin',
      },
    ],
    source: (value) => String.raw`
flowchart TB
  subgraph Outer["${value.outer}"]
    direction TB
    Source["${value.source}"]
    subgraph Inner["${value.inner}"]
      direction LR
      Queue[(Work queue)] --> Transform["${value.transform}"]
      Transform --> Review{"\`**Quality** accepted?\`"}
    end
    Source --> Queue
    Review -- Yes --> Sink[( ${value.sink} )]
    Review -- No --> Queue
  end
`,
    expectedTexts: (value) => [value.outer, value.sink],
  }),
  ...expandTemplate({
    kind: 'flowchart',
    id: 'shape_catalog',
    layout: 'dagre',
    aspectRatio: 1.7,
    features: ['advanced-shapes', 'frontmatter-config'],
    variants: [
      {
        slug: 'document_control',
        title: 'Document control shape catalog',
        scenario: 'Document workflow exercises advanced Mermaid shape declarations.',
        labels: ['Draft', 'Approval', 'Policy archive', 'Exception', 'Publication'],
      },
      {
        slug: 'network_control',
        title: 'Network control shape catalog',
        scenario: 'Network workflow exercises advanced Mermaid shape declarations.',
        labels: ['Request', 'Control plane', 'State store', 'Alert', 'Edge delivery'],
      },
    ],
    source: (value) => String.raw`
---
config:
  flowchart:
    curve: stepAfter
---
flowchart LR
  A@{ shape: doc, label: "${value.labels[0]}" }
  B@{ shape: processes, label: "${value.labels[1]}" }
  C@{ shape: lin-cyl, label: "${value.labels[2]}" }
  D@{ shape: bang, label: "${value.labels[3]}" }
  E@{ shape: cloud, label: "${value.labels[4]}" }
  A --> B --> C
  B --> D --> E
  C --> E
`,
    expectedTexts: (value) => [value.labels[0], value.labels[4]],
  }),
];

const xyChartCases = [
  ...expandTemplate({
    kind: 'xychart',
    id: 'mixed_vertical',
    layout: 'dagre',
    aspectRatio: 1.4,
    features: [
      'vertical',
      'categorical-axis',
      'explicit-domain',
      'bars',
      'lines',
      'mixed-plots',
      'legend',
    ],
    variants: [
      {
        slug: 'api_capacity',
        title: 'API capacity and demand',
        scenario: 'Capacity bars are compared with observed and forecast demand.',
        categories: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
        bars: [62, 68, 72, 78, 84, 88],
        lineA: [48, 52, 60, 69, 76, 73],
        lineB: [51, 57, 65, 72, 80, 82],
      },
      {
        slug: 'warehouse_slots',
        title: 'Warehouse slot utilization',
        scenario: 'Available slots are compared with actual and projected utilization.',
        categories: ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'],
        bars: [90, 90, 110, 110, 130, 130],
        lineA: [64, 72, 81, 88, 97, 104],
        lineB: [70, 77, 86, 94, 105, 116],
      },
    ],
    source: (value) => String.raw`
---
config:
  xyChart:
    showLegend: true
---
xychart
  title "${value.title}"
  x-axis ${array(value.categories)}
  y-axis "Units" 0 --> 140
  bar "Capacity" ${array(value.bars)}
  line "Observed" ${array(value.lineA)}
  line "Forecast" ${array(value.lineB)}
`,
    expectedTexts: (value) => [value.title, 'Capacity'],
  }),
  ...expandTemplate({
    kind: 'xychart',
    id: 'horizontal_labels',
    layout: 'dagre',
    aspectRatio: 1.4,
    features: [
      'horizontal',
      'data-labels',
      'outside-labels',
      'automatic-domain',
    ],
    variants: [
      {
        slug: 'review_queue',
        title: 'Review queue age',
        scenario: 'Horizontal bars expose queue age labels outside each bar.',
        categories: ['Security', 'Privacy', 'Legal', 'Finance', 'Operations'],
        values: [14, 9, 21, 7, 12],
      },
      {
        slug: 'build_duration',
        title: 'Build duration by target',
        scenario: 'Horizontal bars expose build duration labels outside each bar.',
        categories: ['Android', 'iOS', 'Desktop', 'Web', 'Server'],
        values: [18, 24, 11, 9, 16],
      },
    ],
    source: (value) => String.raw`
---
config:
  xyChart:
    showDataLabel: true
    showDataLabelOutsideBar: true
---
xychart horizontal
  title "${value.title}"
  x-axis ${array(value.categories)}
  bar "Minutes" ${array(value.values)}
`,
    expectedTexts: (value) => [value.title, String(value.values[0])],
  }),
  ...expandTemplate({
    kind: 'xychart',
    id: 'point_labels',
    layout: 'dagre',
    aspectRatio: 1.4,
    features: ['point-labels', 'numeric-axis', 'theme-palette'],
    variants: [
      {
        slug: 'model_efficiency',
        title: 'Model efficiency checkpoints',
        scenario: 'Named line points track milestone improvements.',
        points: [
          [88, 'Baseline'],
          [71, 'Quantized'],
          [54, 'Distilled'],
          [42, 'Target'],
        ],
      },
      {
        slug: 'database_latency',
        title: 'Database latency checkpoints',
        scenario: 'Named line points track query-plan improvements.',
        points: [
          [96, 'Original'],
          [72, 'Indexed'],
          [55, 'Rewritten'],
          [39, 'Cached'],
        ],
      },
    ],
    source: (value) => String.raw`
---
config:
  themeVariables:
    xyChart:
      plotColorPalette: "#2563eb, #f97316"
---
xychart
  title "${value.title}"
  x-axis 1 --> 4
  y-axis "Milliseconds" 0 --> 110
  line ${pointArray(value.points)}
`,
    expectedTexts: (value) => [value.title, value.points[0][1]],
  }),
  ...expandTemplate({
    kind: 'xychart',
    id: 'axis_controls',
    layout: 'dagre',
    aspectRatio: 1.4,
    features: ['axis-rotation', 'component-visibility'],
    variants: [
      {
        slug: 'monthly_retention',
        title: 'Monthly retention cohort',
        scenario: 'Rotated labels fit a compact chart with selected axis components hidden.',
        categories: ['January', 'February', 'March', 'April', 'May', 'June'],
        values: [82, 79, 76, 74, 72, 70],
      },
      {
        slug: 'quarterly_quality',
        title: 'Quarterly quality trend',
        scenario: 'Rotated labels fit a compact chart with selected axis components hidden.',
        categories: ['Q1 baseline', 'Q1 final', 'Q2 baseline', 'Q2 final', 'Q3 baseline', 'Q3 final'],
        values: [71, 78, 76, 84, 82, 89],
      },
    ],
    source: (value) => String.raw`
---
config:
  xyChart:
    width: 520
    height: 360
    showLegend: false
    xAxis:
      labelRotation: -45
      showTitle: false
    yAxis:
      showTick: false
---
xychart
  title "${value.title}"
  x-axis ${array(value.categories)}
  y-axis "Percent" 0 --> 100
  line ${array(value.values)}
`,
    expectedTexts: (value) => [value.title, value.categories[0]],
  }),
];

const sequenceCases = [
  ...expandTemplate({
    kind: 'sequence',
    id: 'request_lifecycle',
    layout: 'dagre',
    aspectRatio: 0.82,
    features: [
      'participants',
      'actor-types',
      'aliases',
      'autonumber',
      'activations',
      'alt-opt',
    ],
    variants: [
      {
        slug: 'session_refresh',
        title: 'Session refresh lifecycle',
        scenario: 'A client refreshes a session through gateway and identity services.',
        actor: 'Mobile user',
        gateway: 'Session gateway',
        service: 'Identity service',
        operation: 'Refresh session',
      },
      {
        slug: 'shipment_quote',
        title: 'Shipment quote lifecycle',
        scenario: 'A merchant obtains a shipment quote through routing services.',
        actor: 'Merchant',
        gateway: 'Shipping gateway',
        service: 'Routing service',
        operation: 'Request quote',
      },
    ],
    source: (value) => String.raw`
sequenceDiagram
  autonumber
  actor User as ${value.actor}
  participant API@{ type: "boundary", alias: "${value.gateway}" }
  participant Service@{ type: "control", alias: "${value.service}" }
  User->>+API: ${value.operation}
  API->>+Service: Validate request
  alt accepted
    Service-->>API: Result
  else rejected
    Service-->>API: Structured error
  end
  deactivate Service
  opt audit enabled
    API->>API: Append audit event
  end
  API-->>-User: Response
`,
    expectedTexts: (value) => [value.actor, value.operation],
  }),
  ...expandTemplate({
    kind: 'sequence',
    id: 'control_regions',
    layout: 'dagre',
    aspectRatio: 0.76,
    features: ['parallel', 'critical', 'break', 'loops', 'notes'],
    variants: [
      {
        slug: 'cache_rebuild',
        title: 'Cache rebuild controls',
        scenario: 'Parallel reads and a critical publish section coordinate cache rebuild.',
        caller: 'Scheduler',
        worker: 'Rebuilder',
        store: 'Cache',
      },
      {
        slug: 'index_rollout',
        title: 'Search index rollout controls',
        scenario: 'Parallel validation and a critical swap section coordinate index rollout.',
        caller: 'Release job',
        worker: 'Indexer',
        store: 'Search cluster',
      },
    ],
    source: (value) => String.raw`
sequenceDiagram
  participant Caller as ${value.caller}
  participant Worker as ${value.worker}
  participant Store as ${value.store}
  par prepare
    Caller->>Worker: Start build
  and inspect
    Caller->>Store: Read current version
  end
  loop bounded batches
    Worker->>Store: Write next batch
  end
  critical publish atomically
    Worker->>Store: Promote new version
  option lock unavailable
    Worker-->>Caller: Retry later
  end
  break validation failed
    Worker--xCaller: Abort rollout
  end
  Note over Worker,Store: Old version remains available
`,
    expectedTexts: (value) => [value.caller, 'Old version remains available'],
  }),
  ...expandTemplate({
    kind: 'sequence',
    id: 'dynamic_participants',
    layout: 'dagre',
    aspectRatio: 0.78,
    features: ['boxes', 'create-destroy', 'rect'],
    variants: [
      {
        slug: 'export_worker',
        title: 'Dynamic export worker',
        scenario: 'An export coordinator creates and destroys an isolated worker.',
        box: 'Export services',
        coordinator: 'Export coordinator',
        worker: 'CSV worker',
      },
      {
        slug: 'scan_worker',
        title: 'Dynamic scan worker',
        scenario: 'A security coordinator creates and destroys an isolated scanner.',
        box: 'Security services',
        coordinator: 'Scan coordinator',
        worker: 'Artifact scanner',
      },
    ],
    source: (value) => String.raw`
sequenceDiagram
  box rgb(239,246,255) ${value.box}
    participant API as ${value.coordinator}
  end
  create participant Worker as ${value.worker}
  API->>Worker: Start isolated task
  rect rgb(240,253,244)
    Worker->>Worker: Process input
    Worker-->>API: Stream progress
  end
  destroy Worker
  Worker-->>API: Final result
`,
    expectedTexts: (value) => [value.coordinator, value.worker],
  }),
  ...expandTemplate({
    kind: 'sequence',
    id: 'arrow_matrix',
    layout: 'dagre',
    aspectRatio: 0.8,
    features: ['arrow-families', 'self-messages'],
    variants: [
      {
        slug: 'transport',
        title: 'Transport signal matrix',
        scenario: 'Transport acknowledgements exercise open, closed, async, and failure arrows.',
        left: 'Transport client',
        right: 'Transport service',
      },
      {
        slug: 'workflow',
        title: 'Workflow signal matrix',
        scenario: 'Workflow acknowledgements exercise open, closed, async, and failure arrows.',
        left: 'Workflow client',
        right: 'Workflow engine',
      },
    ],
    source: (value) => String.raw`
sequenceDiagram
  participant A as ${value.left}
  participant B as ${value.right}
  A->B: Open request
  B-->A: Open response
  A->>B: Closed request
  B-->>A: Closed response
  A-)B: Async event
  B--xA: Failure event
  A<<->>B: Bidirectional sync
  A->>A: Update local state
`,
    expectedTexts: (value) => [value.left, 'Bidirectional sync'],
  }),
];

const classCases = [
  ...expandTemplate({
    kind: 'class',
    id: 'domain_model',
    layout: 'dagre',
    aspectRatio: 1.45,
    features: [
      'members',
      'visibility',
      'generics',
      'annotations',
      'relation-labels',
      'cardinalities',
    ],
    variants: [
      {
        slug: 'billing',
        title: 'Billing domain model',
        scenario: 'Invoices, line items, and payment attempts form a typed aggregate.',
        root: 'Invoice',
        child: 'LineItem',
        service: 'PaymentService',
      },
      {
        slug: 'learning',
        title: 'Learning domain model',
        scenario: 'Courses, lessons, and enrollment services form a typed aggregate.',
        root: 'Course',
        child: 'Lesson',
        service: 'EnrollmentService',
      },
    ],
    source: (value) => String.raw`
classDiagram
  class ${value.root} {
    <<aggregate>>
    +String id
    -List~${value.child}~ items
    +total() Decimal
  }
  class ${value.child} {
    +String id
    +Decimal amount
  }
  class ${value.service} {
    <<service>>
    +execute(${value.root}) Result~Boolean~
  }
  ${value.root} "1" *-- "1..*" ${value.child} : contains
  ${value.service} ..> ${value.root} : processes
`,
    expectedTexts: (value) => [value.root, value.service],
  }),
  ...expandTemplate({
    kind: 'class',
    id: 'namespace_tree',
    layout: 'dagre',
    aspectRatio: 1.55,
    features: ['namespaces', 'nested-namespaces', 'direction', 'notes'],
    variants: [
      {
        slug: 'observability',
        title: 'Observability namespace tree',
        scenario: 'Nested namespaces separate collection and storage responsibilities.',
        outer: 'observability',
        inner: 'collection',
        first: 'TraceReceiver',
        second: 'SpanStore',
      },
      {
        slug: 'commerce',
        title: 'Commerce namespace tree',
        scenario: 'Nested namespaces separate checkout and fulfillment responsibilities.',
        outer: 'commerce',
        inner: 'checkout',
        first: 'CheckoutCoordinator',
        second: 'FulfillmentGateway',
      },
    ],
    source: (value) => String.raw`
classDiagram
  direction LR
  namespace ${value.outer} {
    namespace ${value.inner} {
      class ${value.first}
    }
    class ${value.second}
  }
  ${value.first} --> ${value.second} : publishes
  note for ${value.first} "Owns request lifecycle"
`,
    expectedTexts: (value) => [value.first, 'Owns request lifecycle'],
  }),
  ...expandTemplate({
    kind: 'class',
    id: 'relation_matrix',
    layout: 'dagre',
    aspectRatio: 1.65,
    features: [
      'relation-markers',
      'two-way-relations',
      'lollipop',
    ],
    variants: [
      {
        slug: 'plugins',
        title: 'Plugin relation matrix',
        scenario: 'Plugin contracts exercise inheritance, composition, aggregation, and interfaces.',
        prefix: 'Plugin',
      },
      {
        slug: 'connectors',
        title: 'Connector relation matrix',
        scenario: 'Connector contracts exercise inheritance, composition, aggregation, and interfaces.',
        prefix: 'Connector',
      },
    ],
    source: (value) => String.raw`
classDiagram
  class ${value.prefix}Base
  class ${value.prefix}Config
  class ${value.prefix}Runtime
  class ${value.prefix}Registry
  class ${value.prefix}Port
  ${value.prefix}Base <|-- ${value.prefix}Runtime
  ${value.prefix}Runtime *-- ${value.prefix}Config
  ${value.prefix}Registry o-- ${value.prefix}Runtime
  ${value.prefix}Runtime <--> ${value.prefix}Registry
  ${value.prefix}Runtime --() ${value.prefix}Port
`,
    expectedTexts: (value) => [`${value.prefix}Runtime`, `${value.prefix}Port`],
  }),
  ...expandTemplate({
    kind: 'class',
    id: 'styled_metadata',
    layout: 'dagre',
    aspectRatio: 1.4,
    features: ['classes-styles', 'markdown', 'metadata', 'two-way-relations'],
    variants: [
      {
        slug: 'policy',
        title: 'Policy metadata model',
        scenario: 'Styled policy classes include Markdown labels and accessibility metadata.',
        titleText: 'Policy evaluation model',
        left: 'PolicyInput',
        right: 'PolicyDecision',
      },
      {
        slug: 'routing',
        title: 'Routing metadata model',
        scenario: 'Styled routing classes include Markdown labels and accessibility metadata.',
        titleText: 'Routing decision model',
        left: 'RouteInput',
        right: 'RouteDecision',
      },
    ],
    source: (value) => String.raw`
---
title: ${value.titleText}
accTitle: ${value.titleText}
accDescr: A compact decision model
---
classDiagram
  class ${value.left}["\`**${value.left}**\`"]
  class ${value.right}
  ${value.left} <--> ${value.right} : evaluates
  classDef input fill:#dbeafe,stroke:#1d4ed8,color:#172554
  class ${value.left} input
`,
    expectedTexts: (value) => [value.left, value.right],
  }),
];

const stateCases = [
  ...expandTemplate({
    kind: 'state',
    id: 'nested_lifecycle',
    layout: 'dagre',
    aspectRatio: 1.35,
    features: [
      'simple-states',
      'descriptions',
      'aliases',
      'start-end',
      'composites',
      'nested-composites',
    ],
    variants: [
      {
        slug: 'document',
        title: 'Document lifecycle',
        scenario: 'A nested review lifecycle returns drafts for revision before publication.',
        outer: 'DocumentFlow',
        inner: 'ReviewCycle',
        draft: 'Draft document',
        done: 'Published',
      },
      {
        slug: 'shipment',
        title: 'Shipment lifecycle',
        scenario: 'A nested dispatch lifecycle returns parcels for correction before delivery.',
        outer: 'ShipmentFlow',
        inner: 'DispatchCycle',
        draft: 'Prepared parcel',
        done: 'Delivered',
      },
    ],
    source: (value) => String.raw`
stateDiagram-v2
  [*] --> ${value.outer}
  state "${value.draft}" as Draft
  state "${value.done}" as Done
  state ${value.outer} {
    [*] --> Draft
    state ${value.inner} {
      Draft --> Review
      Review --> Draft : changes requested
      Review --> Accepted : approved
    }
    Accepted --> Complete
  }
  ${value.outer} --> Done : complete
  Done --> [*]
`,
    expectedTexts: (value) => [value.draft, value.done],
  }),
  ...expandTemplate({
    kind: 'state',
    id: 'decision_sync',
    layout: 'dagre',
    aspectRatio: 1.35,
    features: ['choice', 'fork-join', 'concurrency'],
    variants: [
      {
        slug: 'deployment',
        title: 'Deployment decision and synchronization',
        scenario: 'A deployment choice fans out validation before synchronized rollout.',
        begin: 'CandidateReady',
        left: 'RegionValidation',
        right: 'ClientValidation',
        done: 'RolloutComplete',
      },
      {
        slug: 'migration',
        title: 'Migration decision and synchronization',
        scenario: 'A migration choice fans out checks before synchronized cutover.',
        begin: 'PlanReady',
        left: 'SchemaValidation',
        right: 'TrafficValidation',
        done: 'CutoverComplete',
      },
    ],
    source: (value) => String.raw`
stateDiagram-v2
  state decision <<choice>>
  state split <<fork>>
  state merge <<join>>
  [*] --> ${value.begin}
  ${value.begin} --> decision
  decision --> split : proceed
  decision --> Rework : revise
  split --> ${value.left}
  split --> ${value.right}
  ${value.left} --> merge
  ${value.right} --> merge
  merge --> ${value.done}
  ${value.done} --> [*]
  Rework --> ${value.begin}
`,
    expectedTexts: (value) => [value.begin, value.done],
  }),
  ...expandTemplate({
    kind: 'state',
    id: 'notes_styles',
    layout: 'dagre',
    aspectRatio: 1.35,
    features: ['notes', 'classes-styles', 'links', 'direction'],
    variants: [
      {
        slug: 'approval',
        title: 'Approval state annotations',
        scenario: 'Directional states use notes, classes, and a sanitized link.',
        pending: 'PendingApproval',
        approved: 'Approved',
        note: 'Requires two reviewers',
      },
      {
        slug: 'retention',
        title: 'Retention state annotations',
        scenario: 'Directional states use notes, classes, and a sanitized link.',
        pending: 'PendingDeletion',
        approved: 'Deleted',
        note: 'Honors legal hold',
      },
    ],
    source: (value) => String.raw`
stateDiagram-v2
  direction LR
  [*] --> ${value.pending}
  note right of ${value.pending}
    ${value.note}
  end note
  ${value.pending} --> ${value.approved}
  ${value.approved} --> [*]
  classDef guarded fill:#fef3c7,stroke:#b45309,color:#451a03
  class ${value.pending} guarded
  click ${value.approved} href "https://example.com/status"
`,
    expectedTexts: (value) => [value.pending, value.note],
  }),
  ...expandTemplate({
    kind: 'state',
    id: 'markdown_metadata',
    layout: 'dagre',
    aspectRatio: 1.35,
    features: ['markdown', 'metadata', 'unicode'],
    variants: [
      {
        slug: 'localization',
        title: 'Localization workflow metadata',
        scenario: 'Unicode and Markdown state labels retain accessibility metadata.',
        titleText: 'Localization workflow',
        first: '翻訳待ち',
        second: 'Published',
      },
      {
        slug: 'compliance',
        title: 'Compliance workflow metadata',
        scenario: 'Unicode and Markdown state labels retain accessibility metadata.',
        titleText: 'Compliance workflow',
        first: 'Révision',
        second: 'Certified',
      },
    ],
    source: (value) => String.raw`
---
title: ${value.titleText}
accTitle: ${value.titleText}
accDescr: State transition evidence
---
stateDiagram-v2
  state "\`**${value.first}**\`" as First
  state "${value.second}" as Second
  [*] --> First
  First --> Second : accepted
  Second --> [*]
`,
    expectedTexts: (value) => [value.first, value.second],
  }),
];

const erCases = [
  ...expandTemplate({
    kind: 'er',
    id: 'attribute_model',
    layout: 'dagre',
    aspectRatio: 1.45,
    features: [
      'entities',
      'aliases',
      'attributes',
      'optional-types',
      'keys',
      'comments',
      'identifying',
    ],
    variants: [
      {
        slug: 'support',
        title: 'Support data model',
        scenario: 'Tickets and messages exercise aliases, keys, comments, and optional types.',
        parent: 'TICKET',
        child: 'MESSAGE',
        parentAlias: 'Support ticket',
        childAlias: 'Conversation message',
      },
      {
        slug: 'catalog',
        title: 'Catalog data model',
        scenario: 'Products and variants exercise aliases, keys, comments, and optional types.',
        parent: 'PRODUCT',
        child: 'VARIANT',
        parentAlias: 'Catalog product',
        childAlias: 'Product variant',
      },
    ],
    source: (value) => String.raw`
erDiagram
  ${value.parent}["${value.parentAlias}"] {
    uuid id PK "stable identifier"
    string tenant_id FK
    string? description "optional detail"
    datetime created_at
  }
  ${value.child}["${value.childAlias}"] {
    uuid id PK
    uuid parent_id FK
    string body
  }
  ${value.parent} ||--|{ ${value.child} : contains
`,
    expectedTexts: (value) => [value.parentAlias, value.childAlias],
  }),
  ...expandTemplate({
    kind: 'er',
    id: 'cardinality_matrix',
    layout: 'dagre',
    aspectRatio: 1.55,
    features: ['cardinalities', 'non-identifying'],
    variants: [
      {
        slug: 'tenant',
        title: 'Tenant relationship cardinalities',
        scenario: 'Tenant resources cover optional, singular, and plural cardinalities.',
        prefix: 'TENANT',
      },
      {
        slug: 'workspace',
        title: 'Workspace relationship cardinalities',
        scenario: 'Workspace resources cover optional, singular, and plural cardinalities.',
        prefix: 'WORKSPACE',
      },
    ],
    source: (value) => String.raw`
erDiagram
  ${value.prefix} ||..o{ MEMBER : invites
  ${value.prefix} ||..|{ PROJECT : owns
  ${value.prefix} |o..o| BILLING_PROFILE : configures
  PROJECT }o..o{ LABEL : tagged_with
  MEMBER }|..|| PROFILE : has
`,
    expectedTexts: (value) => [value.prefix, 'PROJECT'],
  }),
  ...expandTemplate({
    kind: 'er',
    id: 'subgraph_domains',
    layout: 'dagre',
    aspectRatio: 1.55,
    features: ['direction', 'subgraphs', 'nested-subgraphs', 'unicode'],
    variants: [
      {
        slug: 'health',
        title: 'Health data domains',
        scenario: 'Nested clinical domains preserve direction and Unicode aliases.',
        outer: 'HEALTH_PLATFORM',
        inner: 'CLINICAL',
        first: 'PATIENT',
        second: '診療記録',
      },
      {
        slug: 'travel',
        title: 'Travel data domains',
        scenario: 'Nested booking domains preserve direction and Unicode aliases.',
        outer: 'TRAVEL_PLATFORM',
        inner: 'BOOKING',
        first: 'TRAVELER',
        second: 'Réservation',
      },
    ],
    source: (value) => String.raw`
erDiagram
  direction LR
  subgraph ${value.outer}
    direction TB
    subgraph ${value.inner}
      ${value.first}
      RECORD["${value.second}"]
    end
    AUDIT_EVENT
  end
  ${value.first} ||--o{ RECORD : owns
  RECORD ||--o{ AUDIT_EVENT : emits
`,
    expectedTexts: (value) => [value.first, value.second],
  }),
  ...expandTemplate({
    kind: 'er',
    id: 'styled_metadata',
    layout: 'dagre',
    aspectRatio: 1.5,
    features: ['classes-styles', 'layout-config', 'metadata'],
    variants: [
      {
        slug: 'governance',
        title: 'Governance metadata model',
        scenario: 'Styled governance entities use frontmatter layout and accessibility metadata.',
        titleText: 'Governance catalog',
        root: 'DATA_ASSET',
        child: 'POLICY_BINDING',
      },
      {
        slug: 'feature_flags',
        title: 'Feature flag metadata model',
        scenario: 'Styled feature entities use frontmatter layout and accessibility metadata.',
        titleText: 'Feature flag catalog',
        root: 'FEATURE_FLAG',
        child: 'ROLLOUT_RULE',
      },
    ],
    source: (value) => String.raw`
---
title: ${value.titleText}
accTitle: ${value.titleText}
accDescr: Entity relationship evidence
config:
  layout: dagre
---
erDiagram
  ${value.root} ||--o{ ${value.child} : governs
  classDef governed fill:#dbeafe,stroke:#1d4ed8,color:#172554
  class ${value.root},${value.child} governed
`,
    expectedTexts: (value) => [value.root, value.child],
  }),
];

const ganttCases = [
  ...expandTemplate({
    kind: 'gantt',
    id: 'dependency_plan',
    layout: 'dagre',
    aspectRatio: 1.75,
    features: [
      'date-formats',
      'duration-units',
      'dependencies',
      'task-states',
      'sections',
    ],
    variants: [
      {
        slug: 'key_rotation',
        title: 'Key rotation rollout',
        scenario: 'Security key rotation includes active, done, critical, and dependent tasks.',
        diagramTitle: 'Key rotation rollout',
        sectionA: 'Preparation',
        sectionB: 'Execution',
        start: '2028-01-03',
      },
      {
        slug: 'schema_upgrade',
        title: 'Schema upgrade rollout',
        scenario: 'Schema upgrade includes active, done, critical, and dependent tasks.',
        diagramTitle: 'Schema upgrade rollout',
        sectionA: 'Preparation',
        sectionB: 'Migration',
        start: '2028-03-06',
      },
    ],
    source: (value) => String.raw`
gantt
  title ${value.diagramTitle}
  dateFormat YYYY-MM-DD
  axisFormat %b %d
  todayMarker off
  section ${value.sectionA}
  Inventory :done, inventory, ${value.start}, 3d
  Dry run :active, dryrun, after inventory, 4d
  section ${value.sectionB}
  Primary change :crit, change, after dryrun, 2d
  Validation :validate, after change, 36h
  Cleanup :after validate, 2d
`,
    expectedTexts: (value) => [value.diagramTitle, 'Primary change'],
  }),
  ...expandTemplate({
    kind: 'gantt',
    id: 'calendar_controls',
    layout: 'dagre',
    aspectRatio: 1.75,
    features: ['milestones', 'excludes', 'weekends', 'axis-format'],
    variants: [
      {
        slug: 'store_launch',
        title: 'Store launch calendar',
        scenario: 'A launch calendar excludes weekends and marks approval milestones.',
        diagramTitle: 'Store launch calendar',
        start: '2028-05-01',
        milestone: 'Store approval',
      },
      {
        slug: 'regional_cutover',
        title: 'Regional cutover calendar',
        scenario: 'A cutover calendar excludes weekends and marks readiness milestones.',
        diagramTitle: 'Regional cutover calendar',
        start: '2028-07-03',
        milestone: 'Region ready',
      },
    ],
    source: (value) => String.raw`
gantt
  title ${value.diagramTitle}
  dateFormat YYYY-MM-DD
  axisFormat %a %d
  todayMarker off
  excludes weekends
  weekend saturday
  section Delivery
  Build candidate :build, ${value.start}, 5d
  Verification :verify, after build, 4d
  ${value.milestone} :milestone, ready, after verify, 0d
  Staged rollout :after ready, 5d
`,
    expectedTexts: (value) => [value.diagramTitle, value.milestone],
  }),
  ...expandTemplate({
    kind: 'gantt',
    id: 'axis_controls',
    layout: 'dagre',
    aspectRatio: 1.75,
    features: ['tick-interval', 'top-axis', 'vertical-markers'],
    variants: [
      {
        slug: 'capacity',
        title: 'Capacity review timeline',
        scenario: 'A long-range timeline uses two-week ticks, top axis, and vertical markers.',
        diagramTitle: 'Capacity review timeline',
        start: '2028-09-01',
        marker: '2028-10-02',
      },
      {
        slug: 'retention',
        title: 'Retention review timeline',
        scenario: 'A long-range timeline uses two-week ticks, top axis, and vertical markers.',
        diagramTitle: 'Retention review timeline',
        start: '2029-01-08',
        marker: '2029-02-05',
      },
    ],
    source: (value) => String.raw`
---
config:
  gantt:
    topAxis: true
---
gantt
  title ${value.diagramTitle}
  dateFormat YYYY-MM-DD
  axisFormat %b %d
  tickInterval 2week
  todayMarker off
  section Review
  Baseline :base, ${value.start}, 14d
  Experiment :experiment, after base, 21d
  Review checkpoint :vert, checkpoint, ${value.marker}, 1d
  Decision :milestone, decision, after experiment, 0d
`,
    expectedTexts: (value) => [value.diagramTitle, 'Experiment'],
  }),
  ...expandTemplate({
    kind: 'gantt',
    id: 'compact_links',
    layout: 'dagre',
    aspectRatio: 1.75,
    features: ['compact-mode', 'links', 'frontmatter-config', 'unicode'],
    variants: [
      {
        slug: 'localization',
        title: 'Localization compact plan',
        scenario: 'A compact plan retains a sanitized task link and Unicode labels.',
        diagramTitle: 'Localization delivery',
        start: '2029-04-02',
        localized: '翻訳 validation',
      },
      {
        slug: 'certification',
        title: 'Certification compact plan',
        scenario: 'A compact plan retains a sanitized task link and Unicode labels.',
        diagramTitle: 'Certification delivery',
        start: '2029-06-04',
        localized: 'Révision evidence',
      },
    ],
    source: (value) => String.raw`
---
config:
  gantt:
    displayMode: compact
---
gantt
  title ${value.diagramTitle}
  dateFormat YYYY-MM-DD
  todayMarker off
  section Evidence
  Collect inputs :collect, ${value.start}, 5d
  ${value.localized} :review, after collect, 5d
  Publish record :publish, after review, 2d
  click publish href "https://example.com/evidence"
`,
    expectedTexts: (value) => [value.diagramTitle, value.localized],
  }),
];

const pieCases = [
  ...expandTemplate({
    kind: 'pie',
    id: 'distribution',
    layout: 'dagre',
    aspectRatio: 1.15,
    features: ['basic-slices', 'show-data', 'title', 'decimal-values'],
    variants: [
      {
        slug: 'compute',
        title: 'Compute cost distribution',
        scenario: 'Decimal cost shares render with data labels and a legend.',
        diagramTitle: 'Compute cost distribution',
        values: [['On-demand', 42.5], ['Reserved', 31.25], ['Spot', 18.75], ['Other', 7.5]],
      },
      {
        slug: 'traffic',
        title: 'Traffic distribution',
        scenario: 'Decimal traffic shares render with data labels and a legend.',
        diagramTitle: 'Traffic distribution',
        values: [['Direct', 38.4], ['Search', 27.6], ['Referral', 21.2], ['Other', 12.8]],
      },
    ],
    source: (value) => pieSource(value, 'showData'),
    expectedTexts: (value) => [value.diagramTitle, value.values[0][0]],
  }),
  ...expandTemplate({
    kind: 'pie',
    id: 'donut_center',
    layout: 'dagre',
    aspectRatio: 1.15,
    features: ['donut', 'legend-center', 'static-highlight'],
    variants: [
      {
        slug: 'subscription',
        title: 'Subscription donut',
        scenario: 'A centered donut statically highlights the growth segment.',
        diagramTitle: 'Subscription mix',
        highlight: 'Growth',
        values: [['Core', 58], ['Growth', 24], ['Trial', 12], ['Paused', 6]],
      },
      {
        slug: 'storage',
        title: 'Storage donut',
        scenario: 'A centered donut statically highlights the archive segment.',
        diagramTitle: 'Storage mix',
        highlight: 'Archive',
        values: [['Primary', 52], ['Replica', 28], ['Archive', 15], ['Free', 5]],
      },
    ],
    source: (value) => String.raw`
---
config:
  pie:
    donutHole: 0.52
    legendPosition: center
    highlightSlice: "${value.highlight}"
---
${pieSource(value)}
`,
    expectedTexts: (value) => [value.diagramTitle, value.highlight],
  }),
  ...expandTemplate({
    kind: 'pie',
    id: 'label_edges',
    layout: 'dagre',
    aspectRatio: 1.15,
    features: ['escaped-labels', 'duplicate-labels', 'zero-values', 'unicode'],
    variants: [
      {
        slug: 'incident',
        title: 'Incident label edge cases',
        scenario: 'Escaped, duplicate, zero, and Unicode labels remain deterministic.',
        diagramTitle: 'Incident labels',
        unicode: 'Réseau',
      },
      {
        slug: 'content',
        title: 'Content label edge cases',
        scenario: 'Escaped, duplicate, zero, and Unicode labels remain deterministic.',
        diagramTitle: 'Content labels',
        unicode: '動画',
      },
    ],
    source: (value) => String.raw`
pie showData
  title ${value.diagramTitle}
  "Quoted \"label\"" : 12
  "Duplicate" : 18
  "Duplicate" : 30
  "Zero" : 0
  "${value.unicode}" : 40
`,
    expectedTexts: (value) => [value.diagramTitle, value.unicode],
  }),
  ...expandTemplate({
    kind: 'pie',
    id: 'themed_many',
    layout: 'dagre',
    aspectRatio: 1.15,
    features: [
      'legend-right',
      'legend-transforms',
      'theme-colors',
      'many-slices',
      'metadata',
    ],
    variants: [
      {
        slug: 'regions',
        title: 'Regional traffic theme',
        scenario: 'A themed chart with many slices exercises legend transforms.',
        diagramTitle: 'Regional traffic',
      },
      {
        slug: 'services',
        title: 'Service ownership theme',
        scenario: 'A themed chart with many slices exercises legend transforms.',
        diagramTitle: 'Service ownership',
      },
    ],
    source: (value) => String.raw`
---
title: ${value.diagramTitle}
accTitle: ${value.diagramTitle}
accDescr: Distribution with many categories
config:
  pie:
    legendPosition: right
    legendTransform: "translate(8, 4)"
  themeVariables:
    pie1: "#2563eb"
    pie2: "#14b8a6"
    pie3: "#f97316"
---
pie
  title ${value.diagramTitle}
  "A" : 20
  "B" : 18
  "C" : 16
  "D" : 14
  "E" : 12
  "F" : 10
  "G" : 6
  "H" : 4
`,
    expectedTexts: (value) => [value.diagramTitle, 'H'],
  }),
];

const journeyCases = [
  ...expandTemplate({
    kind: 'journey',
    id: 'multi_stage',
    layout: 'dagre',
    aspectRatio: 2.05,
    features: [
      'sections',
      'scores',
      'single-actor',
      'multi-actor',
      'actor-reuse',
      'title',
    ],
    variants: [
      {
        slug: 'support',
        title: 'Customer support journey',
        scenario: 'A customer and support team move from problem reporting to resolution.',
        diagramTitle: 'Customer support resolution',
        actor: 'Customer',
        collaborator: 'Support Specialist',
        sections: [
          ['Report', [['Describe the problem', 3], ['Attach evidence', 2]]],
          ['Investigate', [['Reproduce the issue', 3], ['Explain the cause', 4]]],
          ['Resolve', [['Verify the fix', 5], ['Confirm closure', 5]]],
        ],
      },
      {
        slug: 'procurement',
        title: 'Procurement approval journey',
        scenario: 'A requester and procurement reviewer coordinate a purchase approval.',
        diagramTitle: 'Procurement approval',
        actor: 'Requester',
        collaborator: 'Procurement Reviewer',
        sections: [
          ['Prepare', [['Define requirements', 4], ['Collect quotations', 3]]],
          ['Review', [['Evaluate suppliers', 2], ['Approve budget', 3]]],
          ['Complete', [['Issue purchase order', 4], ['Confirm delivery', 5]]],
        ],
      },
    ],
    source: (value) => String.raw`
journey
  title ${value.diagramTitle}
${value.sections.map(([section, tasks], sectionIndex) => (
    `  section ${section}\n${tasks.map(([task, score], taskIndex) => {
      const actors = taskIndex === 0 && sectionIndex !== 0
        ? `${value.actor}, ${value.collaborator}`
        : value.actor;
      return `    ${task}: ${score}: ${actors}`;
    }).join('\n')}`
  )).join('\n')}
`,
    expectedTexts: (value) => [value.diagramTitle, value.sections.at(-1)[1].at(-1)[0]],
  }),
  ...expandTemplate({
    kind: 'journey',
    id: 'metadata_edges',
    layout: 'dagre',
    aspectRatio: 1.85,
    features: [
      'decimal-scores',
      'actorless-tasks',
      'frontmatter-title',
      'accessibility',
      'comments',
    ],
    variants: [
      {
        slug: 'automation',
        title: 'Automation metadata journey',
        scenario: 'Automated and human tasks retain metadata, comments, and decimal scores.',
        diagramTitle: 'Automation evidence journey',
        accessibilityTitle: 'Automation workflow satisfaction',
        actor: 'Operator',
        first: 'Collect scheduled inputs',
        second: 'Review generated evidence',
        third: 'Approve automated outcome',
      },
      {
        slug: 'recovery',
        title: 'Recovery metadata journey',
        scenario: 'Recovery tasks retain metadata, comments, and decimal scores.',
        diagramTitle: 'Recovery evidence journey',
        accessibilityTitle: 'Recovery workflow satisfaction',
        actor: 'Reliability Engineer',
        first: 'Detect failed workload',
        second: 'Review recovery evidence',
        third: 'Confirm restored service',
      },
    ],
    source: (value) => String.raw`
---
title: ${value.diagramTitle}
---
journey
  accTitle: ${value.accessibilityTitle}
  accDescr: Journey with automatic and operator-owned stages
  %% Automatic tasks intentionally omit an actor.
  section Observe
    ${value.first}: 2.5
  section Decide
    ${value.second}: 3.5: ${value.actor}
    %% The final score exercises a fractional face position.
    ${value.third}: 4.5: ${value.actor}
`,
    expectedTexts: (value) => [value.diagramTitle, value.third],
  }),
  ...expandTemplate({
    kind: 'journey',
    id: 'configured_text',
    layout: 'dagre',
    aspectRatio: 2.0,
    features: ['configuration', 'long-task-text', 'long-actor-text'],
    variants: [
      {
        slug: 'compliance',
        title: 'Configured compliance journey',
        scenario: 'Long compliance labels exercise measured actor width and configured task boxes.',
        diagramTitle: 'Compliance evidence review',
        actor: 'Regional Compliance Review Coordination Team',
        tasks: [
          'Inspect policy evidence for every active deployment region',
          'Resolve missing attestations with the responsible service owner',
          'Publish the approved compliance decision record',
        ],
      },
      {
        slug: 'continuity',
        title: 'Configured continuity journey',
        scenario: 'Long continuity labels exercise measured actor width and configured task boxes.',
        diagramTitle: 'Business continuity validation',
        actor: 'Global Business Continuity Validation Team',
        tasks: [
          'Collect recovery objectives from every critical product area',
          'Validate regional failover evidence against agreed objectives',
          'Approve the consolidated continuity readiness report',
        ],
      },
    ],
    source: (value) => String.raw`
---
config:
  journey:
    leftMargin: 170
    maxLabelWidth: 210
    width: 190
    height: 62
    taskMargin: 65
    taskFontSize: 13
---
journey
  title ${value.diagramTitle}
  section Evidence
    ${value.tasks[0]}: 3: ${value.actor}
    ${value.tasks[1]}: 2: ${value.actor}
  section Decision
    ${value.tasks[2]}: 5: ${value.actor}
`,
    expectedTexts: (value) => [value.diagramTitle, value.tasks[2]],
  }),
  ...expandTemplate({
    kind: 'journey',
    id: 'theme_actor_order',
    layout: 'dagre',
    aspectRatio: 1.95,
    features: ['theme-colors', 'actor-order', 'multi-actor'],
    variants: [
      {
        slug: 'release',
        title: 'Themed release actor order',
        scenario: 'A custom section palette retains Mermaid actor sorting and task marker order.',
        diagramTitle: 'Themed release coordination',
        sectionA: 'Prepare',
        sectionB: 'Release',
        taskA: 'Review candidate',
        taskB: 'Approve rollout',
        actorsA: 'Web Owner, Android Owner, Desktop Owner',
        actorsB: 'Quality Lead, Android Owner, Web Owner',
      },
      {
        slug: 'migration',
        title: 'Themed migration actor order',
        scenario: 'A custom section palette retains Mermaid actor sorting and task marker order.',
        diagramTitle: 'Themed migration coordination',
        sectionA: 'Inventory',
        sectionB: 'Migrate',
        taskA: 'Confirm dependencies',
        taskB: 'Approve cutover',
        actorsA: 'Storage Owner, API Owner, Client Owner',
        actorsB: 'Reliability Lead, API Owner, Storage Owner',
      },
    ],
    source: (value) => String.raw`
---
config:
  themeVariables:
    fillType0: "#e0f2fe"
    fillType1: "#dcfce7"
    textColor: "#1f2937"
---
journey
  title ${value.diagramTitle}
  section ${value.sectionA}
    ${value.taskA}: 3: ${value.actorsA}
  section ${value.sectionB}
    ${value.taskB}: 5: ${value.actorsB}
`,
    expectedTexts: (value) => [value.diagramTitle, value.taskB],
  }),
];

const requirementCases = [
  ...expandTemplate({
    kind: 'requirement',
    id: 'typed_fields',
    layout: 'dagre',
    aspectRatio: 1.7,
    features: [
      'requirement-types',
      'fields',
      'risk-levels',
      'verification-methods',
      'elements',
      'directions',
      'dagre-layout',
    ],
    variants: [
      {
        slug: 'device',
        title: 'Device requirement type matrix',
        scenario: 'A device model exercises every requirement type and all typed fields.',
        direction: 'LR',
        prefix: 'DEV',
        rootText: 'Operate safely in every supported mode',
        functionText: 'Stop output after a detected control fault',
        interfaceText: 'Publish a fault notification to the operator console',
        performanceText: 'Complete the shutdown within the approved interval',
        physicalText: 'Keep the enclosure below the thermal limit',
        constraintText: 'Use redundant temperature sensors',
        implementationType: 'Safety controller',
        evidenceType: 'Independent validation laboratory',
      },
      {
        slug: 'checkout',
        title: 'Checkout requirement type matrix',
        scenario: 'A checkout model exercises every requirement type and all typed fields.',
        direction: 'TB',
        prefix: 'PAY',
        rootText: 'Complete checkout without duplicate orders',
        functionText: 'Create at most one order for each payment attempt',
        interfaceText: 'Preserve the external payment contract',
        performanceText: 'Return authorization within the latency budget',
        physicalText: 'Protect payment keys in approved hardware',
        constraintText: 'Use idempotent command identifiers',
        implementationType: 'Checkout service',
        evidenceType: 'Payment contract suite',
      },
    ],
    source: (value) => String.raw`
requirementDiagram
  direction ${value.direction}
  requirement system_goal {
    id: "${value.prefix}-SYS-1"
    text: "${value.rootText}"
    risk: high
    verifyMethod: demonstration
  }
  functionalRequirement function_goal {
    id: "${value.prefix}-FN-2"
    text: "${value.functionText}"
    risk: high
    verifyMethod: test
  }
  interfaceRequirement interface_goal {
    id: "${value.prefix}-IF-3"
    text: "${value.interfaceText}"
    risk: medium
    verifyMethod: inspection
  }
  performanceRequirement performance_goal {
    id: "${value.prefix}-PF-4"
    text: "${value.performanceText}"
    risk: medium
    verifyMethod: analysis
  }
  physicalRequirement physical_goal {
    id: "${value.prefix}-PH-5"
    text: "${value.physicalText}"
    risk: low
    verifyMethod: test
  }
  designConstraint implementation_constraint {
    id: "${value.prefix}-DC-6"
    text: "${value.constraintText}"
    risk: low
    verifyMethod: inspection
  }
  element implementation {
    type: "${value.implementationType}"
    docRef: "architecture/${value.prefix.toLowerCase()}-implementation"
  }
  element verification_evidence {
    type: "${value.evidenceType}"
    docRef: "evidence/${value.prefix.toLowerCase()}-validation"
  }
  system_goal - contains -> function_goal
  system_goal - contains -> interface_goal
  function_goal - derives -> performance_goal
  implementation - satisfies -> implementation_constraint
  verification_evidence - verifies -> physical_goal
`,
    expectedTexts: (value) => [value.rootText, value.evidenceType],
  }),
  ...expandTemplate({
    kind: 'requirement',
    id: 'relation_matrix',
    layout: 'dagre',
    aspectRatio: 1.65,
    features: [
      'elements',
      'relationship-types',
      'reverse-relationships',
      'directions',
      'dagre-layout',
    ],
    variants: [
      {
        slug: 'replication',
        title: 'Replication relationship matrix',
        scenario: 'Replication requirements exercise every relationship and reverse syntax.',
        direction: 'BT',
        rootText: 'Maintain a recoverable replica',
        childText: 'Persist every accepted change',
        copyText: 'Mirror the recovery policy',
        derivedText: 'Calculate a replication checkpoint',
        refinedText: 'Define the recovery-point objective',
        tracedText: 'Record the originating change request',
        implementationType: 'Replication worker',
        evidenceType: 'Recovery validation suite',
      },
      {
        slug: 'delivery',
        title: 'Delivery relationship matrix',
        scenario: 'Delivery requirements exercise every relationship and reverse syntax.',
        direction: 'RL',
        rootText: 'Deliver each accepted message',
        childText: 'Persist delivery state',
        copyText: 'Mirror the routing policy',
        derivedText: 'Calculate the retry schedule',
        refinedText: 'Define the acknowledgement contract',
        tracedText: 'Record the originating delivery request',
        implementationType: 'Delivery worker',
        evidenceType: 'Delivery verification suite',
      },
    ],
    source: (value) => String.raw`
requirementDiagram
  direction ${value.direction}
  requirement root_goal {
    text: "${value.rootText}"
  }
  functionalRequirement child_goal {
    text: "${value.childText}"
  }
  requirement copy_goal {
    text: "${value.copyText}"
  }
  performanceRequirement derived_goal {
    text: "${value.derivedText}"
  }
  interfaceRequirement refined_goal {
    text: "${value.refinedText}"
  }
  requirement traced_goal {
    text: "${value.tracedText}"
  }
  element implementation {
    type: "${value.implementationType}"
  }
  element verification_evidence {
    type: "${value.evidenceType}"
  }
  root_goal - contains -> child_goal
  copy_goal <- copies - root_goal
  root_goal - derives -> derived_goal
  implementation - satisfies -> root_goal
  verification_evidence - verifies -> child_goal
  refined_goal <- refines - root_goal
  root_goal - traces -> traced_goal
`,
    expectedTexts: (value) => [value.rootText, value.evidenceType],
  }),
  ...expandTemplate({
    kind: 'requirement',
    id: 'metadata_layout',
    layout: 'dagre',
    aspectRatio: 1.55,
    features: [
      'directions',
      'dagre-layout',
      'frontmatter-title',
      'accessibility',
      'markdown',
      'unicode',
      'comments',
    ],
    variants: [
      {
        slug: 'authorization',
        title: 'Authorization requirement metadata',
        scenario: 'Authorization requirements preserve titles, accessibility, Markdown, and Unicode.',
        diagramTitle: 'Authorization requirements',
        accessibilityTitle: 'Authorization requirement trace',
        accessibilityDescription: 'Authorization controls and their verification evidence',
        direction: 'LR',
        primaryText: 'Authorize every protected request',
        unicodeText: 'Préserver la décision de sécurité',
        actorType: 'Policy enforcement point',
      },
      {
        slug: 'continuity',
        title: 'Continuity requirement metadata',
        scenario: 'Continuity requirements preserve titles, accessibility, Markdown, and Unicode.',
        diagramTitle: 'Continuity requirements',
        accessibilityTitle: 'Continuity requirement trace',
        accessibilityDescription: 'Recovery controls and their verification evidence',
        direction: 'TB',
        primaryText: 'Restore every critical service',
        unicodeText: 'Vérifier la reprise régionale',
        actorType: 'Regional recovery coordinator',
      },
    ],
    source: (value) => String.raw`
---
title: ${value.diagramTitle}
config:
  layout: dagre
  theme: default
  look: classic
---
requirementDiagram
  accTitle: ${value.accessibilityTitle}
  accDescr {
    ${value.accessibilityDescription}.
  }
  direction ${value.direction}
  %% Markdown and Unicode are intentionally combined in requirement text.
  requirement primary_requirement {
    id: "META-1"
    text: "**${value.primaryText}**"
    risk: high
    verifyMethod: inspection
  }
  functionalRequirement localized_requirement {
    id: "META-2"
    text: "*${value.unicodeText}*"
    risk: medium
    verifyMethod: demonstration
  }
  element responsible_component {
    type: "${value.actorType}"
    docRef: "architecture/metadata-owner"
  }
  primary_requirement - contains -> localized_requirement
  responsible_component - satisfies -> primary_requirement
`,
    expectedTexts: (value) => [value.diagramTitle, value.primaryText, value.unicodeText],
  }),
  ...expandTemplate({
    kind: 'requirement',
    id: 'styled_theme',
    layout: 'dagre',
    aspectRatio: 1.5,
    features: [
      'directions',
      'dagre-layout',
      'direct-styles',
      'classes',
      'theme-variables',
    ],
    variants: [
      {
        slug: 'privacy',
        title: 'Styled privacy requirements',
        scenario: 'Privacy controls combine class assignment, direct styles, and theme variables.',
        direction: 'RL',
        primaryText: 'Limit access to approved identities',
        secondaryText: 'Record every policy decision',
        componentType: 'Privacy gateway',
      },
      {
        slug: 'resilience',
        title: 'Styled resilience requirements',
        scenario: 'Resilience controls combine class assignment, direct styles, and theme variables.',
        direction: 'BT',
        primaryText: 'Continue service during a regional outage',
        secondaryText: 'Record every failover decision',
        componentType: 'Traffic controller',
      },
    ],
    source: (value) => String.raw`
---
config:
  themeVariables:
    requirementBackground: "#ecfeff"
    requirementBorderColor: "#0e7490"
    requirementTextColor: "#164e63"
    relationColor: "#475569"
    requirementEdgeLabelBackground: "#fff7ed"
---
requirementDiagram
  direction ${value.direction}
  requirement primary_requirement:::critical {
    id: "STYLE-1"
    text: "${value.primaryText}"
    risk: high
    verifyMethod: test
  }
  functionalRequirement secondary_requirement {
    id: "STYLE-2"
    text: "${value.secondaryText}"
    risk: medium
    verifyMethod: analysis
  }
  element implementation {
    type: "${value.componentType}"
    docRef: "architecture/styled-component"
  }
  classDef critical fill:#fee2e2,stroke:#b91c1c,color:#7f1d1d,stroke-width:3px
  class implementation critical
  style secondary_requirement fill:#dcfce7,stroke:#15803d,color:#14532d
  primary_requirement - contains -> secondary_requirement
  implementation - satisfies -> primary_requirement
`,
    expectedTexts: (value) => [value.primaryText, value.componentType],
  }),
];

const gitGraphCases = [
  ...expandTemplate({
    kind: 'gitgraph',
    id: 'commit_metadata',
    layout: 'dagre',
    aspectRatio: 1.8,
    features: [
      'commits',
      'custom-ids',
      'messages',
      'tags',
      'commit-types',
      'frontmatter-config',
      'title',
      'accessibility',
      'unicode',
      'comments',
    ],
    variants: [
      {
        slug: 'release',
        title: 'Release commit metadata',
        scenario: 'A release history combines custom IDs, messages, tags, symbols, and metadata.',
        diagramTitle: 'Release commit history',
        accessibilityTitle: 'Release commits',
        first: 'plan',
        second: 'rollback',
        third: '发布',
        tag: 'v4.0.0',
      },
      {
        slug: 'migration',
        title: 'Migration commit metadata',
        scenario: 'A migration history combines custom IDs, messages, tags, symbols, and metadata.',
        diagramTitle: 'Migration commit history',
        accessibilityTitle: 'Migration commits',
        first: 'inventory',
        second: 'restore-point',
        third: '切换',
        tag: 'migration-ready',
      },
    ],
    source: (value) => String.raw`
---
title: ${value.diagramTitle}
---
gitGraph
  accTitle: ${value.accessibilityTitle}
  accDescr {
    Commit identifiers and release markers describe the verified delivery history.
  }
  %% Every public commit attribute is represented.
  commit id: "${value.first}" msg: "Create the delivery plan"
  commit id: "${value.second}" msg: "Record a safe rollback point" type: REVERSE
  commit id: "${value.third}" msg: "Publish the verified result" type: HIGHLIGHT tag: "${value.tag}"
`,
    expectedTexts: (value) => [value.diagramTitle, value.third, value.tag],
  }),
  ...expandTemplate({
    kind: 'gitgraph',
    id: 'branch_merge',
    layout: 'dagre',
    aspectRatio: 1.75,
    features: [
      'branches',
      'quoted-branches',
      'checkout',
      'switch',
      'branch-order',
      'main-branch-config',
      'merges',
      'merge-customization',
    ],
    variants: [
      {
        slug: 'delivery',
        title: 'Ordered delivery branches',
        scenario: 'Delivery and hotfix lanes use explicit order, both switching commands, and customized merges.',
        main: 'trunk',
        feature: 'delivery lane',
        hotfix: 'urgent-fix',
        merge: 'accept-delivery',
        tag: 'reviewed',
      },
      {
        slug: 'platform',
        title: 'Ordered platform branches',
        scenario: 'Platform and recovery lanes use explicit order, both switching commands, and customized merges.',
        main: 'stable',
        feature: 'platform lane',
        hotfix: 'recovery-fix',
        merge: 'accept-platform',
        tag: 'approved',
      },
    ],
    source: (value) => String.raw`
---
config:
  gitGraph:
    mainBranchName: "${value.main}"
    mainBranchOrder: 2
---
gitGraph LR:
  commit id: "root"
  branch "${value.feature}" order: 1
  commit id: "feature-1"
  switch ${value.main}
  branch ${value.hotfix} order: 3
  commit id: "fix-1"
  checkout "${value.feature}"
  merge ${value.hotfix} id: "${value.merge}" tag: "${value.tag}" type: REVERSE
  switch ${value.main}
  merge "${value.feature}" id: "publish"
`,
    expectedTexts: (value) => [value.main, value.feature, value.merge, value.tag],
  }),
  ...expandTemplate({
    kind: 'gitgraph',
    id: 'cherry_orientation',
    layout: 'dagre',
    aspectRatio: 1.45,
    features: [
      'branches',
      'checkout',
      'merges',
      'cherry-pick',
      'merge-cherry-pick',
      'orientations',
      'parallel-commits',
    ],
    variants: [
      {
        slug: 'top_to_bottom',
        title: 'Top-to-bottom cherry-pick history',
        scenario: 'Top-to-bottom parallel ranks include normal and merge cherry-picks.',
        direction: 'TB',
        root: 'tb-root',
        source: 'tb-change',
        merge: 'tb-merge',
      },
      {
        slug: 'bottom_to_top',
        title: 'Bottom-to-top cherry-pick history',
        scenario: 'Bottom-to-top parallel ranks include normal and merge cherry-picks.',
        direction: 'BT',
        root: 'bt-root',
        source: 'bt-change',
        merge: 'bt-merge',
      },
    ],
    source: (value) => String.raw`
---
config:
  gitGraph:
    parallelCommits: true
---
gitGraph ${value.direction}:
  commit id: "${value.root}"
  branch develop
  branch release
  commit id: "release-base"
  checkout develop
  commit id: "${value.source}"
  checkout main
  commit id: "main-change"
  merge develop id: "${value.merge}"
  branch hotfix
  commit id: "independent-fix"
  checkout release
  cherry-pick id: "${value.merge}" parent: "${value.source}"
  cherry-pick id: "independent-fix"
`,
    expectedTexts: (value) => [value.root, value.merge],
  }),
  ...expandTemplate({
    kind: 'gitgraph',
    id: 'configured_visibility',
    layout: 'dagre',
    aspectRatio: 1.85,
    features: [
      'branches',
      'merges',
      'visibility-config',
      'frontmatter-config',
      'title',
      'accessibility',
      'theme-variables',
    ],
    variants: [
      {
        slug: 'hidden_branches',
        title: 'Hidden branch decorations',
        scenario: 'Branch labels and spines are hidden while commits, tags, and metadata remain visible.',
        diagramTitle: 'Hidden branch release',
        showBranches: false,
        showCommitLabel: true,
        rotateCommitLabel: true,
        tag: 'branchless',
      },
      {
        slug: 'hidden_commits',
        title: 'Hidden commit labels',
        scenario: 'Commit labels are hidden while branch lanes, tags, and metadata remain visible.',
        diagramTitle: 'Compact release history',
        showBranches: true,
        showCommitLabel: false,
        rotateCommitLabel: false,
        tag: 'compact',
      },
    ],
    source: (value) => String.raw`
---
title: ${value.diagramTitle}
config:
  theme: base
  gitGraph:
    diagramPadding: 16
    showBranches: ${value.showBranches}
    showCommitLabel: ${value.showCommitLabel}
    rotateCommitLabel: ${value.rotateCommitLabel}
  themeVariables:
    git0: "#0f766e"
    git1: "#c2410c"
    commitLineColor: "#475569"
    tagLabelColor: "#134e4a"
    tagLabelBackground: "#ccfbf1"
    tagLabelBorder: "#0f766e"
    textColor: "#0f172a"
---
gitGraph
  accTitle: ${value.diagramTitle}
  accDescr: The configured graph keeps release metadata available.
  commit id: "baseline"
  branch verification
  commit id: "validated" type: HIGHLIGHT
  checkout main
  merge verification id: "published" tag: "${value.tag}"
`,
    expectedTexts: (value) => [value.diagramTitle, value.tag],
  }),
];

const mindmapCases = [
  ...expandTemplate({
    kind: 'mindmap',
    id: 'hierarchy',
    layout: 'cose-bilkent',
    aspectRatio: 1.6,
    features: [
      'hierarchy',
      'irregular-indentation',
      'deep-hierarchy',
      'wide-hierarchy',
      'comments',
      'cose-bilkent-layout',
      'unicode',
    ],
    variants: [
      {
        slug: 'service_ownership',
        title: 'Service ownership hierarchy',
        scenario: 'Service ownership spans client, API, data, and operations responsibilities.',
        root: 'Service ownership',
        first: 'Client experience',
        deep: 'Release verification',
        unicode: '品質確認',
      },
      {
        slug: 'research_program',
        title: 'Research program hierarchy',
        scenario: 'A research program connects questions, methods, evidence, and publication work.',
        root: 'Research program',
        first: 'Research questions',
        deep: 'Independent replication',
        unicode: '結果確認',
      },
    ],
    source: (value) => String.raw`
---
title: ${value.title}
config:
  layout: cose-bilkent
---
mindmap
  root((${value.root}))
    ${value.first}
        Discovery
              Interviews
          Synthesis
                ${value.deep}
    Delivery
      Plan
      Build
      Verify
        ${value.unicode}
    Operations
      Ownership
      Monitoring
      Improvement
    %% Comments do not create hierarchy nodes.
`,
    expectedTexts: (value) => [value.root, value.deep, value.unicode],
  }),
  ...expandTemplate({
    kind: 'mindmap',
    id: 'shape_text',
    layout: 'cose-bilkent',
    aspectRatio: 1.7,
    features: [
      'default-shape',
      'square-shape',
      'rounded-shape',
      'circle-shape',
      'cloud-shape',
      'bang-shape',
      'hexagon-shape',
      'markdown',
      'html-breaks',
      'entities',
    ],
    variants: [
      {
        slug: 'delivery',
        title: 'Delivery shape vocabulary',
        scenario: 'Delivery concepts exercise every supported shape and text representation.',
        root: 'Delivery model',
        emphasis: 'Validated',
        entity: 'Build &amp; sign',
        multiline: 'Release<br/>evidence',
      },
      {
        slug: 'operations',
        title: 'Operations shape vocabulary',
        scenario: 'Operational concepts exercise every supported shape and text representation.',
        root: 'Operations model',
        emphasis: 'Observed',
        entity: 'Alert &amp; respond',
        multiline: 'Recovery<br/>evidence',
      },
    ],
    source: (value) => String.raw`
---
config:
  layout: cose-bilkent
  mindmap:
    maxNodeWidth: 135
---
mindmap
  root((${value.root}))
    Default branch
      **${value.emphasis}**
    square[${value.entity}]
      squareChild[Square child]
    rounded(Rounded)
      wrapped(A long rounded label that wraps at the configured width)
    circle((Circle))
      circleChild((${value.multiline}))
    cloud)Cloud(
      cloudChild)Cloud child(
    bang))Bang((
      bangChild))Bang child((
    hex{{Hexagon}}
      hexChild{{Hexagon child}}
`,
    expectedTexts: (value) => [value.root, value.emphasis, 'Hexagon child'],
  }),
  ...expandTemplate({
    kind: 'mindmap',
    id: 'layout',
    layout: (value) => value.layout,
    aspectRatio: 1.55,
    features: (value) => [
      'hierarchy',
      value.layoutFeature,
    ],
    variants: [
      {
        slug: 'dagre_pipeline',
        title: 'Dagre delivery pipeline',
        scenario: 'A measured top-to-bottom hierarchy preserves Dagre ranks and routed edges.',
        layout: 'dagre',
        layoutFeature: 'dagre-layout',
        root: 'Delivery pipeline',
        left: 'Inputs',
        right: 'Outputs',
      },
      {
        slug: 'tidy_architecture',
        title: 'Tidy-tree architecture map',
        scenario: 'A bidirectional tidy tree balances architecture branches around the root.',
        layout: 'tidy-tree',
        layoutFeature: 'tidy-tree-layout',
        root: 'Architecture',
        left: 'Clients',
        right: 'Services',
      },
    ],
    source: (value) => String.raw`
---
title: ${value.title}
config:
  layout: ${value.layout}
---
mindmap
  root((${value.root}))
    ${value.left}
      Primary
        Contract
        Validation
      Secondary
        Fallback
        Recovery
    ${value.right}
      Processing
        Commands
        Queries
      Storage
        Operational
        Analytical
`,
    expectedTexts: (value) => [value.root, value.left, value.right],
  }),
  ...expandTemplate({
    kind: 'mindmap',
    id: 'configured_theme',
    layout: 'dagre',
    aspectRatio: 1.45,
    features: [
      'frontmatter-title',
      'sizing-config',
      'theme-variables',
      'dagre-layout',
      'markdown',
    ],
    variants: [
      {
        slug: 'governance',
        title: 'Configured governance map',
        scenario: 'Governance labels use explicit sizing and a restrained custom palette.',
        root: 'Governance',
        sectionA: 'Architecture review',
        sectionB: 'Release review',
        accent: '#0f766e',
      },
      {
        slug: 'readiness',
        title: 'Configured readiness map',
        scenario: 'Readiness labels use explicit sizing and a contrasting custom palette.',
        root: 'Operational readiness',
        sectionA: 'Service checks',
        sectionB: 'Recovery checks',
        accent: '#1d4ed8',
      },
    ],
    source: (value) => String.raw`
---
title: ${value.title}
config:
  layout: dagre
  mindmap:
    padding: 16
    maxNodeWidth: 125
    useMaxWidth: false
  theme: base
  themeVariables:
    mainBkg: "#f8fafc"
    nodeBorder: "#334155"
    git0: "${value.accent}"
    gitBranchLabel0: "#ffffff"
    cScale0: "#ccfbf1"
    cScale1: "#dbeafe"
    cScaleLabel0: "#134e4a"
    cScaleLabel1: "#1e3a8a"
---
mindmap
  root((**${value.root}**))
    ${value.sectionA}
      Evidence inventory
      Decision record
    ${value.sectionB}
      Automated validation
      Manual confirmation
    Follow-up
      Named owners
      Due dates
`,
    expectedTexts: (value) => [value.root, value.sectionA, value.sectionB],
  }),
];

const quadrantCases = [
  {
    id: 'prod_quadrant_release_scoring',
    kind: 'quadrant',
    title: 'Release candidate scoring',
    scenario: 'Release candidates are compared by readiness and expected customer value.',
    aspectRatio: 1,
    features: ['title', 'x-axis', 'y-axis', 'quadrant-labels', 'points'],
    expectedTexts: ['Release candidate scoring', 'Ready to ship', 'Candidate Alpha'],
    source: String.raw`
quadrantChart
  title Release candidate scoring
  x-axis Lower readiness --> Higher readiness
  y-axis Lower customer value --> Higher customer value
  quadrant-1 Ready to ship
  quadrant-2 Validate value
  quadrant-3 Defer
  quadrant-4 Finish hardening
  Candidate Alpha: [0.82, 0.88]
  Candidate Beta: [0.61, 0.74]
  Candidate Gamma: [0.44, 0.36]
`,
  },
  {
    id: 'prod_quadrant_boundary_coordinates',
    kind: 'quadrant',
    title: 'Boundary coordinate coverage',
    scenario: 'Points at every chart corner and center verify normalized coordinate projection.',
    aspectRatio: 1,
    features: ['points', 'boundary-points', 'point-radius'],
    expectedTexts: ['Origin', 'Upper right', 'Center'],
    source: String.raw`
quadrantChart
  x-axis Zero --> One
  y-axis Zero --> One
  Origin: [0, 0] radius: 7
  Upper left: [0, 1] radius: 8
  Lower right: [1, 0] radius: 9
  Upper right: [1, 1] radius: 10
  Center: [0.5, 0.5] radius: 11
`,
  },
  {
    id: 'prod_quadrant_empty_framework',
    kind: 'quadrant',
    title: 'Empty decision framework',
    scenario: 'A framework without observations centers all semantic labels.',
    aspectRatio: 1,
    features: ['title', 'x-axis', 'y-axis', 'quadrant-labels', 'empty-points'],
    expectedTexts: ['Decision framework', 'Immediate action', 'Monitor'],
    source: String.raw`
quadrantChart
  title Decision framework
  x-axis Lower urgency --> Higher urgency
  y-axis Lower impact --> Higher impact
  quadrant-1 Immediate action
  quadrant-2 Schedule
  quadrant-3 Ignore
  quadrant-4 Monitor
`,
  },
  {
    id: 'prod_quadrant_inline_styles',
    kind: 'quadrant',
    title: 'Inline observation styles',
    scenario: 'Individual observations override radius, fill, and stroke independently.',
    aspectRatio: 1,
    features: ['points', 'point-radius', 'point-color', 'point-stroke'],
    expectedTexts: ['Critical exposure', 'Watch item'],
    source: String.raw`
quadrantChart
  title Inline observation styles
  Critical exposure: [0.86, 0.91] radius: 14, color: #dc2626, stroke-color: #7f1d1d, stroke-width: 4px
  Watch item: [0.54, 0.62] radius: 9, color: #f59e0b, stroke-color: #92400e, stroke-width: 2px
  Healthy signal: [0.24, 0.18] color: #16a34a
`,
  },
  {
    id: 'prod_quadrant_class_precedence',
    kind: 'quadrant',
    title: 'Reusable point classes',
    scenario: 'Shared point classes apply first and local declarations retain precedence.',
    aspectRatio: 1,
    features: ['points', 'class-styles', 'inline-precedence', 'point-color', 'point-stroke'],
    expectedTexts: ['Shared style', 'Local override'],
    source: String.raw`
quadrantChart
  title Reusable point classes
  Shared style:::priority: [0.32, 0.72]
  Local override:::priority: [0.74, 0.81] color: #2563eb, radius: 13
  classDef priority color: #109060, radius: 10, stroke-color: #064e3b, stroke-width: 3px
`,
  },
  {
    id: 'prod_quadrant_dimension_config',
    kind: 'quadrant',
    title: 'Configured chart dimensions',
    scenario: 'Frontmatter controls chart geometry, label metrics, borders, and responsive sizing.',
    aspectRatio: 1.3,
    features: ['frontmatter-config', 'title', 'points'],
    expectedTexts: ['Configured portfolio', 'Platform renewal'],
    source: String.raw`
---
config:
  quadrantChart:
    chartWidth: 560
    chartHeight: 430
    titleFontSize: 24
    titlePadding: 14
    quadrantPadding: 9
    xAxisLabelPadding: 7
    yAxisLabelPadding: 8
    xAxisLabelFontSize: 15
    yAxisLabelFontSize: 17
    quadrantLabelFontSize: 18
    quadrantTextTopPadding: 8
    pointTextPadding: 7
    pointLabelFontSize: 13
    pointRadius: 8
    yAxisPosition: right
    quadrantInternalBorderStrokeWidth: 2
    quadrantExternalBorderStrokeWidth: 4
    useMaxWidth: false
---
quadrantChart
  title Configured portfolio
  x-axis Lower return --> Higher return
  y-axis Lower risk --> Higher risk
  Platform renewal: [0.71, 0.64]
`,
  },
  {
    id: 'prod_quadrant_theme_metadata',
    kind: 'quadrant',
    title: 'Themed accessible assessment',
    scenario: 'Theme variables and accessibility metadata travel through the production scene.',
    aspectRatio: 1,
    features: ['theme-colors', 'metadata', 'quadrant-labels', 'points'],
    expectedTexts: ['Themed assessment', 'Approve', 'Evidence review'],
    source: String.raw`
---
title: Themed assessment
config:
  themeVariables:
    quadrant1Fill: "#dcfce7"
    quadrant2Fill: "#dbeafe"
    quadrant3Fill: "#fee2e2"
    quadrant4Fill: "#fef3c7"
    quadrantPointFill: "#111827"
    quadrantPointTextFill: "#111827"
    quadrantExternalBorderStrokeFill: "#475569"
---
quadrantChart
  accTitle: Accessible assessment
  accDescr: Evidence review organized across four outcomes
  quadrant-1 Approve
  quadrant-2 Investigate
  quadrant-3 Reject
  quadrant-4 Recheck
  Evidence review: [0.63, 0.76]
`,
  },
  {
    id: 'prod_quadrant_unicode_comments',
    kind: 'quadrant',
    title: 'Unicode regional assessment',
    scenario: 'Unicode labels, quoted punctuation, comments, and direct colors share one chart.',
    aspectRatio: 1,
    features: ['unicode', 'comments', 'x-axis', 'y-axis', 'points', 'point-color'],
    expectedTexts: ['地域評価', 'São Paulo', '서울'],
    source: String.raw`
quadrantChart
  %% Regional labels intentionally mix scripts.
  title 地域評価
  x-axis "低い到達度" --> "高い到達度"
  y-axis "低い関与 ❤" --> "高い関与 ❤"
  quadrant-1 拡大
  quadrant-2 検証
  quadrant-3 再評価
  quadrant-4 維持
  東京: [0.82, 0.88]
  서울: [0.36, 0.42]
  "São Paulo": [0.61, 0.73] color: #2563eb
`,
  },
];

const packetCases = [
  {
    id: 'prod_packet_tcp_header',
    kind: 'packet',
    title: 'TCP header layout',
    scenario: 'Explicit ranges and single-bit flags span eight fixed-width packet rows.',
    aspectRatio: 2.4,
    features: [
      'packet-header',
      'explicit-ranges',
      'single-bit-fields',
      'row-splitting',
      'frontmatter-title',
      'show-bits',
    ],
    expectedTexts: ['TCP Packet', 'Source Port', 'SYN', 'Data (variable length)'],
    source: String.raw`
---
title: TCP Packet
---
packet
  0-15: "Source Port"
  16-31: "Destination Port"
  32-63: "Sequence Number"
  64-95: "Acknowledgment Number"
  96-99: "Data Offset"
  100-105: "Reserved"
  106: "URG"
  107: "ACK"
  108: "PSH"
  109: "RST"
  110: "SYN"
  111: "FIN"
  112-127: "Window"
  128-143: "Checksum"
  144-159: "Urgent Pointer"
  160-191: "(Options and Padding)"
  192-255: "Data (variable length)"
`,
  },
  {
    id: 'prod_packet_udp_counts',
    kind: 'packet',
    title: 'UDP packet bit counts',
    scenario: 'Automatic bit counts and explicit ranges combine in one packet.',
    aspectRatio: 2.4,
    features: ['bit-count-fields', 'mixed-addressing', 'title', 'row-splitting'],
    expectedTexts: ['UDP Packet', 'Source Port', 'Checksum', 'Data payload'],
    source: String.raw`
packet
  title UDP Packet
  +16: "Source Port"
  +16: "Destination Port"
  32-47: "Length"
  +16: "Checksum"
  +64: "Data payload"
`,
  },
  {
    id: 'prod_packet_control_flags',
    kind: 'packet',
    title: 'Control flag register',
    scenario: 'Single-bit flags and compact ranges preserve source order within one row.',
    aspectRatio: 2.8,
    features: ['single-bit-fields', 'explicit-ranges', 'show-bits'],
    expectedTexts: ['Version', 'Priority', 'ACK', 'FIN'],
    source: String.raw`
packet
  0-3: "Version"
  4-7: "Priority"
  8: "URG"
  9: "ACK"
  10: "PSH"
  11: "RST"
  12: "SYN"
  13: "FIN"
  14-31: "Reserved"
`,
  },
  {
    id: 'prod_packet_compact_config',
    kind: 'packet',
    title: 'Compact packet without bit labels',
    scenario: 'Packet-specific dimensions, hidden bit labels, and intrinsic sizing are configured.',
    aspectRatio: 2.6,
    features: ['configuration', 'hide-bits', 'responsive-sizing', 'bit-count-fields'],
    expectedTexts: ['Compact frame', 'Type', 'Length', 'Payload'],
    source: String.raw`
---
config:
  packet:
    rowHeight: 28
    bitWidth: 20
    bitsPerRow: 16
    showBits: false
    paddingX: 2
    paddingY: 4
    useMaxWidth: false
---
packet
  title Compact frame
  +4: "Type"
  +4: "Flags"
  +8: "Length"
  +32: "Payload"
`,
  },
  {
    id: 'prod_packet_beta_telemetry',
    kind: 'packet',
    title: 'Packet beta telemetry frame',
    scenario: 'The packet-beta alias renders through the same parser and renderer.',
    aspectRatio: 2.5,
    features: ['packet-beta-header', 'bit-count-fields', 'row-splitting'],
    expectedTexts: ['Telemetry frame', 'Device ID', 'Timestamp', 'Reading'],
    source: String.raw`
packet-beta
  title Telemetry frame
  +8: "Version"
  +24: "Device ID"
  +32: "Timestamp"
  +32: "Reading"
`,
  },
  {
    id: 'prod_packet_accessible_unicode',
    kind: 'packet',
    title: 'Accessible international packet',
    scenario: 'Accessibility metadata, Unicode, entities, comments, and escaped labels coexist.',
    aspectRatio: 2.4,
    features: ['accessibility', 'comments', 'escaped-labels', 'unicode'],
    expectedTexts: ['地域 packet', '種類 &amp; mode', '東京', '서울'],
    source: String.raw`
packet
  title 地域 packet
  accTitle: International packet fields
  accDescr: Field ranges for a regional transport frame
  %% Labels preserve entities and escaped quote syntax.
  +8: "種類 &amp; mode"
  +8: "東京 \"edge\""
  +16: "서울"
`,
  },
  {
    id: 'prod_packet_cross_row_field',
    kind: 'packet',
    title: 'Cross-row protocol field',
    scenario: 'One long field is split repeatedly at exact row boundaries.',
    aspectRatio: 2.2,
    features: ['row-splitting', 'bit-count-fields', 'show-bits'],
    expectedTexts: ['Cross-row field', 'Preamble', 'Variable extension', 'Trailer'],
    source: String.raw`
packet
  title Cross-row field
  +5: "Preamble"
  +90: "Variable extension"
  +1: "Trailer"
`,
  },
  {
    id: 'prod_packet_wide_rows',
    kind: 'packet',
    title: 'Wide 64-bit packet rows',
    scenario: 'A 64-bit row configuration preserves explicit and counted field geometry.',
    aspectRatio: 3,
    features: ['configuration', 'mixed-addressing', 'responsive-sizing'],
    expectedTexts: ['Wide record', 'Identifier', 'Sequence', 'Payload'],
    source: String.raw`
---
config:
  packet:
    bitWidth: 16
    bitsPerRow: 64
    rowHeight: 36
    paddingX: 4
    paddingY: 8
    useMaxWidth: true
---
packet
  title Wide record
  0-15: "Identifier"
  +16: "Sequence"
  32-63: "Payload"
  +64: "Extended payload"
`,
  },
];

const radarCases = [
  {
    id: 'prod_radar_grade_benchmark',
    kind: 'radar',
    title: 'Academic grade benchmark',
    scenario: 'Two students are compared with positional values across six labeled axes.',
    aspectRatio: 1,
    features: [
      'axis-declarations',
      'axis-labels',
      'positional-entries',
      'multiple-curves',
      'frontmatter-title',
      'explicit-range',
      'legend',
      'circle-graticule',
    ],
    expectedTexts: ['Grades benchmark', 'Mathematics', 'Alice', 'Bob'],
    source: String.raw`
---
title: Grades benchmark
---
radar-beta
  title Grades benchmark
  axis math["Mathematics"], science["Science"], language["Language"]
  axis history["History"], geography["Geography"], arts["Arts"]
  curve alice["Alice"] { 86, 91, 79, 73, 82, 94 }
  curve bob["Bob"] { 74, 78, 88, 84, 90, 81 }
  min 0
  max 100
`,
  },
  {
    id: 'prod_radar_reordered_quality',
    kind: 'radar',
    title: 'Reordered quality indicators',
    scenario: 'Named entries appear out of order and resolve against the declared axis sequence.',
    aspectRatio: 1,
    features: [
      'axis-declarations',
      'axis-labels',
      'detailed-entries',
      'reference-reordering',
      'multiple-curves',
      'title',
      'polygon-graticule',
    ],
    expectedTexts: ['Quality indicators', 'Reliability', 'Observed', 'Goal'],
    source: String.raw`
radar-beta:
  title Quality indicators
  axis reliability["Reliability"], performance["Performance"], usability["Usability"], maintainability["Maintainability"]
  curve observed["Observed"] { usability: 74, reliability: 92, maintainability: 66, performance: 81 }
  curve goal["Goal"] { maintainability 88, performance 90, reliability 96, usability 89 }
  graticule polygon
  max 100
`,
  },
  {
    id: 'prod_radar_hidden_legend',
    kind: 'radar',
    title: 'Private operational profile',
    scenario: 'A polygon chart uses an explicit scale, custom tick count, and no legend.',
    aspectRatio: 1,
    features: ['hidden-legend', 'ticks', 'polygon-graticule', 'explicit-range', 'title'],
    expectedTexts: ['Operational profile', 'Detection', 'Containment'],
    source: String.raw`
radar-beta
  title Operational profile
  axis detect["Detection"], contain["Containment"], restore["Restoration"], learn["Learning"]
  curve current { 62, 78, 71, 83 }
  min 20
  max 100
  ticks 8
  showLegend false
  graticule polygon
`,
  },
  {
    id: 'prod_radar_inferred_maximum',
    kind: 'radar',
    title: 'Inferred product maximum',
    scenario: 'The circular scale maximum is inferred from three complete product curves.',
    aspectRatio: 1,
    features: [
      'inferred-max',
      'circle-graticule',
      'multiple-curves',
      'positional-entries',
      'ticks',
    ],
    expectedTexts: ['Product comparison', 'Product A', 'Product C'],
    source: String.raw`
radar-beta
  title Product comparison
  axis fit["Market fit"], growth["Growth"], margin["Margin"], retention["Retention"], reach["Reach"]
  curve a["Product A"] { 41, 72, 58, 66, 79 }
  curve b["Product B"] { 68, 54, 81, 73, 62 }
  curve c["Product C"] { 57, 83, 64, 77, 69 }
  ticks 6
  graticule circle
`,
  },
  {
    id: 'prod_radar_intrinsic_config',
    kind: 'radar',
    title: 'Intrinsic configured radar',
    scenario: 'Diagram dimensions, margins, scale factors, tension, and intrinsic sizing are configured.',
    aspectRatio: 1.25,
    features: [
      'configuration',
      'responsive-sizing',
      'curve-tension',
      'title',
      'circle-graticule',
    ],
    expectedTexts: ['Configured capacity', 'Compute', 'Reserved'],
    source: String.raw`
---
config:
  radar:
    width: 500
    height: 380
    marginTop: 36
    marginRight: 52
    marginBottom: 40
    marginLeft: 52
    axisScaleFactor: 0.84
    axisLabelFactor: 0.96
    curveTension: 0.3
    useMaxWidth: false
---
radar-beta
  title Configured capacity
  axis compute["Compute"], memory["Memory"], network["Network"], storage["Storage"]
  curve reserved["Reserved"] { 75, 82, 68, 79 }
  curve consumed["Consumed"] { 61, 73, 57, 65 }
  max 100
`,
  },
  {
    id: 'prod_radar_theme_palette',
    kind: 'radar',
    title: 'Custom radar palette',
    scenario: 'Root palette values and nested radar theme variables style all chart layers.',
    aspectRatio: 1,
    features: ['theme-variables', 'configuration', 'multiple-curves', 'legend'],
    expectedTexts: ['Portfolio health', 'Current', 'Forecast'],
    source: String.raw`
---
config:
  theme: base
  themeVariables:
    textColor: "#172554"
    cScale0: "#2563eb"
    cScale1: "#dc2626"
    cScale2: "#16a34a"
    radar:
      axisColor: "#475569"
      axisStrokeWidth: 3
      axisLabelFontSize: 14
      curveOpacity: 0.32
      curveStrokeWidth: 3
      graticuleColor: "#94a3b8"
      graticuleOpacity: 0.24
      graticuleStrokeWidth: 2
      legendFontSize: 13
---
radar-beta
  title Portfolio health
  axis value["Value"], confidence["Confidence"], urgency["Urgency"], readiness["Readiness"], reach["Reach"]
  curve current["Current"] { 78, 69, 84, 72, 88 }
  curve forecast["Forecast"] { 89, 82, 76, 91, 93 }
  curve threshold["Threshold"] { 65, 65, 65, 65, 65 }
  max 100
`,
  },
  {
    id: 'prod_radar_accessible_unicode',
    kind: 'radar',
    title: 'Accessible international radar',
    scenario: 'Unicode, accessibility metadata, comments, entities, and escaped labels coexist.',
    aspectRatio: 1,
    features: [
      'accessibility',
      'comments',
      'escaped-labels',
      'unicode',
      'detailed-entries',
      'reference-reordering',
    ],
    expectedTexts: ['地域 comparison', '東京', '서울', 'Observed &amp; reviewed'],
    source: String.raw`
radar-beta
  title 地域 comparison
  accTitle: Accessible international comparison
  accDescr {
    Regional indicators are compared in declared axis order.
  }
  %% Labels intentionally mix scripts and escaped punctuation.
  axis tokyo["東京"], seoul["서울"], sao["São Paulo"], quality["Quality \"index\""]
  curve observed["Observed &amp; reviewed"] { quality: 87, sao: 76, tokyo: 94, seoul: 83 }
  max 100
`,
  },
  {
    id: 'prod_radar_option_precedence',
    kind: 'radar',
    title: 'Radar option precedence',
    scenario: 'Repeated comma-separated options use the last value and cap excessive ticks.',
    aspectRatio: 1,
    features: [
      'option-last-wins',
      'tick-cap',
      'ticks',
      'legend',
      'polygon-graticule',
      'explicit-range',
    ],
    expectedTexts: ['Option precedence', 'Baseline', 'Target'],
    source: String.raw`
radar-beta
  title Option precedence
  axis one["Baseline"], two["Target"], three["Forecast"]
  curve values["Values"] { 3, 7, 5 }
  min 1, min 2
  max 8, max 10
  ticks 4, ticks 40
  showLegend false, showLegend true
  graticule circle, graticule polygon
`,
  },
];

const sankeyCases = [
  {
    id: 'prod_sankey_delivery_flow',
    kind: 'sankey',
    title: 'Delivery flow',
    scenario: 'A multi-stage delivery pipeline branches and merges with visible values.',
    aspectRatio: 1.5,
    features: [
      'sankey-header',
      'csv-records',
      'multi-stage-flow',
      'branching',
      'merging',
      'justify-alignment',
      'gradient-links',
      'show-values',
    ],
    expectedTexts: ['Intake', 'Build', 'Release'],
    source: String.raw`
sankey
Intake,Build,80
Intake,Review,40
Build,Release,70
Build,Rework,10
Review,Release,35
Review,Rejected,5
Rework,Release,10
`,
  },
  {
    id: 'prod_sankey_quoted_csv',
    kind: 'sankey',
    title: 'Quoted CSV values',
    scenario: 'Quoted commas, escaped quotes, and blank lines follow the Sankey CSV grammar.',
    aspectRatio: 1.5,
    features: ['quoted-commas', 'escaped-quotes', 'blank-lines', 'csv-records'],
    expectedTexts: ['North', 'Reviewed', 'Deferred'],
    source: String.raw`
sankey

"North, region","Reviewed ""ready""",20
"North, region",Deferred,12.5

"South, region","Reviewed ""ready""",18
`,
  },
  {
    id: 'prod_sankey_left_source_links',
    kind: 'sankey',
    title: 'Left-aligned source links',
    scenario: 'Left alignment and source-colored links render without value labels.',
    aspectRatio: 1.5,
    features: ['left-alignment', 'source-links', 'hide-values', 'responsive-sizing'],
    expectedTexts: ['Gateway', 'Archive'],
    source: String.raw`
---
config:
  sankey:
    nodeAlignment: left
    linkColor: source
    showValues: false
    useMaxWidth: true
---
sankey
Gateway,Validate,45
Gateway,Reject,5
Validate,Publish,38
Validate,Archive,7
`,
  },
  {
    id: 'prod_sankey_right_target_links',
    kind: 'sankey',
    title: 'Right-aligned target links',
    scenario: 'Right alignment, target colors, custom dimensions, and spacing are configured.',
    aspectRatio: 1.75,
    features: [
      'right-alignment',
      'target-links',
      'node-width',
      'node-padding',
      'responsive-sizing',
    ],
    expectedTexts: ['Requests', 'Completed'],
    source: String.raw`
---
config:
  sankey:
    width: 700
    height: 400
    nodeAlignment: right
    linkColor: target
    nodeWidth: 18
    nodePadding: 8
    useMaxWidth: false
---
sankey
Requests,Accepted,80
Requests,Rejected,10
Accepted,Completed,72
Accepted,Cancelled,8
`,
  },
  {
    id: 'prod_sankey_center_currency',
    kind: 'sankey',
    title: 'Centered currency flow',
    scenario: 'Center alignment formats values with a prefix and suffix.',
    aspectRatio: 1.5,
    features: ['center-alignment', 'value-prefix', 'value-suffix', 'show-values'],
    expectedTexts: ['Budget', 'Delivery'],
    source: String.raw`
---
config:
  sankey:
    nodeAlignment: center
    prefix: "$"
    suffix: "k"
---
sankey
Budget,Engineering,90
Budget,Operations,60
Engineering,Delivery,75
Operations,Delivery,40
`,
  },
  {
    id: 'prod_sankey_outlined_custom_nodes',
    kind: 'sankey',
    title: 'Outlined custom nodes',
    scenario: 'Outlined labels and node-specific colors remain legible over dense links.',
    aspectRatio: 1.5,
    features: ['outlined-labels', 'custom-node-colors', 'gradient-links', 'branching'],
    expectedTexts: ['Source', 'Transform', 'Warehouse'],
    source: String.raw`
---
config:
  sankey:
    labelStyle: outlined
    nodeColors:
      Source: "#2563eb"
      Transform: "#dc2626"
      Warehouse: "#16a34a"
---
sankey
Source,Transform,65
Source,Quarantine,15
Transform,Warehouse,55
Transform,Retry,10
`,
  },
  {
    id: 'prod_sankey_fixed_link_color',
    kind: 'sankey',
    title: 'Fixed link color',
    scenario: 'All links use one explicit CSS color while node colors retain the ordinal palette.',
    aspectRatio: 1.5,
    features: ['fixed-link-color', 'multi-stage-flow', 'merging'],
    expectedTexts: ['Capture', 'Index', 'Search'],
    source: String.raw`
---
title: Search ingestion
config:
  sankey:
    linkColor: "#475569"
---
sankey
Capture,Normalize,64
Normalize,Index,56
Normalize,Discard,8
Index,Search,52
Archive,Search,4
`,
  },
  {
    id: 'prod_sankey_beta_compact',
    kind: 'sankey',
    title: 'Compact beta alias',
    scenario: 'The beta alias shares compact node geometry and frontmatter title metadata.',
    aspectRatio: 1.6,
    features: [
      'sankey-beta-header',
      'frontmatter-title',
      'node-width',
      'node-padding',
      'hide-values',
    ],
    expectedTexts: ['Legacy', 'Bridge', 'Modern'],
    source: String.raw`
---
title: Compact migration
config:
  sankey:
    width: 640
    height: 400
    nodeWidth: 6
    nodePadding: 4
    showValues: false
---
sankey-beta
Legacy,Bridge,48
Bridge,Modern,44
Bridge,Manual review,4
`,
  },
];

const treemapCases = [
  {
    id: 'prod_treemap_product_hierarchy',
    kind: 'treemap',
    title: 'Product hierarchy',
    scenario: 'Multiple product roots contain sections and weighted leaves at three levels.',
    aspectRatio: 1.45,
    features: [
      'treemap-beta-header',
      'sections',
      'leaves',
      'numeric-values',
      'colon-values',
      'multiple-roots',
      'hierarchy',
      'deep-hierarchy',
      'title',
    ],
    expectedTexts: ['Product portfolio', 'Electronics', 'Phones', 'Clothing'],
    source: String.raw`
treemap-beta
title Product portfolio
"Electronics"
    "Mobile"
        "Phones": 52
        "Tablets": 18
    "Computers": 30
"Clothing"
    "Men's": 42
    "Women's": 58
`,
  },
  {
    id: 'prod_treemap_irregular_inventory',
    kind: 'treemap',
    title: 'Irregular inventory hierarchy',
    scenario: 'Comma values and changing indentation widths preserve hierarchy transitions.',
    aspectRatio: 1.4,
    features: [
      'treemap-header',
      'sections',
      'leaves',
      'comma-values',
      'multiple-roots',
      'hierarchy',
      'irregular-indentation',
    ],
    expectedTexts: ['Warehouses', 'Primary stock', 'Archive', 'Cold storage'],
    source: String.raw`
treemap
"Warehouses"
  "Primary stock"
      "Fast moving", 72
      "Reserved", 28
  "Archive", 18
"Cold storage"
   "Perishable", 34
   "Long term", 16
`,
  },
  {
    id: 'prod_treemap_styled_risk',
    kind: 'treemap',
    title: 'Styled risk portfolio',
    scenario: 'Section and leaf classes customize every supported classDef paint property.',
    aspectRatio: 1.4,
    features: [
      'class-selectors',
      'class-definitions',
      'class-fill',
      'class-stroke',
      'class-stroke-width',
      'class-text-color',
      'class-font-style',
      'hierarchy',
    ],
    expectedTexts: ['Risk portfolio', 'Critical', 'Layout drift', 'Controlled'],
    source: String.raw`
treemap-beta
"Risk portfolio"
    "Critical":::critical
        "Parser gap": 26
        "Layout drift": 19
    "Controlled": 41:::verified
    "Accepted": 14
classDef critical fill:#fee2e2,stroke:#dc2626,stroke-width:3px,color:#7f1d1d;
classDef verified fill:#dcfce7,stroke:#16a34a,color:#14532d,font-style:italic;
`,
  },
  {
    id: 'prod_treemap_responsive_geometry',
    kind: 'treemap',
    title: 'Responsive capacity geometry',
    scenario: 'Responsive sizing and all spacing and typography controls shape a dense chart.',
    aspectRatio: 1.55,
    features: [
      'responsive-sizing',
      'padding',
      'diagram-padding',
      'show-values',
      'node-width',
      'node-height',
      'border-width',
      'value-font-size',
      'label-font-size',
    ],
    expectedTexts: ['Capacity plan', 'Compute', 'Reserved', 'Storage'],
    source: String.raw`
---
config:
  treemap:
    useMaxWidth: true
    padding: 5
    diagramPadding: 16
    showValues: true
    nodeWidth: 112
    nodeHeight: 46
    borderWidth: 2
    valueFontSize: 13
    labelFontSize: 15
---
treemap-beta
title Capacity plan
"Compute"
    "Reserved": 68
    "Burst": 32
"Storage"
    "Hot": 44
    "Archive": 56
`,
  },
  {
    id: 'prod_treemap_intrinsic_hidden_values',
    kind: 'treemap',
    title: 'Intrinsic hidden-value allocation',
    scenario: 'Frontmatter title and intrinsic sizing render labels without numeric values.',
    aspectRatio: 1.6,
    features: [
      'frontmatter-title',
      'intrinsic-sizing',
      'hide-values',
      'node-width',
      'node-height',
      'border-width',
      'value-font-size',
      'label-font-size',
    ],
    expectedTexts: ['Allocation without values', 'Engineering', 'Reliability', 'Research'],
    source: String.raw`
---
title: Allocation without values
config:
  treemap:
    useMaxWidth: false
    showValues: false
    nodeWidth: 128
    nodeHeight: 52
    borderWidth: 3
    valueFontSize: 11
    labelFontSize: 16
---
treemap
"Engineering"
    "Reliability": 46
    "Features": 38
"Research": 24
`,
  },
  {
    id: 'prod_treemap_currency_budget',
    kind: 'treemap',
    title: 'Currency budget allocation',
    scenario: 'Dollar values use grouped thousands across nested financial categories.',
    aspectRatio: 1.5,
    features: [
      'currency-format',
      'thousands-format',
      'hierarchy',
      'show-values',
    ],
    expectedTexts: ['Annual budget', 'Operations', 'Salaries', 'Campaigns'],
    source: String.raw`
---
config:
  treemap:
    valueFormat: '$0,0'
---
treemap-beta
title Annual budget
"Operations"
    "Salaries": 720000
    "Infrastructure": 280000
"Growth"
    "Campaigns": 360000
    "Events": 140000
`,
  },
  {
    id: 'prod_treemap_fixed_precision',
    kind: 'treemap',
    title: 'Fixed precision service cost',
    scenario: 'Decimal service costs use a two-place D3 fixed-point formatter.',
    aspectRatio: 1.35,
    features: ['fixed-format', 'show-values', 'numeric-values', 'multiple-roots'],
    expectedTexts: ['Service cost', 'Gateway', 'Search', 'Storage'],
    source: String.raw`
---
config:
  treemap:
    valueFormat: '.2f'
---
treemap
title Service cost
"Gateway": 18.625
"Search": 31.375
"Storage": 24.5
"Observability": 12.75
`,
  },
  {
    id: 'prod_treemap_accessible_market_share',
    kind: 'treemap',
    title: 'Accessible international market share',
    scenario: 'Percentage values, theme selection, metadata, comments, and Unicode coexist.',
    aspectRatio: 1.4,
    features: [
      'percentage-format',
      'accessibility',
      'comments',
      'unicode',
      'theme',
      'frontmatter-title',
      'show-values',
    ],
    expectedTexts: ['地域 market share', '東京', '서울', 'São Paulo'],
    source: String.raw`
---
title: 地域 market share
config:
  theme: forest
  treemap:
    valueFormat: '.1%'
---
treemap-beta
accTitle: Accessible regional market share
accDescr {
  Market share grouped by international operating region.
}
%% Fractional values are formatted as percentages.
"アジア"
    "東京": 0.34
    "서울": 0.27
"Americas"
    "São Paulo": 0.21
    "Others": 0.18
`,
  },
];

const vennCases = [
  {
    id: 'prod_venn_delivery_overlap',
    kind: 'venn',
    title: 'Weighted delivery overlap',
    scenario: 'Two labeled delivery groups use explicit and default sizes with a shared region.',
    aspectRatio: 1.75,
    features: [
      'venn-beta-header',
      'title',
      'sets',
      'bracket-labels',
      'default-sizes',
      'explicit-sizes',
      'pairwise-unions',
    ],
    expectedTexts: ['Delivery ownership', 'Product planning', 'Engineering delivery', 'Shared roadmap'],
    source: String.raw`
venn-beta
  title Delivery ownership
  set Product["Product planning"]:24
  set Engineering["Engineering delivery"]
  union Product,Engineering["Shared roadmap"]:6
`,
  },
  {
    id: 'prod_venn_quoted_multi_set',
    kind: 'venn',
    title: 'Quoted three-team alignment',
    scenario: 'Quoted identifiers, unquoted labels, and a three-set union exercise synthetic pairs.',
    aspectRatio: 1.75,
    features: [
      'sets',
      'quoted-identifiers',
      'unquoted-bracket-labels',
      'explicit-sizes',
      'multi-set-unions',
      'synthetic-pairwise-layout',
    ],
    expectedTexts: ['Customer Need', 'Feasible', 'Viable', 'Ship ready'],
    source: String.raw`
venn-beta
  set "Customer Need"[Customer Need]:30
  set Feasible[Feasible]:24
  set Viable[Viable]:20
  union "Customer Need",Feasible,Viable[Ship ready]:3
`,
  },
  {
    id: 'prod_venn_indented_capabilities',
    kind: 'venn',
    title: 'Indented capability inventory',
    scenario: 'Labeled, unlabeled, and numeric text identifiers are placed in set and union areas.',
    aspectRatio: 1.75,
    features: [
      'sets',
      'pairwise-unions',
      'indented-text',
      'labeled-text',
      'unlabeled-text',
      'numeric-text-identifiers',
    ],
    expectedTexts: ['Client', 'Compose UI', 'OfflineCache', '2026', 'Telemetry'],
    source: String.raw`
venn-beta
  set Client["Client"]:22
    text C1["Compose UI"]
    text OfflineCache
  set Platform["Platform"]:20
    text 2026
  union Client,Platform["Shared"]:7
    text T1["Telemetry"]
`,
  },
  {
    id: 'prod_venn_explicit_contracts',
    kind: 'venn',
    title: 'Explicit cross-area contracts',
    scenario: 'Explicit text statements target single and pairwise areas independently of indentation.',
    aspectRatio: 1.75,
    features: [
      'sets',
      'pairwise-unions',
      'explicit-text',
      'labeled-text',
      'unlabeled-text',
      'quoted-identifiers',
    ],
    expectedTexts: ['Runtime', 'Storage', 'Lifecycle contract', 'Schema contract'],
    source: String.raw`
venn-beta
  set Runtime["Runtime"]:18
  set Storage["Storage"]:16
  union Runtime,Storage["Persistence"]:5
text Runtime Lifecycle["Lifecycle contract"]
text Runtime,Storage "Schema contract"
`,
  },
  {
    id: 'prod_venn_styled_release_risk',
    kind: 'venn',
    title: 'Styled release risk',
    scenario: 'Set, intersection, and text-node styles cover every supported paint property and color form.',
    aspectRatio: 1.75,
    features: [
      'set-styles',
      'intersection-styles',
      'text-styles',
      'fill',
      'stroke',
      'stroke-width',
      'fill-opacity',
      'text-color',
      'hex-colors',
      'rgb-colors',
      'rgba-colors',
    ],
    expectedTexts: ['Known risk', 'Unknown risk', 'Mitigation', 'Owner review'],
    source: String.raw`
venn-beta
  set Known["Known risk"]:22
    text Owner["Owner review"]
  set Unknown["Unknown risk"]:18
  union Known,Unknown["Mitigation"]:5
  style Known fill:#ef4444,stroke:#7f1d1d,stroke-width:4px,fill-opacity:0.2,color:#450a0a
  style Unknown fill:rgb(59, 130, 246),stroke:#1e3a8a,fill-opacity:0.18
  style Known,Unknown fill:rgba(34, 197, 94, 0.35),color:#052e16
  style Owner color:rgb(124, 45, 18)
`,
  },
  {
    id: 'prod_venn_responsive_debug',
    kind: 'venn',
    title: 'Responsive debug geometry',
    scenario: 'Explicit dimensions, padding, responsive sizing, and debug guides shape text placement.',
    aspectRatio: 1.75,
    features: [
      'responsive-sizing',
      'width',
      'height',
      'padding',
      'debug-layout',
      'indented-text',
    ],
    expectedTexts: ['Responsive layout', 'Alpha', 'Review queue'],
    source: String.raw`
---
config:
  venn:
    width: 840
    height: 480
    padding: 24
    useMaxWidth: true
    useDebugLayout: true
---
venn-beta
  title Responsive layout
  set Alpha:20
    text A1["Review queue"]
  set Beta:18
  union Alpha,Beta:6
`,
  },
  {
    id: 'prod_venn_intrinsic_theme',
    kind: 'venn',
    title: 'Intrinsic themed comparison',
    scenario: 'Frontmatter selects intrinsic sizing and overrides Venn palette and title colors.',
    aspectRatio: 1.6,
    features: [
      'frontmatter-title',
      'intrinsic-sizing',
      'width',
      'height',
      'theme',
      'theme-variables',
      'hex-colors',
    ],
    expectedTexts: ['Security posture', 'Prevent', 'Detect', 'Respond'],
    source: String.raw`
---
title: Security posture
config:
  theme: forest
  themeVariables:
    venn1: "#0f766e"
    venn2: "#ca8a04"
    vennTitleTextColor: "#172554"
    vennSetTextColor: "#111827"
  venn:
    width: 720
    height: 450
    useMaxWidth: false
---
venn-beta
  set Prevent["Prevent"]:26
  set Detect["Detect"]:22
  union Prevent,Detect["Respond"]:7
`,
  },
  {
    id: 'prod_venn_international_regions',
    kind: 'venn',
    title: 'International regions',
    scenario: 'Comments, Unicode, and merged style declarations coexist.',
    aspectRatio: 1.75,
    features: [
      'comments',
      'unicode',
      'set-styles',
      'text-styles',
      'text-color',
      'fill',
    ],
    expectedTexts: ['地域 collaboration', '東京', '서울', '共有', '共同運用'],
    source: String.raw`
venn-beta
  title 地域 collaboration
  %% Mixed scripts and repeated styles exercise normalization.
  set Tokyo["東京"]:20
    text T1["共同運用"]
  set Seoul["서울"]:18
  union Tokyo,Seoul["共有"]:6
  style Tokyo fill:#f97316
  style Tokyo color:#431407
  style T1 color:#7c2d12
`,
  },
];

const ishikawaCases = [
  {
    id: 'prod_ishikawa_photo_quality',
    kind: 'ishikawa',
    title: 'Photo quality investigation',
    scenario: 'The official beta header drives alternating top-level causes with nested and leaf causes.',
    aspectRatio: 1.8,
    features: [
      'ishikawa-beta-header',
      'effect',
      'top-level-causes',
      'alternating-causes',
      'nested-causes',
      'leaf-causes',
    ],
    expectedTexts: ['Blurry Photo', 'Process', 'Out of focus', 'User', 'Shaky hands'],
    source: String.raw`
ishikawa-beta
  Blurry Photo
  Process
    Out of focus
    Shutter speed too slow
  User
    Shaky hands
  Equipment
    Damaged lens
  Environment
    Too dark
`,
  },
  {
    id: 'prod_ishikawa_root_only',
    kind: 'ishikawa',
    title: 'Root-only operational effect',
    scenario: 'The stable header renders a long effect without any cause branches.',
    aspectRatio: 1.4,
    features: [
      'ishikawa-header',
      'effect',
      'root-only',
      'long-wrapping',
    ],
    expectedTexts: ['Customer visible request processing degradation'],
    source: String.raw`
ishikawa
Customer visible request processing degradation
`,
  },
  {
    id: 'prod_ishikawa_deep_hierarchy',
    kind: 'ishikawa',
    title: 'Deep ownership hierarchy',
    scenario: 'A deeply nested cause chain exercises alternating descendant ordering and indentation normalization.',
    aspectRatio: 1.9,
    features: [
      'nested-causes',
      'deep-hierarchy',
      'base-level-normalization',
      'irregular-indentation',
    ],
    expectedTexts: ['Release incident', 'Delivery', 'Review', 'Automation', 'Capacity'],
    source: String.raw`
ishikawa-beta
    Release incident
Delivery
   Review
       Automation
          Capacity
             Queue saturation
People
  Ownership gap
`,
  },
  {
    id: 'prod_ishikawa_encoded_regions',
    kind: 'ishikawa',
    title: 'Encoded regional causes',
    scenario: 'Comments, entities, HTML breaks, and multilingual labels share one hierarchy.',
    aspectRatio: 1.8,
    features: [
      'comments',
      'entities',
      'html-breaks',
      'unicode',
      'nested-causes',
    ],
    expectedTexts: ['Regional outage', '東京', '서울', 'São Paulo'],
    source: String.raw`
%% Mixed scripts and sanitized text exercise the shared text pipeline.
ishikawa
Regional outage
  東京 &amp; edge
    Cache&lt;br/&gt;miss
  서울 routing
    Retry storm
  São Paulo capacity
`,
  },
  {
    id: 'prod_ishikawa_responsive_config',
    kind: 'ishikawa',
    title: 'Responsive configured analysis',
    scenario: 'Frontmatter title, theme, padding, and responsive sizing shape a multi-branch analysis.',
    aspectRatio: 1.8,
    features: [
      'frontmatter-title',
      'diagram-padding',
      'responsive-sizing',
      'theme',
      'alternating-causes',
    ],
    expectedTexts: ['Payment failure', 'Client', 'Service'],
    source: String.raw`
---
title: Checkout reliability review
config:
  theme: forest
  ishikawa:
    diagramPadding: 36
    useMaxWidth: true
---
ishikawa-beta
Payment failure
  Client
    Stale checkout state
  Service
    Dependency timeout
  Data
    Replica lag
  Operations
    Delayed escalation
`,
  },
  {
    id: 'prod_ishikawa_intrinsic_config',
    kind: 'ishikawa',
    title: 'Intrinsic configured analysis',
    scenario: 'Intrinsic sizing and compact diagram padding retain deterministic fishbone bounds.',
    aspectRatio: 1.75,
    features: [
      'diagram-padding',
      'intrinsic-sizing',
      'top-level-causes',
      'leaf-causes',
    ],
    expectedTexts: ['Build delay', 'Toolchain', 'Dependencies', 'Infrastructure'],
    source: String.raw`
---
config:
  ishikawa:
    diagramPadding: 12
    useMaxWidth: false
---
ishikawa
Build delay
  Toolchain
  Dependencies
  Infrastructure
`,
  },
  {
    id: 'prod_ishikawa_alternating_capacity',
    kind: 'ishikawa',
    title: 'Alternating capacity factors',
    scenario: 'Six top-level causes exercise repeated upper and lower branch pairing.',
    aspectRatio: 2,
    features: [
      'top-level-causes',
      'alternating-causes',
      'leaf-causes',
    ],
    expectedTexts: ['Capacity shortfall', 'Traffic', 'Storage', 'Compute', 'Network', 'Scheduling', 'Operations'],
    source: String.raw`
ishikawa-beta
Capacity shortfall
  Traffic
  Storage
  Compute
  Network
  Scheduling
  Operations
`,
  },
  {
    id: 'prod_ishikawa_long_nested_labels',
    kind: 'ishikawa',
    title: 'Long nested evidence labels',
    scenario: 'Long labels at several depths exercise the renderer wrapping thresholds.',
    aspectRatio: 2,
    features: [
      'nested-causes',
      'deep-hierarchy',
      'long-wrapping',
    ],
    expectedTexts: [
      'End to end production compatibility verification failure',
      'Asynchronous replication completion evidence missing',
      'Operational readiness and rollback evidence incomplete',
    ],
    source: String.raw`
ishikawa
End to end production compatibility verification failure
  Asynchronous replication completion evidence missing
    Cross region validation label not recorded
      Deterministic rendering verification evidence unavailable
  Operational readiness and rollback evidence incomplete
    Customer facing result confirmation evidence delayed
`,
  },
];

const cynefinCases = [
  {
    id: 'prod_cynefin_delivery_domains',
    kind: 'cynefin',
    title: 'Delivery decision domains',
    scenario: 'All five fixed domains classify delivery work and retain declaration-independent placement.',
    aspectRatio: 4 / 3,
    features: [
      'cynefin-beta-header',
      'domains',
      'fixed-domain-layout',
      'quoted-items',
      'domain-descriptions',
      'wavy-boundaries',
    ],
    expectedTexts: ['Product discovery', 'Architecture review', 'Release checklist', 'Incident containment', 'Untriaged request'],
    source: String.raw`
cynefin-beta
  title Delivery decision domains
  clear
    "Release checklist"
  chaotic
    "Incident containment"
  complex
    "Product discovery"
  confusion
    "Untriaged request"
  complicated
    "Architecture review"
`,
  },
  {
    id: 'prod_cynefin_transition_lifecycle',
    kind: 'cynefin',
    title: 'Operational transition lifecycle',
    scenario: 'Labelled and unlabelled transitions connect domain centers while self-loops are filtered.',
    aspectRatio: 4 / 3,
    features: [
      'transitions',
      'transition-labels',
      'unlabelled-transitions',
      'self-loop-filtering',
    ],
    expectedTexts: ['Pattern identified', 'Best practice codified', 'Stabilized'],
    source: String.raw`
cynefin-beta
  complex
    "Explore signal"
  complicated
    "Analyze evidence"
  clear
    "Codify response"
  chaotic
    "Contain impact"
  complex --> complicated : "Pattern identified"
  complicated --> clear : "Best practice codified"
  chaotic --> complex : "Stabilized"
  confusion --> chaotic
  clear --> clear : "Ignored"
`,
  },
  {
    id: 'prod_cynefin_confusion_overflow',
    kind: 'cynefin',
    title: 'Confusion overflow triage',
    scenario: 'The center ellipse renders three unknowns and summarizes the remaining backlog.',
    aspectRatio: 4 / 3,
    features: [
      'confusion-items',
      'confusion-overflow',
      'quoted-items',
    ],
    expectedTexts: ['Unknown owner', 'Unknown impact', 'Unknown urgency'],
    source: String.raw`
cynefin-beta
  confusion
    "Unknown owner"
    "Unknown impact"
    "Unknown urgency"
    "Unknown dependency"
    "Unknown deadline"
`,
  },
  {
    id: 'prod_cynefin_intrinsic_worksheet',
    kind: 'cynefin',
    title: 'Intrinsic decision worksheet',
    scenario: 'Explicit geometry, straight boundaries, hidden descriptions, and intrinsic sizing produce a compact worksheet.',
    aspectRatio: 1.4,
    features: [
      'empty-domains',
      'hidden-domain-descriptions',
      'straight-boundaries',
      'deterministic-seed',
      'width',
      'height',
      'padding',
      'intrinsic-sizing',
    ],
    expectedTexts: ['Decision worksheet'],
    source: String.raw`
---
title: Decision worksheet
config:
  cynefin:
    width: 700
    height: 500
    padding: 20
    showDomainDescriptions: false
    boundaryAmplitude: 0
    seed: 42
    useMaxWidth: false
---
cynefin-beta
  complex
  complicated
  chaotic
  clear
`,
  },
  {
    id: 'prod_cynefin_responsive_theme',
    kind: 'cynefin',
    title: 'Responsive themed operating model',
    scenario: 'Nested Cynefin theme variables and responsive sizing customize every visual role.',
    aspectRatio: 4 / 3,
    features: [
      'responsive-sizing',
      'theme',
      'theme-variables',
      'deterministic-seed',
      'wavy-boundaries',
    ],
    expectedTexts: ['Adaptive experiment', 'Expert diagnosis', 'Known remediation', 'Emergency action'],
    source: String.raw`
---
config:
  theme: forest
  themeVariables:
    cynefin:
      complexBg: "#dcfce7"
      complicatedBg: "#dbeafe"
      chaoticBg: "#fee2e2"
      clearBg: "#fef9c3"
      confusionBg: "#ede9fe"
      boundaryColor: "#334155"
      cliffColor: "#be123c"
      arrowColor: "#0369a1"
      domainFontSize: 18
      itemFontSize: 13
  cynefin:
    seed: 73
    useMaxWidth: true
---
cynefin-beta
  complex
    "Adaptive experiment"
  complicated
    "Expert diagnosis"
  clear
    "Known remediation"
  chaotic
    "Emergency action"
`,
  },
  {
    id: 'prod_cynefin_duplicate_domain',
    kind: 'cynefin',
    title: 'Latest domain declaration',
    scenario: 'A repeated domain replaces its item list without changing its fixed visual position.',
    aspectRatio: 4 / 3,
    features: [
      'duplicate-domain-replacement',
      'domains',
      'quoted-items',
    ],
    expectedTexts: ['Replacement experiment', 'Preserved analysis'],
    source: String.raw`
cynefin-beta
  complex
    "Superseded experiment"
  complicated
    "Preserved analysis"
  complex
    "Replacement experiment"
`,
  },
  {
    id: 'prod_cynefin_accessible_regions',
    kind: 'cynefin',
    title: 'Accessible regional decisions',
    scenario: 'Frontmatter, accessibility metadata, comments, entities, and multilingual labels share one framework.',
    aspectRatio: 4 / 3,
    features: [
      'frontmatter-title',
      'accessibility',
      'comments',
      'entities',
      'unicode',
    ],
    expectedTexts: ['Regional operating model', '東京', '서울 analysis', 'São Paulo response'],
    source: String.raw`
---
title: Regional operating model
---
cynefin-beta
  accTitle: Accessible regional decisions
  accDescr: Work classified across international operating regions
  %% Entity decoding and mixed scripts use the shared preprocessing path.
  complex
    "東京 &amp; discovery"
  complicated
    "서울 analysis"
  chaotic
    "São Paulo response"
`,
  },
  {
    id: 'prod_cynefin_colon_header',
    kind: 'cynefin',
    title: 'Colon declaration form',
    scenario: 'The detector-compatible colon header preserves fixed domains and deterministic boundaries.',
    aspectRatio: 4 / 3,
    features: [
      'colon-header',
      'cynefin-beta-header',
      'deterministic-seed',
    ],
    expectedTexts: ['Governed process', 'Novel response'],
    source: String.raw`
---
config:
  cynefin:
    seed: -17
---
cynefin-beta:
  clear
    "Governed process"
  chaotic
    "Novel response"
`,
  },
];

const agentflowCases = [
  {
    id: 'prod_agentflow_typed_review',
    kind: 'agentflow',
    title: 'Typed review workflow',
    scenario: 'All six Agentflow shape aliases and three edge semantics form one review workflow.',
    layout: 'dagre',
    aspectRatio: 1.5,
    features: [
      'agentflow-beta-header',
      'shape-aliases',
      'sequence-edges',
      'chained-edges',
      'labelled-edges',
      'reference-edges',
      'failure-edges',
      'single-line-metadata',
    ],
    expectedTexts: ['Change request', 'Review change', 'run_checks', 'Approved?', 'Review policy', 'Publish change'],
    source: String.raw`
---
config:
  layout: dagre
  theme: default
  look: classic
---
agentflow-beta TB
  request["Change request"]@{ shape: input }
  review["Review change"]@{ shape: task }
  checks["run_checks"]@{ shape: tool }
  decision["Approved?"]@{ shape: decision }
  policy["Review policy"]@{ shape: refdoc }
  publish["Publish change"]@{ shape: action }
  request --> review --> checks --> decision
  review -.- policy
  decision -- approved --> publish
  decision --x review
`,
  },
  {
    id: 'prod_agentflow_nested_global',
    kind: 'agentflow',
    title: 'Nested flows with global context',
    scenario: 'Nested agent containers consult a shared top-level reference and hand work between flows.',
    layout: 'dagre',
    aspectRatio: 4 / 3,
    features: [
      'directions',
      'flows',
      'nested-flows',
      'global-nodes',
      'reference-edges',
      'container-metadata',
    ],
    expectedTexts: ['Research Team', 'Discovery Agent', 'Synthesis Agent', 'Shared knowledge base'],
    source: String.raw`
---
config:
  layout: dagre
  theme: default
  look: classic
---
agentflow-beta TD
  global
    knowledge["Shared knowledge base"]@{ shape: refdoc }
  end
  flow team["Research Team"]
    flow discoverer["Discovery Agent"]
      collect["Collect evidence"]@{ shape: task }
      collect -.- knowledge
    end
    discoverer@{ model: "research-model", instruction: "Collect cited evidence." }
    flow synthesizer["Synthesis Agent"]
      summarize["Synthesize findings"]@{ shape: task }
      summarize -.- knowledge
    end
    discoverer --> synthesizer
  end
`,
  },
  {
    id: 'prod_agentflow_collapsed_boundary',
    kind: 'agentflow',
    title: 'Collapsed boundary handoff',
    scenario: 'A collapsed flow retains incoming and outgoing edges as one summary node.',
    layout: 'dagre',
    aspectRatio: 1.4,
    features: [
      'flows',
      'collapsed-flows',
      'sequence-edges',
      'container-metadata',
    ],
    expectedTexts: ['Raw request', 'Private processing', 'Delivered result'],
    source: String.raw`
---
config:
  layout: dagre
  theme: default
  look: classic
---
agentflow-beta TB
  input["Raw request"]@{ shape: input }
  flow private["Private processing"]
    validate["Validate request"]@{ shape: task }
    transform["Transform payload"]@{ shape: tool }
    validate --> transform
  end
  private@{ view: "collapsed", description: "Internal implementation" }
  output["Delivered result"]@{ shape: action }
  input --> validate
  transform --> output
`,
  },
  {
    id: 'prod_agentflow_connector_contracts',
    kind: 'agentflow',
    title: 'Connector reference contracts',
    scenario: 'Bare, dotted, and URL connector references coexist with preserved connector metadata.',
    layout: 'dagre',
    aspectRatio: 1.8,
    features: [
      'connectors',
      'connector-ref-bare',
      'connector-ref-dotted',
      'connector-ref-url',
      'custom-metadata',
      'single-line-metadata',
    ],
    expectedTexts: ['Issue API', 'Create issue', 'Read issue', 'Notify callback'],
    source: String.raw`
---
config:
  layout: dagre
  theme: default
  look: classic
---
agentflow-beta LR
  connector issues["Issue API"]
  issues@{ protocol: "http", endpoint: "https://example.com/issues", owner: "delivery" }
  create["Create issue"]@{ shape: tool, connectorRef: "issues.create" }
  read["Read issue"]@{ shape: tool, connectorRef: "issues" }
  notify["Notify callback"]@{ shape: action, connectorRef: "https://example.com/hooks/release" }
  create --> read --> notify
`,
  },
  {
    id: 'prod_agentflow_parallel_canonical',
    kind: 'agentflow',
    title: 'Parallel canonical tools',
    scenario: 'A right-to-left fan-out combines an alias with canonical supported shape names.',
    layout: 'dagre',
    aspectRatio: 1.7,
    features: [
      'directions',
      'canonical-shapes',
      'fan-out',
      'sequence-edges',
    ],
    expectedTexts: ['Orchestrate request', 'Retrieve context', 'Rank options', 'Verify result'],
    source: String.raw`
---
config:
  layout: dagre
  theme: default
  look: classic
---
agentflow-beta RL
  request["Orchestrate request"]@{ shape: task }
  retrieve["Retrieve context"]@{ shape: subprocess }
  rank["Rank options"]@{ shape: framed-rectangle }
  verify["Verify result"]@{ shape: diamond }
  request --> retrieve & rank & verify
`,
  },
  {
    id: 'prod_agentflow_metadata_contract',
    kind: 'agentflow',
    title: 'Typed metadata contract',
    scenario: 'Single-line and multiline metadata preserve typed contracts and unknown consumer fields.',
    layout: 'dagre',
    aspectRatio: 1.5,
    features: [
      'multiline-metadata',
      'single-line-metadata',
      'custom-metadata',
      'shape-aliases',
      'directions',
    ],
    expectedTexts: ['Customer prompt', 'Generate answer', 'Send answer'],
    source: String.raw`
---
config:
  layout: dagre
  theme: default
  look: classic
---
agentflow-beta BT
  prompt["Customer prompt"]@{ shape: input, value: "Explain the release" }
  generate["Generate answer"]@{
    shape: tool
    params: "prompt :: String"
    returns: "Answer"
    retry: 2
    cache: "15m"
  }
  send["Send answer"]@{ shape: action, example: "published" }
  prompt --> generate --> send
`,
  },
  {
    id: 'prod_agentflow_configured_accessible',
    kind: 'agentflow',
    title: 'Configured accessible workflow',
    scenario: 'Frontmatter configuration, accessibility metadata, entities, comments, and Unicode render together.',
    layout: 'dagre',
    aspectRatio: 1.6,
    features: [
      'frontmatter-title',
      'accessibility',
      'comments',
      'entities',
      'unicode',
      'agentflow-config',
      'intrinsic-sizing',
      'theme',
      'look',
    ],
    expectedTexts: ['Regional workflow', 'München request', '서울 verification', 'São Paulo publish'],
    source: String.raw`
---
title: Regional workflow
config:
  layout: dagre
  theme: forest
  look: classic
  agentflow:
    nodeSpacing: 68
    rankSpacing: 76
    diagramPadding: 24
    useMaxWidth: false
---
agentflow-beta LR
  accTitle: Accessible regional workflow
  accDescr {
    International requests move through verification and publication.
  }
  %% Encoded entities and mixed scripts use the shared preprocessing path.
  request["東京 &amp; München request"]@{ shape: input }
  verify["서울 verification"]@{ shape: task }
  publish["São Paulo publish"]@{ shape: action }
  request --> verify --> publish
`,
  },
  {
    id: 'prod_agentflow_responsive_handoff',
    kind: 'agentflow',
    title: 'Responsive agent handoff',
    scenario: 'Per-diagram spacing and responsive sizing apply to a horizontal multi-agent handoff.',
    layout: 'dagre',
    aspectRatio: 1.8,
    features: [
      'directions',
      'flows',
      'agentflow-config',
      'responsive-sizing',
      'theme',
      'look',
      'container-metadata',
    ],
    expectedTexts: ['Intake Agent', 'Resolution Agent', 'Classify request', 'Resolve request'],
    source: String.raw`
---
config:
  layout: dagre
  theme: default
  look: classic
  agentflow:
    nodeSpacing: 44
    rankSpacing: 88
    titleTopMargin: 18
    useMaxWidth: true
---
agentflow-beta LR
  flow intake["Intake Agent"]
    classify["Classify request"]@{ shape: decision }
  end
  intake@{ instruction: "Assign a support category." }
  flow resolution["Resolution Agent"]
    resolve["Resolve request"]@{ shape: task }
  end
  resolution@{ model: "resolution-model" }
  intake --> resolution
`,
  },
];

const timelineCases = [
  {
    id: 'prod_timeline_release_history',
    kind: 'timeline',
    title: 'Product release history',
    scenario: 'A horizontal timeline presents periods with one or more release events.',
    aspectRatio: 1.6,
    features: ['lr', 'title', 'periods', 'events'],
    expectedTexts: ['Product release history', '2024', 'General availability'],
    source: String.raw`
timeline
  title Product release history
  2024 : Private preview : Public beta
  2025 : General availability
`,
  },
  {
    id: 'prod_timeline_vertical_delivery',
    kind: 'timeline',
    title: 'Vertical delivery plan',
    scenario: 'The TD renderer places periods and event stacks on opposite sides of the axis.',
    aspectRatio: 1,
    features: ['td', 'sections', 'events', 'metadata'],
    expectedTexts: ['Vertical delivery plan', 'Build', 'Release candidate'],
    source: String.raw`
timeline TD
  title Vertical delivery plan
  accTitle: Accessible vertical delivery plan
  accDescr: Build and release milestones
  section Build
    Foundation : Architecture review : API contract
  section Release
    Release candidate : Automated checks : Manual review
`,
  },
  {
    id: 'prod_timeline_continued_events',
    kind: 'timeline',
    title: 'Continued event stack',
    scenario: 'Events on following lines attach to the most recent period.',
    aspectRatio: 1.4,
    features: ['lr', 'continued-events', 'events'],
    expectedTexts: ['Implementation', 'Accessibility review', 'Launch approval'],
    source: String.raw`
timeline
  Implementation : Core complete
                 : Accessibility review : Load test
                 : Launch approval
`,
  },
  {
    id: 'prod_timeline_industrial_sections',
    kind: 'timeline',
    title: 'Industrial eras',
    scenario: 'Multiple sections group periods while preserving source order.',
    aspectRatio: 1.8,
    features: ['lr', 'sections', 'html-breaks', 'unicode'],
    expectedTexts: ['産業時代', '17th-20th century', 'Industry 4.0'],
    source: String.raw`
timeline
  title 産業時代
  section 17th-20th century
    Industry 1.0 : Machinery, Water power, Steam <br> power
    Industry 2.0 : Electricity and mass production
  section 21st century
    Industry 4.0 : Internet, Robotics, Internet of Things
`,
  },
  {
    id: 'prod_timeline_sectionless_palette',
    kind: 'timeline',
    title: 'Sectionless color rotation',
    scenario: 'Periods without sections rotate through Mermaid color slots.',
    aspectRatio: 1.7,
    features: ['lr', 'sectionless-colors', 'events', 'comments'],
    expectedTexts: ['Discover', 'Deliver', 'Operate'],
    source: String.raw`
timeline
  %% Period colors rotate without explicit sections.
  Discover : User research
  Design : Architecture
  Deliver : Implementation
  Operate : Monitoring
`,
  },
  {
    id: 'prod_timeline_disable_multicolor',
    kind: 'timeline',
    title: 'Monochrome sectionless timeline',
    scenario: 'The timeline-specific switch keeps sectionless periods in one color slot.',
    aspectRatio: 1.7,
    features: ['lr', 'disable-multicolor', 'frontmatter-config'],
    expectedTexts: ['Plan', 'Build', 'Validate'],
    source: String.raw`
---
config:
  timeline:
    disableMulticolor: true
---
timeline
  Plan : Scope
  Build : Implementation
  Validate : Acceptance
`,
  },
  {
    id: 'prod_timeline_spacing_config',
    kind: 'timeline',
    title: 'Configured spacing',
    scenario: 'Timeline padding, left margin, and intrinsic sizing come from frontmatter.',
    aspectRatio: 1.5,
    features: ['lr', 'frontmatter-config', 'sections'],
    expectedTexts: ['Configured spacing', 'Milestones', 'Production'],
    source: String.raw`
---
config:
  timeline:
    leftMargin: 210
    padding: 28
    useMaxWidth: false
---
timeline
  title Configured spacing
  section Milestones
    Preview : Internal
    Production : External
`,
  },
  {
    id: 'prod_timeline_custom_colors',
    kind: 'timeline',
    title: 'Custom timeline colors',
    scenario: 'Color scale variables style adjacent sections and their events.',
    aspectRatio: 1.6,
    features: ['theme-colors', 'redux-theme', 'sections', 'events'],
    expectedTexts: ['Custom timeline colors', 'Discovery', 'Delivery'],
    source: String.raw`
---
config:
  theme: redux-color
  look: neo
  themeVariables:
    cScale0: "#dbeafe"
    cScale1: "#dcfce7"
    cScaleLabel0: "#1e3a8a"
    cScaleLabel1: "#14532d"
---
timeline
  title Custom timeline colors
  section Discovery
    Research : Interviews
  section Delivery
    Launch : Production rollout
`,
  },
];

const kanbanCases = [
  {
    id: 'prod_kanban_delivery_board',
    kind: 'kanban',
    title: 'Delivery workflow',
    scenario: 'Explicit stage and task identifiers form a three-column delivery board.',
    aspectRatio: 2.1,
    features: ['sections', 'tasks', 'explicit-ids'],
    expectedTexts: ['Backlog', 'Implement parser', 'Production release'],
    source: String.raw`
kanban
  backlog[Backlog]
    parser[Implement parser]
    layout[Match layout]
  verify[Verification]
    tests[Run automated tests]
    review[Review screenshots]
  done[Done]
    release[Production release]
`,
  },
  {
    id: 'prod_kanban_anonymous_forms',
    kind: 'kanban',
    title: 'Anonymous and shaped labels',
    scenario: 'Anonymous nodes and legacy shape delimiters retain their visible labels.',
    aspectRatio: 1.7,
    features: ['anonymous-items', 'node-forms', 'tasks'],
    expectedTexts: ['Planning', 'Rounded task', 'Hexagon task'],
    source: String.raw`
kanban
  [Planning]
    (Rounded task)
    circle((Circle task))
    cloud(-Cloud task-)
    hex{{Hexagon task}}
`,
  },
  {
    id: 'prod_kanban_nested_comments',
    kind: 'kanban',
    title: 'Flattened nested tasks',
    scenario: 'Deeper indentation and comments preserve task order in the current stage.',
    aspectRatio: 1.5,
    features: ['deeper-indentation', 'comments', 'tasks'],
    expectedTexts: ['Discovery', 'Interviews', 'Validated findings'],
    source: String.raw`
kanban
  discovery[Discovery]
    interviews[Interviews]
      synthesis[Research synthesis]
    %% Deep descendants remain direct cards in this diagram family.
        findings[Validated findings]
`,
  },
  {
    id: 'prod_kanban_metadata',
    kind: 'kanban',
    title: 'Operational task metadata',
    scenario: 'Ticket numbers, assignees, and all priority bands appear on task cards.',
    aspectRatio: 1.8,
    features: ['ticket', 'assigned', 'priorities'],
    expectedTexts: ['Incident response', 'OPS-401', 'Owner B'],
    source: String.raw`
kanban
  active[Incident response]
    restore[Restore traffic]@{ ticket: OPS-401, assigned: 'Owner A', priority: 'Very High' }
    mitigate[Reduce load]@{ ticket: OPS-402, assigned: 'Owner B', priority: High }
    monitor[Monitor recovery]@{ priority: Medium }
    followup[Write follow-up]@{ priority: Low }
    polish[Polish dashboard]@{ priority: 'Very Low' }
`,
  },
  {
    id: 'prod_kanban_ticket_links',
    kind: 'kanban',
    title: 'Linked delivery tickets',
    scenario: 'A configured ticket URL creates external interactions for ticket labels.',
    aspectRatio: 1.7,
    features: ['ticket', 'ticket-links', 'assigned'],
    expectedTexts: ['Implementation', 'KB-501', 'Acceptance'],
    source: String.raw`
---
config:
  kanban:
    ticketBaseUrl: "https://issues.example/browse/#TICKET#"
---
kanban
  build[Build]
    implementation[Implementation]@{ ticket: KB-501, assigned: Ada }
  verify[Verify]
    acceptance[Acceptance]@{ ticket: KB-502, assigned: Lin }
`,
  },
  {
    id: 'prod_kanban_width_and_empty',
    kind: 'kanban',
    title: 'Configured columns',
    scenario: 'Configured section width applies to wrapped cards and an empty stage.',
    aspectRatio: 1.9,
    features: ['section-width', 'wrapped-labels', 'empty-sections'],
    expectedTexts: ['Ready', 'deterministic rendering', 'Waiting'],
    source: String.raw`
---
config:
  kanban:
    sectionWidth: 240
---
kanban
  ready[Ready]
    long[Complete deterministic rendering verification for every supported platform]
  waiting[Waiting]
  complete[Complete]
    shipped[Artifacts published]
`,
  },
  {
    id: 'prod_kanban_theme_markdown',
    kind: 'kanban',
    title: 'Themed markdown board',
    scenario: 'Palette variables and Markdown emphasis style stages and card labels.',
    aspectRatio: 1.8,
    features: ['theme-colors', 'markdown', 'sections'],
    expectedTexts: ['Architecture', 'API contract', 'Visual review'],
    source: String.raw`
---
config:
  theme: base
  look: classic
  themeVariables:
    cScale2: "#dbeafe"
    cScale3: "#dcfce7"
    cScaleLabel2: "#1e3a8a"
    cScaleLabel3: "#14532d"
---
kanban
  design[**Architecture**]
    contract[*API contract*]
  validate[**Validation**]
    visual[*Visual review*]
`,
  },
  {
    id: 'prod_kanban_unicode',
    kind: 'kanban',
    title: 'International workflow',
    scenario: 'Unicode labels and metadata render in the same fixed-column layout.',
    aspectRatio: 1.8,
    features: ['unicode', 'sections', 'assigned', 'ticket'],
    expectedTexts: ['準備', '東京で確認', '서울 출시'],
    source: String.raw`
kanban
  prepare[準備]
    tokyo[東京で確認]@{ assigned: 品質 }
  complete[완료]
    seoul[서울 출시]@{ ticket: 국제-7 }
`,
  },
];

export const conformanceCases = [
  ...flowchartCases,
  ...xyChartCases,
  ...quadrantCases,
  ...timelineCases,
  ...kanbanCases,
  ...sequenceCases,
  ...classCases,
  ...stateCases,
  ...erCases,
  ...ganttCases,
  ...pieCases,
  ...journeyCases,
  ...requirementCases,
  ...gitGraphCases,
  ...mindmapCases,
  ...packetCases,
  ...radarCases,
  ...sankeyCases,
  ...treemapCases,
  ...vennCases,
  ...ishikawaCases,
  ...cynefinCases,
  ...agentflowCases,
];

export const cases = [
  ...releaseCandidateCases.map((entry) => ({
    ...entry,
    features: [],
    expectedTexts: [],
  })),
  ...conformanceCases,
];

function expandTemplate(template) {
  return template.variants.map((variant) => ({
    id: `prod_${template.kind}_${template.id}_${variant.slug}`,
    kind: template.kind,
    title: variant.title,
    scenario: variant.scenario,
    layout: typeof template.layout === 'function'
      ? template.layout(variant)
      : template.layout,
    aspectRatio: template.aspectRatio,
    features: typeof template.features === 'function'
      ? template.features(variant)
      : template.features,
    expectedTexts: template.expectedTexts(variant),
    source: template.source(variant),
  }));
}

function array(values) {
  return `[${values.map((value) => (
    typeof value === 'string' ? JSON.stringify(value) : value
  )).join(', ')}]`;
}

function pointArray(points) {
  return `[${points.map(([value, label]) => `${value} ${JSON.stringify(label)}`).join(', ')}]`;
}

function pieSource(value, suffix = '') {
  return String.raw`
pie${suffix.length > 0 ? ` ${suffix}` : ''}
  title ${value.diagramTitle}
${value.values.map(([label, amount]) => `  ${JSON.stringify(label)} : ${amount}`).join('\n')}
`;
}
