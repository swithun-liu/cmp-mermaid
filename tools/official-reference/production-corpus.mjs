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
};

const flowchartCases = [
  ...expandTemplate({
    kind: 'flowchart',
    id: 'orchestration',
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
  layout: elk
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

export const conformanceCases = [
  ...flowchartCases,
  ...xyChartCases,
  ...sequenceCases,
  ...classCases,
  ...stateCases,
  ...erCases,
  ...ganttCases,
  ...pieCases,
  ...journeyCases,
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
    layout: template.layout,
    aspectRatio: template.aspectRatio,
    features: template.features,
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
