const dimension = (name, category, zero, one) => Object.freeze({
  name,
  category,
  options: Object.freeze([zero, one]),
});

export const familyDimensionSpecs = Object.freeze({
  swimlanes: Object.freeze([
    dimension('direction', 'layout', 'left-to-right', 'top-to-bottom'),
    dimension('lane-count', 'topology', 'two-lanes', 'four-lanes'),
    dimension('lane-nesting', 'topology', 'flat-lanes', 'nested-lane'),
    dimension('branching', 'topology', 'single-path', 'cross-lane-fan-out'),
    dimension('merging', 'topology', 'separate-endings', 'cross-lane-fan-in'),
    dimension('cycle', 'topology', 'acyclic', 'return-loop'),
    dimension('edge-semantics', 'style', 'solid-unlabelled', 'labelled-dotted'),
    dimension('node-form', 'style', 'process', 'decision'),
  ]),
  architecture: Object.freeze([
    dimension('groups', 'topology', 'ungrouped', 'platform-group'),
    dimension('nested-groups', 'topology', 'single-level', 'nested-groups'),
    dimension('junction', 'topology', 'direct-route', 'junction-fan-out'),
    dimension('storage', 'data', 'compute-only', 'database-service'),
    dimension('bidirectional', 'topology', 'one-way', 'two-way'),
    dimension('edge-label', 'style', 'unlabelled', 'labelled'),
    dimension('alignment', 'layout', 'free-layout', 'row-column-grid'),
    dimension('boundary-route', 'topology', 'service-route', 'group-route'),
  ]),
  c4: Object.freeze([
    dimension('external-system', 'topology', 'internal-only', 'external-system'),
    dimension('boundary', 'topology', 'unbounded', 'system-boundary'),
    dimension('database', 'data', 'service', 'database'),
    dimension('queue', 'data', 'direct-call', 'queued-flow'),
    dimension('fan-out', 'topology', 'linear', 'fan-out'),
    dimension('relationship-direction', 'layout', 'automatic', 'directed'),
    dimension('bidirectional', 'topology', 'one-way', 'bidirectional'),
    dimension('relationship-style', 'style', 'default', 'styled'),
  ]),
  railroad: Object.freeze([
    dimension('choice', 'topology', 'single-token', 'alternatives'),
    dimension('optional', 'syntax', 'required', 'optional'),
    dimension('zero-or-more', 'topology', 'single', 'zero-or-more'),
    dimension('one-or-more', 'topology', 'single-use', 'one-or-more'),
    dimension('nesting', 'topology', 'flat', 'nested-sequence'),
    dimension('symbol-kind', 'syntax', 'terminal', 'nonterminal-special'),
    dimension('rule-count', 'data', 'one-rule', 'three-rules'),
    dimension('branch-width', 'data', 'two-alternatives', 'four-alternatives'),
  ]),
  treeview: Object.freeze([
    dimension('root-count', 'topology', 'single-root', 'second-root'),
    dimension('branch-width', 'topology', 'narrow', 'wide'),
    dimension('depth', 'topology', 'shallow', 'deep'),
    dimension('entry-kind', 'data', 'files', 'mixed-files-directories'),
    dimension('quoted-labels', 'syntax', 'bare', 'quoted'),
    dimension('box-drawing', 'syntax', 'indentation', 'box-drawing'),
    dimension('description', 'data', 'plain', 'described'),
    dimension('class-highlight', 'style', 'default', 'highlighted'),
  ]),
  xychart: Object.freeze([
    dimension('orientation', 'layout', 'vertical', 'horizontal'),
    dimension('x-axis', 'data', 'categorical', 'numeric'),
    dimension('domain', 'data', 'automatic', 'explicit'),
    dimension('primary-plot', 'style', 'line', 'bar'),
    dimension('series-count', 'data', 'one-series', 'three-series'),
    dimension('point-count', 'data', 'four-points', 'eight-points'),
    dimension('value-shape', 'data', 'monotonic', 'peaked'),
    dimension('labels', 'style', 'plain', 'legend-and-data-labels'),
  ]),
  quadrant: Object.freeze([
    dimension('point-count', 'data', 'four-points', 'eight-points'),
    dimension('distribution', 'data', 'clustered', 'all-quadrants'),
    dimension('boundary-points', 'data', 'interior', 'boundaries'),
    dimension('empty-region', 'topology', 'all-occupied', 'empty-quadrant'),
    dimension('point-radius', 'style', 'default-radius', 'varied-radius'),
    dimension('point-color', 'style', 'theme-color', 'inline-colors'),
    dimension('class-style', 'style', 'no-class', 'class-style'),
    dimension('metadata', 'syntax', 'minimal', 'title-and-comments'),
  ]),
  timeline: Object.freeze([
    dimension('direction', 'layout', 'left-to-right', 'top-to-bottom'),
    dimension('sections', 'topology', 'sectionless', 'sectioned'),
    dimension('period-count', 'data', 'three-periods', 'six-periods'),
    dimension('continued-events', 'topology', 'one-event-per-period', 'continued-events'),
    dimension('event-density', 'data', 'sparse', 'dense'),
    dimension('html-break', 'syntax', 'plain-text', 'html-break'),
    dimension('palette', 'style', 'multicolor', 'single-color'),
    dimension('metadata', 'syntax', 'minimal', 'title-and-comments'),
  ]),
  kanban: Object.freeze([
    dimension('section-count', 'topology', 'two-sections', 'four-sections'),
    dimension('task-count', 'data', 'four-tasks', 'eight-tasks'),
    dimension('task-identity', 'syntax', 'anonymous', 'explicit-ids'),
    dimension('node-form', 'style', 'square', 'rounded'),
    dimension('indentation', 'topology', 'flat', 'nested'),
    dimension('empty-section', 'topology', 'all-populated', 'empty-section'),
    dimension('metadata', 'data', 'plain', 'ticket-assignee-priority'),
    dimension('markdown', 'syntax', 'plain-labels', 'markdown-labels'),
  ]),
  sequence: Object.freeze([
    dimension('autonumber', 'syntax', 'manual', 'autonumber'),
    dimension('participant-kind', 'data', 'participants', 'actors-and-boundary'),
    dimension('activation', 'topology', 'inactive', 'activated'),
    dimension('message-shape', 'style', 'solid', 'dotted-and-self'),
    dimension('loop', 'topology', 'linear', 'loop'),
    dimension('alternative', 'topology', 'single-outcome', 'alt-opt'),
    dimension('parallel', 'topology', 'sequential', 'parallel'),
    dimension('notes', 'data', 'no-notes', 'notes'),
  ]),
  class: Object.freeze([
    dimension('direction', 'layout', 'left-to-right', 'top-to-bottom'),
    dimension('members', 'data', 'attributes-only', 'attributes-and-methods'),
    dimension('generics', 'syntax', 'concrete', 'generic'),
    dimension('annotation', 'data', 'plain', 'annotated'),
    dimension('relation-kind', 'topology', 'association', 'inheritance-composition'),
    dimension('cardinality', 'data', 'unqualified', 'cardinalities-and-labels'),
    dimension('namespace', 'topology', 'flat', 'namespace'),
    dimension('notes', 'data', 'no-notes', 'notes-and-style'),
  ]),
  state: Object.freeze([
    dimension('direction', 'layout', 'left-to-right', 'top-to-bottom'),
    dimension('description', 'data', 'identifier-only', 'aliases-and-descriptions'),
    dimension('composite', 'topology', 'flat', 'composite'),
    dimension('choice', 'topology', 'direct', 'choice'),
    dimension('fork-join', 'topology', 'sequential', 'fork-join'),
    dimension('concurrency', 'topology', 'single-region', 'concurrent-regions'),
    dimension('cycle', 'topology', 'acyclic', 'recovery-loop'),
    dimension('notes-style', 'style', 'plain', 'notes-and-class'),
  ]),
  er: Object.freeze([
    dimension('direction', 'layout', 'left-to-right', 'top-to-bottom'),
    dimension('attributes', 'data', 'minimal', 'rich-attributes'),
    dimension('keys', 'data', 'primary-key', 'primary-foreign-unique'),
    dimension('cardinality', 'topology', 'one-to-many', 'many-to-many'),
    dimension('identification', 'topology', 'identifying', 'non-identifying'),
    dimension('branching', 'topology', 'linear', 'hub'),
    dimension('subgraph', 'topology', 'flat', 'subgraph'),
    dimension('alias-style', 'style', 'identifiers', 'aliases-and-classes'),
  ]),
  gantt: Object.freeze([
    dimension('sections', 'topology', 'one-section', 'three-sections'),
    dimension('dependencies', 'topology', 'fixed-dates', 'dependencies'),
    dimension('task-states', 'style', 'critical', 'mixed-states'),
    dimension('milestone', 'topology', 'no-milestone', 'milestone'),
    dimension('calendar', 'data', 'calendar-days', 'weekend-excludes'),
    dimension('axis', 'layout', 'default-axis', 'formatted-axis'),
    dimension('top-axis', 'layout', 'bottom-axis', 'top-axis'),
    dimension('vertical-marker', 'data', 'no-marker', 'vertical-marker'),
  ]),
  pie: Object.freeze([
    dimension('show-data', 'style', 'legend-only', 'show-data'),
    dimension('slice-count', 'data', 'four-slices', 'eight-slices'),
    dimension('value-kind', 'data', 'integers', 'decimals-and-zero'),
    dimension('distribution', 'data', 'balanced', 'skewed'),
    dimension('donut', 'style', 'pie', 'donut'),
    dimension('legend-position', 'layout', 'right', 'center'),
    dimension('highlight', 'style', 'none', 'static-highlight'),
    dimension('label-form', 'syntax', 'plain', 'escaped-and-duplicate'),
  ]),
  journey: Object.freeze([
    dimension('sections', 'topology', 'one-section', 'three-sections'),
    dimension('task-count', 'data', 'four-tasks', 'eight-tasks'),
    dimension('scores', 'data', 'integer-scores', 'decimal-scores'),
    dimension('actors', 'data', 'single-actor', 'multiple-actors'),
    dimension('actor-reuse', 'topology', 'fixed-actor', 'reordered-reused-actors'),
    dimension('actorless', 'data', 'all-assigned', 'actorless-task'),
    dimension('title-source', 'syntax', 'diagram-title', 'frontmatter-title'),
    dimension('long-text', 'layout', 'short-labels', 'long-labels'),
  ]),
  requirement: Object.freeze([
    dimension('direction', 'layout', 'left-to-right', 'top-to-bottom'),
    dimension('requirement-types', 'data', 'functional', 'mixed-types'),
    dimension('risk-levels', 'data', 'medium', 'mixed-risks'),
    dimension('verification', 'data', 'test', 'mixed-methods'),
    dimension('elements', 'topology', 'requirements-only', 'elements'),
    dimension('relationship-kind', 'topology', 'contains', 'mixed-relations'),
    dimension('reverse-relations', 'topology', 'forward-only', 'reverse'),
    dimension('styling', 'style', 'default', 'direct-and-class-style'),
  ]),
  gitgraph: Object.freeze([
    dimension('orientation', 'layout', 'left-to-right', 'top-to-bottom'),
    dimension('branches', 'topology', 'main-only', 'feature-branch'),
    dimension('merge', 'topology', 'linear', 'merge'),
    dimension('parallel-work', 'topology', 'single-branch', 'two-branches'),
    dimension('commit-types', 'style', 'normal', 'highlight-reverse'),
    dimension('tags', 'data', 'untagged', 'tagged'),
    dimension('messages', 'data', 'ids-only', 'messages'),
    dimension('branch-names', 'syntax', 'bare', 'quoted'),
  ]),
  mindmap: Object.freeze([
    dimension('depth', 'topology', 'shallow', 'deep'),
    dimension('width', 'topology', 'narrow', 'wide'),
    dimension('shape-variety', 'style', 'default', 'mixed-shapes'),
    dimension('irregular-indentation', 'topology', 'regular', 'irregular'),
    dimension('markdown', 'syntax', 'plain', 'markdown'),
    dimension('html-entities', 'syntax', 'literal', 'html-and-entities'),
    dimension('layout', 'layout', 'dagre', 'tidy-tree'),
    dimension('metadata', 'data', 'minimal', 'title-comments-unicode'),
  ]),
  packet: Object.freeze([
    dimension('header', 'syntax', 'packet', 'packet-beta'),
    dimension('field-count', 'data', 'four-fields', 'eight-fields'),
    dimension('field-widths', 'data', 'fixed-four-bit', 'mixed-widths'),
    dimension('single-bit', 'data', 'ranges-only', 'single-bit-fields'),
    dimension('addressing', 'syntax', 'explicit-ranges', 'mixed-addressing'),
    dimension('row-splitting', 'layout', 'single-row', 'multiple-rows'),
    dimension('bit-labels', 'style', 'show-bits', 'hide-bits'),
    dimension('metadata', 'syntax', 'minimal', 'title-comments-escaped'),
  ]),
  radar: Object.freeze([
    dimension('axis-count', 'data', 'four-axes', 'eight-axes'),
    dimension('entry-form', 'syntax', 'positional', 'detailed'),
    dimension('curve-count', 'data', 'one-curve', 'three-curves'),
    dimension('range', 'data', 'inferred', 'explicit'),
    dimension('graticule', 'style', 'circle', 'polygon'),
    dimension('legend', 'style', 'shown', 'hidden'),
    dimension('ticks', 'data', 'four-ticks', 'eight-ticks'),
    dimension('reference-order', 'topology', 'declaration-order', 'reordered'),
  ]),
  sankey: Object.freeze([
    dimension('header', 'syntax', 'sankey', 'sankey-beta'),
    dimension('branching', 'topology', 'linear', 'branching'),
    dimension('merging', 'topology', 'separate', 'merging'),
    dimension('stages', 'topology', 'two-stage', 'four-stage'),
    dimension('csv-escaping', 'syntax', 'plain-records', 'quoted-records'),
    dimension('alignment', 'layout', 'justify', 'left'),
    dimension('link-color', 'style', 'source', 'gradient'),
    dimension('values', 'data', 'hidden', 'shown-with-units'),
  ]),
  treemap: Object.freeze([
    dimension('header', 'syntax', 'treemap', 'treemap-beta'),
    dimension('root-count', 'topology', 'single-root', 'multiple-roots'),
    dimension('depth', 'topology', 'shallow', 'deep'),
    dimension('branch-width', 'data', 'four-leaves', 'eight-leaves'),
    dimension('value-syntax', 'syntax', 'colon-values', 'comma-values'),
    dimension('classes', 'style', 'default', 'class-styled'),
    dimension('values', 'data', 'hidden', 'shown-formatted'),
    dimension('sizing', 'layout', 'responsive', 'intrinsic-padded'),
  ]),
  venn: Object.freeze([
    dimension('set-count', 'data', 'three-sets', 'four-sets'),
    dimension('set-size', 'data', 'default-sizes', 'explicit-sizes'),
    dimension('pairwise-unions', 'topology', 'none', 'pairwise'),
    dimension('multi-set-union', 'topology', 'pairwise-only', 'triple-union'),
    dimension('intersection-text', 'data', 'unlabelled', 'labelled'),
    dimension('set-style', 'style', 'default', 'styled-sets'),
    dimension('intersection-style', 'style', 'default-intersections', 'styled-intersections'),
    dimension('sizing', 'layout', 'responsive', 'intrinsic-debug'),
  ]),
  ishikawa: Object.freeze([
    dimension('header', 'syntax', 'ishikawa', 'ishikawa-beta'),
    dimension('cause-count', 'data', 'three-causes', 'six-causes'),
    dimension('nesting', 'topology', 'flat', 'nested'),
    dimension('depth', 'topology', 'two-level', 'four-level'),
    dimension('alternation', 'topology', 'single-side', 'alternating'),
    dimension('indentation', 'syntax', 'regular', 'irregular'),
    dimension('label-form', 'syntax', 'plain', 'entities-and-html'),
    dimension('wrapping', 'layout', 'short', 'long-wrapping'),
  ]),
  cynefin: Object.freeze([
    dimension('domain-density', 'data', 'one-item', 'three-items'),
    dimension('empty-domain', 'topology', 'all-populated', 'empty-domain'),
    dimension('transitions', 'topology', 'single-transition', 'cross-domain'),
    dimension('transition-labels', 'data', 'unlabelled', 'labelled'),
    dimension('confusion', 'data', 'single-item', 'overflow'),
    dimension('descriptions', 'data', 'hidden', 'shown'),
    dimension('boundaries', 'style', 'wavy', 'straight'),
    dimension('sizing', 'layout', 'responsive', 'intrinsic'),
  ]),
  block: Object.freeze([
    dimension('columns', 'layout', 'automatic', 'explicit'),
    dimension('spans', 'topology', 'single-column', 'column-spans'),
    dimension('spaces', 'topology', 'packed', 'spaces'),
    dimension('composites', 'topology', 'flat', 'nested-composites'),
    dimension('shapes', 'style', 'rectangles', 'mixed-shapes'),
    dimension('links', 'topology', 'linear', 'branching'),
    dimension('edge-style', 'style', 'solid', 'labelled-thick-dotted'),
    dimension('classes', 'style', 'default', 'classes-and-inline-style'),
  ]),
  eventmodeling: Object.freeze([
    dimension('syntax', 'syntax', 'compact', 'relaxed'),
    dimension('timeframes', 'data', 'four-frames', 'eight-frames'),
    dimension('reset-frame', 'topology', 'continuous', 'reset'),
    dimension('entity-alias', 'data', 'default-entity', 'entity-alias'),
    dimension('swimlanes', 'topology', 'default-lane', 'namespaced-lanes'),
    dimension('relations', 'topology', 'inferred', 'explicit'),
    dimension('relation-count', 'topology', 'single', 'multiple'),
    dimension('inline-data', 'data', 'no-data', 'inline-data'),
  ]),
  agentflow: Object.freeze([
    dimension('direction', 'layout', 'left-to-right', 'top-to-bottom'),
    dimension('node-shapes', 'style', 'task-only', 'mixed-shapes'),
    dimension('fan-out', 'topology', 'linear', 'fan-out'),
    dimension('edge-labels', 'data', 'unlabelled', 'labelled'),
    dimension('failure-edge', 'topology', 'success-only', 'failure-path'),
    dimension('flows', 'topology', 'global', 'flow-container'),
    dimension('nested-flows', 'topology', 'single-level', 'nested'),
    dimension('connector-metadata', 'data', 'plain', 'connector-and-metadata'),
  ]),
  usecase: Object.freeze([
    dimension('direction', 'layout', 'left-to-right', 'top-to-bottom'),
    dimension('actor-variants', 'data', 'single-actor', 'multiple-actor-types'),
    dimension('usecase-form', 'style', 'ellipses', 'mixed-forms'),
    dimension('boundary', 'topology', 'system', 'nested-package'),
    dimension('include', 'topology', 'associations', 'include'),
    dimension('extend', 'topology', 'base-only', 'extend'),
    dimension('generalization', 'topology', 'flat-actors', 'generalization'),
    dimension('notes-style', 'style', 'plain', 'notes-and-classes'),
  ]),
  wardley: Object.freeze([
    dimension('component-count', 'data', 'four-components', 'eight-components'),
    dimension('inertia', 'data', 'none', 'inertia'),
    dimension('sourcing', 'data', 'none', 'sourcing-strategies'),
    dimension('dependencies', 'topology', 'linear', 'branching'),
    dimension('link-style', 'style', 'solid', 'dashed-labelled'),
    dimension('flow-links', 'topology', 'dependencies', 'flow'),
    dimension('pipeline', 'topology', 'components', 'pipeline'),
    dimension('annotations', 'data', 'plain', 'notes-annotations-accelerators'),
  ]),
  zenuml: Object.freeze([
    dimension('aliases', 'data', 'direct-names', 'aliases'),
    dimension('participant-groups', 'topology', 'ungrouped', 'grouped'),
    dimension('message-kind', 'style', 'synchronous', 'asynchronous'),
    dimension('nesting', 'topology', 'flat', 'nested-messages'),
    dimension('creation', 'topology', 'existing-participants', 'creation'),
    dimension('returns', 'topology', 'one-way', 'assignments-and-returns'),
    dimension('conditionals', 'topology', 'linear', 'if-else'),
    dimension('concurrency', 'topology', 'sequential', 'loop-and-parallel'),
  ]),
});

export function renderInputSpaceScenario(kind, bits, context) {
  const renderer = renderers[kind];
  if (renderer === undefined) {
    throw new Error(`Unsupported visual input-space kind: ${kind}`);
  }
  return renderer(bits, context);
}

function scenario(
  kind,
  lines,
  bits,
  structuralProfile,
  features,
  expectedTexts,
  layout,
) {
  const dimensions = familyDimensionSpecs[kind].map((spec, index) =>
    Object.freeze({
      name: spec.name,
      category: spec.category,
      option: spec.options[bits[index]],
    })
  );
  return Object.freeze({
    source: lines.join('\n'),
    dimensions: Object.freeze(dimensions),
    structuralProfile: Object.freeze(structuralProfile),
    features: Object.freeze([
      ...features,
      ...dimensions.map(({ name, option }) => `dimension:${name}=${option}`),
    ]),
    expectedTexts: Object.freeze(expectedTexts),
    ...(layout === undefined ? {} : { layout }),
  });
}

function selected(bits, index) {
  return bits[index] === 1;
}

function identifierToken(value) {
  return value.replace(/[^A-Za-z0-9_]/g, '');
}

const renderers = {
  swimlanes: renderSwimlanes,
  architecture: renderArchitecture,
  c4: renderC4,
  railroad: renderRailroad,
  treeview: renderTreeView,
  xychart: renderXyChart,
  quadrant: renderQuadrant,
  timeline: renderTimeline,
  kanban: renderKanban,
  sequence: renderSequence,
  class: renderClass,
  state: renderState,
  er: renderEr,
  gantt: renderGantt,
  pie: renderPie,
  journey: renderJourney,
  requirement: renderRequirement,
  gitgraph: renderGitGraph,
  mindmap: renderMindmap,
  packet: renderPacket,
  radar: renderRadar,
  sankey: renderSankey,
  treemap: renderTreemap,
  venn: renderVenn,
  ishikawa: renderIshikawa,
  cynefin: renderCynefin,
  block: renderBlock,
  eventmodeling: renderEventModeling,
  agentflow: renderAgentflow,
  usecase: renderUseCase,
  wardley: renderWardley,
  zenuml: renderZenUml,
};

function renderSwimlanes(bits, context) {
  const direction = selected(bits, 0) ? 'TD' : 'LR';
  const laneCount = selected(bits, 1) ? 4 : 2;
  const decision = selected(bits, 7);
  const laneNames = ['Intake', 'Assessment', 'Execution', 'Closure'];
  const lines = [`swimlane-beta ${direction}`];
  for (let lane = 0; lane < laneCount; lane += 1) {
    lines.push(`  subgraph ${laneNames[lane]}`);
    if (lane === 1 && selected(bits, 2)) {
      lines.push(
        '    subgraph Specialist',
        decision
          ? `      N${lane + 1}{${context.label(lane)}?}`
          : `      N${lane + 1}[${context.label(lane)}]`,
        '    end',
      );
    } else {
      lines.push(
        decision && lane === 1
          ? `    N${lane + 1}{${context.label(lane)}?}`
          : `    N${lane + 1}[${context.label(lane)}]`,
      );
    }
    lines.push('  end');
  }
  for (let lane = laneCount; lane < 4; lane += 1) {
    lines.push(`  N${lane + 1}[${context.label(lane)}]`);
  }
  lines.push('  N1 --> N2');
  if (selected(bits, 3)) {
    lines.push('  N2 --> N3', '  N2 --> N4');
  } else {
    lines.push('  N2 --> N3', '  N3 --> N4');
  }
  if (selected(bits, 4)) {
    lines.push('  N3 --> N4');
  }
  if (selected(bits, 5)) {
    lines.push('  N4 -.-> N2');
  }
  if (selected(bits, 6)) {
    lines.push('  N1 -->|accepted| N3', '  N4 -. revise .-> N1');
  }
  return scenario(
    'swimlanes',
    lines,
    bits,
    {
      direction,
      laneCount,
      laneDepth: selected(bits, 2) ? 2 : 1,
      fanOut: selected(bits, 3),
      fanIn: selected(bits, 4),
      cycleCount: selected(bits, 5) ? 1 : 0,
      labelledEdgeCount: selected(bits, 6) ? 2 : 0,
      decisionNodeCount: decision ? 1 : 0,
    },
    ['lane-ownership', 'cross-lane-flow', 'branching', 'cycles'],
    [context.label(0)],
    'swimlane',
  );
}

function renderArchitecture(bits, context) {
  const grouped = selected(bits, 0);
  const nested = selected(bits, 1);
  const boundaryRoute = selected(bits, 7);
  const lines = ['architecture-beta'];
  if (grouped) {
    lines.push(`  group platform(cloud)[${context.title}]`);
  }
  if (nested) {
    lines.push(
      `  group runtime(server)[${context.domain.platform}]` +
        (grouped ? ' in platform' : ''),
    );
  }
  if (boundaryRoute) {
    lines.push(
      '  group delivery(cloud)[Delivery]' +
        (grouped ? ' in platform' : ''),
    );
  }
  const owner = nested ? 'runtime' : grouped ? 'platform' : null;
  lines.push(
    `  service s1(server)[${context.label(0)}]${owner ? ` in ${owner}` : ''}`,
    `  service s2(${selected(bits, 3) ? 'database' : 'server'})` +
      `[${context.label(1)}]${boundaryRoute ? ' in delivery' : owner ? ` in ${owner}` : ''}`,
    `  service s3(server)[${context.label(2)}]` +
      `${boundaryRoute ? ' in delivery' : owner ? ` in ${owner}` : ''}`,
    `  service s4(internet)[${context.label(3)}]` +
      `${grouped ? ' in platform' : ''}`,
  );
  if (selected(bits, 2)) {
    lines.push(
      `  junction split${owner ? ` in ${owner}` : ''}`,
      '  s1:R --> L:split',
      '  split:R --> L:s2',
      '  split:B --> T:s3',
    );
  } else {
    lines.push('  s1:R --> L:s2', '  s2:R --> L:s3');
  }
  const finalEdge = selected(bits, 4)
    ? '  s3:R <--> L:s4'
    : '  s3:R --> L:s4';
  lines.push(finalEdge);
  if (selected(bits, 5)) {
    lines.push('  s1:B -[observes]-> T:s4');
  }
  if (selected(bits, 6)) {
    lines.push(
      '  service align_a(server)[Aligned A]',
      '  service align_b(server)[Aligned B]',
      '  service align_c(server)[Aligned C]',
      '  align row align_a align_b align_c',
    );
  }
  if (boundaryRoute) {
    lines.push('  s2{group}:B --> T:s4');
  }
  return scenario(
    'architecture',
    lines,
    bits,
    {
      groupCount: Number(grouped) + Number(nested) + Number(boundaryRoute),
      maxGroupDepth: grouped && nested ? 2 : Number(grouped || nested),
      junctionCount: selected(bits, 2) ? 1 : 0,
      databaseCount: selected(bits, 3) ? 1 : 0,
      bidirectionalEdgeCount: selected(bits, 4) ? 1 : 0,
      labelledEdgeCount: selected(bits, 5) ? 1 : 0,
      alignmentConstraintCount: selected(bits, 6) ? 1 : 0,
      boundaryEdgeCount: boundaryRoute ? 1 : 0,
    },
    ['services', 'groups', 'junctions', 'directional-ports', 'alignment'],
    [context.label(0)],
    'cose-bilkent',
  );
}

function renderC4(bits, context) {
  const boundary = selected(bits, 1);
  const lines = ['C4Container', `  title ${context.title}`];
  if (boundary) {
    lines.push(`  System_Boundary(platform, "${context.domain.platform}") {`);
  }
  const indent = boundary ? '    ' : '  ';
  lines.push(
    `${indent}Person(user, "${context.domain.actor}", "Initiates work")`,
    `${indent}Container(api, "${context.label(0)}", "Kotlin", "Coordinates work")`,
    selected(bits, 2)
      ? `${indent}ContainerDb(store, "${context.label(1)}", "SQL", "Persists state")`
      : `${indent}Container(store, "${context.label(1)}", "Kotlin", "Processes state")`,
  );
  if (selected(bits, 3)) {
    lines.push(
      `${indent}ContainerQueue(queue, "${context.label(2)}", "Events", "Buffers work")`,
    );
  }
  lines.push(
    selected(bits, 0)
      ? `${indent}Container_Ext(external, "${context.label(3)}", "HTTPS", "External dependency")`
      : `${indent}Container(external, "${context.label(3)}", "Kotlin", "Internal dependency")`,
  );
  if (boundary) {
    lines.push('  }');
  }
  lines.push(
    selected(bits, 5)
      ? '  Rel_Right(user, api, "requests")'
      : '  Rel(user, api, "requests")',
    '  Rel(api, store, "reads and writes")',
  );
  if (selected(bits, 3)) {
    lines.push('  Rel(api, queue, "publishes")', '  Rel(queue, external, "delivers")');
  } else {
    lines.push('  Rel(api, external, "calls")');
  }
  if (selected(bits, 4)) {
    lines.push('  Rel(api, external, "validates")');
  }
  if (selected(bits, 6)) {
    lines.push('  BiRel(store, external, "synchronizes")');
  }
  if (selected(bits, 7)) {
    lines.push(
      '  UpdateRelStyle(api, store, $textColor="#7c2d12", ' +
        '$lineColor="#ea580c", $offsetX="-12", $offsetY="16")',
    );
  }
  return scenario(
    'c4',
    lines,
    bits,
    {
      externalCount: selected(bits, 0) ? 1 : 0,
      boundaryDepth: boundary ? 1 : 0,
      databaseCount: selected(bits, 2) ? 1 : 0,
      queueCount: selected(bits, 3) ? 1 : 0,
      apiOutDegree: 2 + Number(selected(bits, 4)),
      directedRelationCount: selected(bits, 5) ? 1 : 0,
      bidirectionalRelationCount: selected(bits, 6) ? 1 : 0,
      styledRelationCount: selected(bits, 7) ? 1 : 0,
    },
    ['containers', 'boundaries', 'storage', 'relationships'],
    [context.title, context.domain.actor],
  );
}

function renderRailroad(bits, context) {
  const token = context.goal.stages[0].toLowerCase();
  let primary = `terminal("${token}")`;
  if (selected(bits, 0)) {
    const alternatives = selected(bits, 7)
      ? 'terminal("create"), terminal("read"), terminal("update"), terminal("delete")'
      : 'terminal("accept"), terminal("reject")';
    primary = `choice(${alternatives})`;
  }
  if (selected(bits, 5)) {
    primary =
      `sequence(nonterminal("subject"), ${primary}, special("validated"))`;
  }
  if (selected(bits, 1)) {
    primary = `optional(${primary})`;
  }
  if (selected(bits, 2)) {
    primary = `zeroOrMore(${primary})`;
  }
  if (selected(bits, 3)) {
    primary = `oneOrMore(${primary})`;
  }
  if (selected(bits, 4)) {
    primary = `sequence(terminal("begin"), ${primary}, terminal("end"))`;
  }
  const lines = ['railroad-beta', `  process = ${primary} ;`];
  if (selected(bits, 6)) {
    lines.push(
      '  identifier = sequence(nonterminal("letter"), zeroOrMore(nonterminal("digit"))) ;',
      '  result = choice(terminal("ok"), special("error")) ;',
    );
  }
  if (selected(bits, 7)) {
    lines.push(
      '  operation = choice(terminal("create"), terminal("read"), ' +
        'terminal("update"), terminal("delete")) ;',
    );
  }
  return scenario(
    'railroad',
    lines,
    bits,
    {
      choiceCount: selected(bits, 0) ? 1 : 0,
      optionalDepth: selected(bits, 1) ? 1 : 0,
      zeroOrMoreDepth: selected(bits, 2) ? 1 : 0,
      oneOrMoreDepth: selected(bits, 3) ? 1 : 0,
      sequenceDepth: selected(bits, 4) ? 1 : 0,
      specialCount: selected(bits, 5) ? 1 : 0,
      ruleCount: 1 + (selected(bits, 6) ? 2 : 0) + Number(selected(bits, 7)),
      alternativeCount: selected(bits, 0) ? (selected(bits, 7) ? 4 : 2) : 1,
    },
    ['grammar-operators', 'nested-expressions', 'multiple-rules'],
    ['process'],
  );
}

function renderTreeView(bits, context) {
  const quoted = selected(bits, 4);
  const root = quoted ? `"${context.domain.object} workspace/"` : `${context.domain.code}/`;
  const file = (name) => quoted ? `"${name}"` : name;
  const entrySuffix = `${selected(bits, 7) ? ' :::highlight' : ''}` +
    `${selected(bits, 6) ? ' ## generated artifact' : ''}`;
  const children = [
    `${file(`${context.goal.code}-one.md`)}${entrySuffix}`,
    `${file(`${context.goal.code}-two.kt`)}${entrySuffix}`,
  ];
  if (selected(bits, 1)) {
    children.push(
      `${file(`${context.goal.code}-three.json`)}${entrySuffix}`,
      `${file(`${context.goal.code}-four.txt`)}${entrySuffix}`,
    );
  }
  const lines = ['treeView-beta'];
  if (selected(bits, 5)) {
    lines.push(`├── ${root}`);
    if (selected(bits, 3)) {
      lines.push('│   ├── src/', `│   │   └── ${children[0]}`);
    } else {
      lines.push(`│   ├── ${children[0]}`);
    }
    lines.push(`│   └── ${children[1]}`);
    if (selected(bits, 0)) {
      lines.push(`└── archive/`, `    └── ${children.at(-1)}`);
    }
  } else {
    lines.push(`    ${root}`);
    if (selected(bits, 3)) {
      lines.push('        src/', `            ${children[0]}`);
      if (selected(bits, 2)) {
        lines.push(
          '            generated/',
          '                reports/',
          `                    ${children[1]}`,
        );
      } else {
        lines.push(`            ${children[1]}`);
      }
    } else {
      lines.push(...children.map((child) => `        ${child}`));
    }
    if (selected(bits, 0)) {
      lines.push('    archive/', `        ${children.at(-1)}`);
    }
  }
  if (selected(bits, 1)) {
    if (selected(bits, 5)) {
      lines.push('├── wide-one.md', '└── wide-two.md');
    } else {
      lines.push('        wide-one.md', '        wide-two.md');
    }
  }
  if (selected(bits, 2)) {
    if (selected(bits, 5)) {
      lines.push(
        '└── deep/',
        '    └── generated/',
        '        └── reports/',
        '            └── evidence.json',
      );
    } else {
      lines.push(
        '        deep/',
        '            generated/',
        '                reports/',
        '                    evidence.json',
      );
    }
  }
  return scenario(
    'treeview',
    lines,
    bits,
    {
      rootCount: selected(bits, 0) ? 2 : 1,
      leafCount: selected(bits, 1) ? 4 : 2,
      maxDepth: selected(bits, 2) ? 4 : selected(bits, 3) ? 2 : 1,
      directoryCount: selected(bits, 3) ? 2 : 0,
      quoted: selected(bits, 4),
      boxDrawing: selected(bits, 5),
      describedCount: selected(bits, 6) ? 2 : 0,
      highlightedCount: selected(bits, 7) ? 2 : 0,
    },
    ['hierarchy', 'files-and-directories', 'box-drawing', 'classes'],
    [context.goal.code],
  );
}

function renderXyChart(bits, context) {
  const pointCount = selected(bits, 5) ? 8 : 4;
  const categories = Array.from(
    { length: pointCount },
    (_, index) => `"${context.goal.stages[index % 4]} ${index + 1}"`,
  );
  const values = Array.from({ length: pointCount }, (_, index) => {
    if (selected(bits, 6)) {
      return 20 + (index <= pointCount / 2 ? index * 14 : (pointCount - index) * 14);
    }
    return 15 + index * 9;
  });
  const config = selected(bits, 7)
    ? [
        '---',
        'config:',
        '  xyChart:',
        '    showLegend: true',
        '    showDataLabel: true',
        '---',
      ]
    : [];
  const lines = [
    ...config,
    `xychart${selected(bits, 0) ? ' horizontal' : ''}`,
    `  title "${context.title}"`,
  ];
  if (selected(bits, 1)) {
    lines.push(`  x-axis 1 --> ${pointCount}`);
  } else {
    lines.push(`  x-axis [${categories.join(', ')}]`);
  }
  if (selected(bits, 2)) {
    lines.push('  y-axis "Signal" 0 --> 120');
  }
  const primary = selected(bits, 3) ? 'bar' : 'line';
  lines.push(`  ${primary} "Primary" [${values.join(', ')}]`);
  if (selected(bits, 4)) {
    lines.push(
      `  line "Trend" [${values.map((value) => value + 8).join(', ')}]`,
      `  bar "Capacity" [${values.map((value) => value + 18).join(', ')}]`,
    );
  }
  return scenario(
    'xychart',
    lines,
    bits,
    {
      orientation: selected(bits, 0) ? 'horizontal' : 'vertical',
      axisKind: selected(bits, 1) ? 'numeric' : 'categorical',
      explicitDomain: selected(bits, 2),
      primaryPlot: primary,
      seriesCount: selected(bits, 4) ? 3 : 1,
      pointCount,
      valueTurns: selected(bits, 6) ? 1 : 0,
      labelsEnabled: selected(bits, 7),
    },
    ['axes', 'plots', 'series-cardinality', 'data-shapes'],
    [context.title],
  );
}

function renderQuadrant(bits, context) {
  const pointCount = selected(bits, 0) ? 8 : 4;
  const lines = [
    'quadrantChart',
    ...(selected(bits, 7)
      ? [`  %% ${context.domain.name} assessment`, `  title ${context.title}`]
      : []),
    '  x-axis Earlier --> Later',
    '  y-axis Lower impact --> Higher impact',
    '  quadrant-1 Prioritize',
    '  quadrant-2 Prepare',
    '  quadrant-3 Observe',
    '  quadrant-4 Execute',
  ];
  const coordinates = selected(bits, 1)
    ? [[0.2, 0.2], [0.2, 0.8], [0.8, 0.2], [0.8, 0.8]]
    : [[0.18, 0.62], [0.24, 0.68], [0.31, 0.73], [0.38, 0.77]];
  for (let index = 0; index < pointCount; index += 1) {
    let [x, y] = coordinates[index % coordinates.length];
    if (selected(bits, 2) && index < 4) {
      [x, y] = [[0, 0], [0, 1], [1, 0], [1, 1]][index];
    }
    if (selected(bits, 3) && x > 0.5 && y < 0.5) {
      y = 0.7;
    }
    const classSuffix = selected(bits, 6) ? ':::priority' : '';
    const inline = selected(bits, 4) || selected(bits, 5)
      ? ` radius: ${selected(bits, 4) ? 7 + index : 8}` +
        `${selected(bits, 5) ? `, color: ${index % 2 ? '#2563eb' : '#16a34a'}` : ''}`
      : '';
    lines.push(
      `  "${context.label(index)}"${classSuffix}: ` +
        `[${x}, ${y}]${inline}`,
    );
  }
  lines.push(
    selected(bits, 1)
      ? '  "Distribution marker": [0.84, 0.16]'
      : '  "Distribution marker": [0.34, 0.66]',
    selected(bits, 3)
      ? '  "Coverage guard": [0.76, 0.76]'
      : '  "Coverage guard": [0.76, 0.24]',
  );
  if (selected(bits, 6)) {
    lines.push(
      '  classDef priority color: #109060, radius: 10, ' +
        'stroke-color: #064e3b, stroke-width: 3px',
    );
  }
  return scenario(
    'quadrant',
    lines,
    bits,
    {
      pointCount,
      distribution: selected(bits, 1) ? 'all-quadrants' : 'clustered',
      occupiedQuadrants: selected(bits, 3) ? 3 : selected(bits, 1) ? 4 : 2,
      boundaryPointCount: selected(bits, 2) ? 4 : 0,
      variedRadius: selected(bits, 4),
      inlineColorCount: selected(bits, 5) ? pointCount : 0,
      classCount: selected(bits, 6) ? 1 : 0,
      metadataLineCount: selected(bits, 7) ? 2 : 0,
    },
    ['points', 'quadrant-distribution', 'boundary-values', 'styles'],
    [context.label(0)],
  );
}

function renderTimeline(bits, context) {
  const periodCount = selected(bits, 2) ? 6 : 3;
  const config = selected(bits, 6)
    ? ['---', 'config:', '  timeline:', '    disableMulticolor: true', '---']
    : [];
  const lines = [
    ...config,
    `timeline${selected(bits, 0) ? ' TD' : ''}`,
  ];
  if (selected(bits, 7)) {
    lines.push(`  %% ${context.domain.name} history`, `  title ${context.title}`);
  }
  const appendPeriods = (start, end) => {
    for (let index = start; index < end; index += 1) {
      const event = `${context.goal.stages[index % 4]} ${index + 1}` +
        `${selected(bits, 5) ? ' <br> checkpoint' : ''}`;
      if (selected(bits, 3) && index % 2 === 1) {
        lines.push(`             : ${event}`);
      } else if (selected(bits, 4)) {
        lines.push(
          `    Period ${index + 1} : ${event} : ` +
            `${context.goal.stages[(index + 1) % 4]} follow-up`,
        );
      } else {
        lines.push(`    Period ${index + 1} : ${event}`);
      }
    }
  };
  if (selected(bits, 1)) {
    lines.push('  section Discovery');
    appendPeriods(0, Math.ceil(periodCount / 2));
    lines.push('  section Delivery');
    appendPeriods(Math.ceil(periodCount / 2), periodCount);
  } else {
    appendPeriods(0, periodCount);
  }
  return scenario(
    'timeline',
    lines,
    bits,
    {
      direction: selected(bits, 0) ? 'TD' : 'LR',
      sectionCount: selected(bits, 1) ? 2 : 0,
      periodCount,
      continuedEventCount: selected(bits, 3) ? Math.floor(periodCount / 2) : 0,
      eventCount: periodCount * (selected(bits, 4) ? 2 : 1),
      htmlBreakCount: selected(bits, 5) ? periodCount : 0,
      multicolor: !selected(bits, 6),
      metadataLineCount: selected(bits, 7) ? 2 : 0,
    },
    ['periods', 'sections', 'continued-events', 'orientation'],
    [context.goal.stages[0]],
  );
}

function renderKanban(bits, context) {
  const sectionCount = selected(bits, 0) ? 4 : 2;
  const taskCount = selected(bits, 1) ? 8 : 4;
  const lines = ['kanban'];
  let taskIndex = 0;
  for (let section = 0; section < sectionCount; section += 1) {
    const empty = selected(bits, 5) && section === sectionCount - 1;
    lines.push(`  section${section + 1}[${context.goal.stages[section % 4]}]`);
    if (empty) {
      continue;
    }
    const count = Math.max(1, Math.ceil(taskCount / sectionCount));
    for (let local = 0; local < count && taskIndex < taskCount; local += 1) {
      const label = selected(bits, 7)
        ? `**${context.label(taskIndex)}**`
        : context.label(taskIndex);
      const id = selected(bits, 2) ? `task${taskIndex + 1}` : '';
      const node = selected(bits, 3)
        ? `${id}(${label})`
        : `${id}[${label}]`;
      const metadata = selected(bits, 6)
        ? `@{ ticket: TASK-${taskIndex + 1}, assigned: Owner${taskIndex + 1}, ` +
          `priority: ${taskIndex % 2 ? 'High' : 'Medium'} }`
        : '';
      const indent = selected(bits, 4) && local > 0 ? '      ' : '    ';
      lines.push(`${indent}${node}${metadata}`);
      taskIndex += 1;
    }
  }
  if (selected(bits, 4)) {
    lines.push(
      '    nestedParent[Nested parent]',
      '      nestedChild[Nested child]',
    );
  }
  return scenario(
    'kanban',
    lines,
    bits,
    {
      sectionCount,
      taskCount: taskIndex,
      explicitIdCount: selected(bits, 2) ? taskIndex : 0,
      roundedTaskCount: selected(bits, 3) ? taskIndex : 0,
      maxIndentDepth: selected(bits, 4) ? 2 : 1,
      emptySectionCount: selected(bits, 5) ? 1 : 0,
      metadataTaskCount: selected(bits, 6) ? taskIndex : 0,
      markdownTaskCount: selected(bits, 7) ? taskIndex : 0,
    },
    ['sections', 'tasks', 'task-forms', 'metadata'],
    [context.goal.stages[0]],
  );
}

function renderSequence(bits, context) {
  const lines = [
    'sequenceDiagram',
    ...(selected(bits, 0) ? ['  autonumber'] : []),
    selected(bits, 1)
      ? `  actor User as ${context.domain.actor}`
      : `  participant User as ${context.domain.actor}`,
    selected(bits, 1)
      ? `  participant Platform@{ type: "boundary", alias: "${context.domain.platform}" }`
      : `  participant Platform as ${context.domain.platform}`,
    '  participant Worker as Specialist',
  ];
  lines.push(`  User->>${selected(bits, 2) ? '+' : ''}Platform: ${context.label(0)}`);
  if (selected(bits, 3)) {
    lines.push(
      `  Platform-->>Worker: ${context.label(1)}`,
      `  Worker->>Worker: ${context.goal.stages[2]} locally`,
    );
  } else {
    lines.push(`  Platform->>Worker: ${context.label(1)}`);
  }
  if (selected(bits, 4)) {
    lines.push(
      '  loop bounded retries',
      `    Worker->>Platform: ${context.label(2)}`,
      '  end',
    );
  } else {
    lines.push(`  Worker->>Platform: ${context.label(2)}`);
  }
  if (selected(bits, 5)) {
    lines.push(
      '  alt accepted',
      `    Platform-->>User: ${context.label(3)}`,
      '  else rejected',
      '    Platform--xUser: remediation required',
      '  end',
      '  opt audit enabled',
      '    Platform->>Platform: append audit event',
      '  end',
    );
  } else {
    lines.push(`  Platform-->>User: ${context.label(3)}`);
  }
  if (selected(bits, 6)) {
    lines.push(
      '  par notify',
      '    Platform->>User: status update',
      '  and persist',
      '    Platform->>Worker: store outcome',
      '  end',
    );
  }
  if (selected(bits, 7)) {
    lines.push(
      '  Note over User,Platform: Input contract is validated',
      '  Note right of Worker: Result is durable',
    );
  }
  if (selected(bits, 2)) {
    lines.push('  deactivate Platform');
  }
  return scenario(
    'sequence',
    lines,
    bits,
    {
      autonumber: selected(bits, 0),
      actorTypeCount: selected(bits, 1) ? 2 : 0,
      activationCount: selected(bits, 2) ? 1 : 0,
      selfMessageCount: selected(bits, 3) ? 1 : 0,
      loopCount: selected(bits, 4) ? 1 : 0,
      alternativeCount: selected(bits, 5) ? 2 : 1,
      parallelBranchCount: selected(bits, 6) ? 2 : 0,
      noteCount: selected(bits, 7) ? 2 : 0,
    },
    ['participants', 'messages', 'control-regions', 'notes'],
    [context.domain.actor, context.label(0)],
  );
}

function renderClass(bits, context) {
  const generic = selected(bits, 2);
  const root = 'Repository';
  const lines = ['classDiagram', `  direction ${selected(bits, 0) ? 'TB' : 'LR'}`];
  if (selected(bits, 6)) {
    lines.push('  namespace domain {');
  }
  lines.push(
    `  class ${root} {`,
    ...(selected(bits, 3) ? ['    <<aggregate>>'] : []),
    `    +String ${context.goal.stages[0].toLowerCase()}`,
    ...(generic ? ['    -List~Record~ records'] : []),
    ...(selected(bits, 1)
      ? [
          '    -Int version',
          '    +save() Boolean',
          '    +findById(String) Result~Boolean~',
        ]
      : []),
    '  }',
    '  class Record {',
    '    +String id',
    '  }',
    '  class Service {',
    ...(selected(bits, 3) ? ['    <<service>>'] : []),
    '    +execute() Boolean',
    '  }',
  );
  if (selected(bits, 6)) {
    lines.push('  }');
  }
  if (selected(bits, 4)) {
    lines.push(
      '  Service <|-- SpecializedService',
      `  ${root} *-- Record`,
    );
  } else {
    lines.push(`  Service --> ${root}`, `${root} --> Record`);
  }
  if (selected(bits, 5)) {
    lines.push(
      `  ${root} "1" *-- "1..*" Record : contains`,
      `  Service ..> ${root} : processes`,
    );
  }
  if (selected(bits, 7)) {
    lines.push(
      `  note for ${root} "Owns ${context.domain.object} state"`,
      '  classDef emphasis fill:#dbeafe,stroke:#1d4ed8,color:#172554',
      `  class ${root} emphasis`,
    );
  }
  return scenario(
    'class',
    lines,
    bits,
    {
      direction: selected(bits, 0) ? 'TB' : 'LR',
      memberCount: selected(bits, 1) ? 6 : 3,
      genericClassCount: generic ? 1 : 0,
      annotationCount: selected(bits, 3) ? 2 : 0,
      relationKindCount: selected(bits, 4) ? 2 : 1,
      qualifiedRelationCount: selected(bits, 5) ? 2 : 0,
      namespaceDepth: selected(bits, 6) ? 1 : 0,
      noteCount: selected(bits, 7) ? 1 : 0,
    },
    ['members', 'relations', 'namespaces', 'notes'],
    ['Repository', 'Record'],
  );
}

function renderState(bits, context) {
  const lines = [
    'stateDiagram-v2',
    `  direction ${selected(bits, 0) ? 'TB' : 'LR'}`,
  ];
  if (selected(bits, 1)) {
    lines.push(
      `  state "${context.label(0)}" as Pending`,
      `  state "${context.label(3)}" as Complete`,
    );
  }
  lines.push('  [*] --> Pending');
  if (selected(bits, 2)) {
    lines.push(
      '  state Processing {',
      '    [*] --> Validate',
      '    Validate --> Execute',
      '    Execute --> [*]',
      '  }',
      '  Pending --> Processing',
    );
  } else {
    lines.push('  Pending --> Validate', '  Validate --> Execute');
  }
  if (selected(bits, 3)) {
    lines.push(
      '  state decision <<choice>>',
      '  Execute --> decision',
      '  decision --> Complete : accepted',
      '  decision --> Rework : rejected',
    );
  } else {
    lines.push('  Execute --> Complete');
  }
  if (selected(bits, 4)) {
    lines.push(
      '  state split <<fork>>',
      '  state merge <<join>>',
      '  Complete --> split',
      '  split --> Notify',
      '  split --> Persist',
      '  Notify --> merge',
      '  Persist --> merge',
      '  merge --> Archived',
    );
  } else {
    lines.push('  Complete --> Archived');
  }
  if (selected(bits, 5)) {
    lines.push(
      '  state Audit {',
      '    [*] --> Evidence',
      '    --',
      '    [*] --> Metrics',
      '  }',
      '  Archived --> Audit',
    );
  }
  if (selected(bits, 6)) {
    lines.push('  Rework --> Pending', '  Archived --> Validate : reopen');
  }
  lines.push('  Archived --> [*]');
  if (selected(bits, 7)) {
    lines.push(
      '  note right of Pending',
      '    Input must be complete',
      '  end note',
      '  classDef guarded fill:#fef3c7,stroke:#b45309,color:#451a03',
      '  class Pending guarded',
    );
  }
  return scenario(
    'state',
    lines,
    bits,
    {
      direction: selected(bits, 0) ? 'TB' : 'LR',
      aliasCount: selected(bits, 1) ? 2 : 0,
      compositeDepth: selected(bits, 2) ? 1 : 0,
      choiceCount: selected(bits, 3) ? 1 : 0,
      forkJoinCount: selected(bits, 4) ? 2 : 0,
      concurrentRegionCount: selected(bits, 5) ? 2 : 0,
      cycleCount: selected(bits, 6) ? 2 : 0,
      noteCount: selected(bits, 7) ? 1 : 0,
    },
    ['states', 'composites', 'pseudostates', 'cycles'],
    [selected(bits, 1) ? context.label(0) : 'Pending'],
  );
}

function renderEr(bits, context) {
  const direction = selected(bits, 0) ? 'TB' : 'LR';
  const rich = selected(bits, 1);
  const lines = ['erDiagram', `  direction ${direction}`];
  const entity = (id, alias, indentation = '  ') => {
    const label = selected(bits, 7) ? `${id}["${alias}"]` : id;
    lines.push(`${indentation}${label} {`);
    lines.push(`${indentation}  uuid id PK`);
    if (selected(bits, 2)) {
      lines.push(
        `${indentation}  uuid parent_id FK`,
        `${indentation}  string code UK`,
      );
    }
    if (rich) {
      lines.push(
        `${indentation}  string? description`,
        `${indentation}  datetime created_at`,
      );
    }
    lines.push(`${indentation}}`);
  };
  if (selected(bits, 6)) {
    lines.push('  subgraph MASTER_DATA', '    direction TB');
    entity('ACCOUNT', context.label(0), '    ');
    entity('RECORD', context.label(1), '    ');
    lines.push('  end', '  subgraph ACTIVITY', '    direction TB');
    entity('EVENT', context.label(2), '    ');
    entity('TAG', context.label(3), '    ');
    lines.push('  end');
  } else {
    entity('ACCOUNT', context.label(0));
    entity('RECORD', context.label(1));
    entity('EVENT', context.label(2));
    entity('TAG', context.label(3));
  }
  const relation = selected(bits, 3) ? '}o--o{' : '||--o{';
  const stroke = selected(bits, 4) ? '..' : '--';
  const effectiveRelation = relation.replace('--', stroke);
  lines.push(`  ACCOUNT ${effectiveRelation} RECORD : owns`);
  if (selected(bits, 5)) {
    lines.push(
      `  ACCOUNT ${effectiveRelation} EVENT : emits`,
      `  ACCOUNT ${effectiveRelation} TAG : classifies`,
    );
  } else {
    lines.push(
      `  RECORD ${effectiveRelation} EVENT : emits`,
      `  EVENT ${effectiveRelation} TAG : tagged`,
    );
  }
  if (selected(bits, 7)) {
    lines.push(
      '  classDef governed fill:#dbeafe,stroke:#1d4ed8,color:#172554',
      '  class ACCOUNT,RECORD governed',
    );
  }
  return scenario(
    'er',
    lines,
    bits,
    {
      direction,
      attributeCount: rich ? 12 : 4,
      keyKindCount: selected(bits, 2) ? 3 : 1,
      cardinality: selected(bits, 3) ? 'many-to-many' : 'one-to-many',
      identifying: !selected(bits, 4),
      maxOutDegree: selected(bits, 5) ? 3 : 1,
      subgraphDepth: selected(bits, 6) ? 1 : 0,
      subgraphCount: selected(bits, 6) ? 2 : 0,
      aliasCount: selected(bits, 7) ? 4 : 0,
    },
    ['entities', 'attributes', 'cardinalities', 'subgraphs'],
    [selected(bits, 7) ? context.label(0) : 'ACCOUNT'],
  );
}

function renderGantt(bits, context) {
  const config = selected(bits, 6)
    ? ['---', 'config:', '  gantt:', '    topAxis: true', '---']
    : [];
  const lines = [
    ...config,
    'gantt',
    `  title ${context.title}`,
    '  dateFormat YYYY-MM-DD',
    ...(selected(bits, 5) ? ['  axisFormat %b %d', '  tickInterval 1week'] : []),
    '  todayMarker off',
    ...(selected(bits, 4) ? ['  excludes weekends', '  weekend saturday'] : []),
    '  section Delivery',
  ];
  const taskState = selected(bits, 2)
    ? ['done', 'active', 'crit', 'active']
    : ['crit', 'crit', 'crit', 'crit'];
  const schedules = selected(bits, 1)
    ? ['2027-01-01', 'after task_1', 'after task_2', 'after task_3']
    : ['2027-01-01', '2027-01-04', '2027-01-07', '2027-01-10'];
  lines.push(
    `  ${context.label(0)} :${taskState[0]}, task_1, ${schedules[0]}, 2d`,
  );
  if (selected(bits, 0)) {
    lines.push('  section Validation');
  }
  lines.push(
    `  ${context.label(1)} :${taskState[1]}, task_2, ${schedules[1]}, 2d`,
    `  ${context.label(2)} :${taskState[2]}, task_3, ${schedules[2]}, 2d`,
  );
  if (selected(bits, 0)) {
    lines.push('  section Release');
  }
  lines.push(
    `  ${context.label(3)} :${taskState[3]}, task_4, ${schedules[3]}, 2d`,
  );
  if (selected(bits, 3)) {
    lines.push('  Approval :milestone, approval, after task_4, 0d');
  }
  if (selected(bits, 7)) {
    lines.push('  Review checkpoint :vert, checkpoint, 2027-01-08, 1d');
  }
  return scenario(
    'gantt',
    lines,
    bits,
    {
      sectionCount: selected(bits, 0) ? 3 : 1,
      dependencyCount: selected(bits, 1) ? 3 : 0,
      taskStateKindCount: selected(bits, 2) ? 4 : 1,
      milestoneCount: selected(bits, 3) ? 1 : 0,
      weekendExcluded: selected(bits, 4),
      explicitAxis: selected(bits, 5),
      topAxis: selected(bits, 6),
      verticalMarkerCount: selected(bits, 7) ? 1 : 0,
    },
    ['tasks', 'dependencies', 'calendar-controls', 'markers'],
    [context.title],
  );
}

function renderPie(bits, context) {
  const config = [];
  if (selected(bits, 4) || selected(bits, 5) || selected(bits, 6)) {
    config.push('---', 'config:', '  pie:');
    if (selected(bits, 4)) {
      config.push('    donutHole: 0.52');
    }
    config.push(`    legendPosition: ${selected(bits, 5) ? 'center' : 'right'}`);
    if (selected(bits, 6)) {
      config.push(`    highlightSlice: "${context.goal.stages[1]}"`);
    }
    config.push('---');
  }
  const sliceCount = selected(bits, 1) ? 8 : 4;
  const lines = [
    ...config,
    `pie${selected(bits, 0) ? ' showData' : ''}`,
    `  title ${context.title}`,
  ];
  for (let index = 0; index < sliceCount; index += 1) {
    const base = selected(bits, 3)
      ? index === 0 ? 72 : 4 + index
      : 10 + (index % 4) * 2;
    const value = selected(bits, 2)
      ? index === sliceCount - 1 ? 0 : base + 0.5
      : base;
    let label = context.label(index);
    if (selected(bits, 7) && index === 0) {
      label = `Quoted \\"${context.goal.stages[0]}\\"`;
    }
    if (selected(bits, 7) && index === 1) {
      label = `Quoted \\"${context.goal.stages[0]}\\"`;
    }
    lines.push(`  "${label}" : ${value}`);
  }
  return scenario(
    'pie',
    lines,
    bits,
    {
      showData: selected(bits, 0),
      sliceCount,
      decimalCount: selected(bits, 2) ? sliceCount - 1 : 0,
      zeroCount: selected(bits, 2) ? 1 : 0,
      dominantSlice: selected(bits, 3),
      donut: selected(bits, 4),
      centeredLegend: selected(bits, 5),
      highlightedSliceCount: selected(bits, 6) ? 1 : 0,
      duplicateLabelCount: selected(bits, 7) ? 1 : 0,
    },
    ['slices', 'distribution', 'donut', 'legend'],
    [context.title],
  );
}

function renderJourney(bits, context) {
  const frontmatter = selected(bits, 6)
    ? ['---', `title: ${context.title}`, '---']
    : [];
  const sectionCount = selected(bits, 0) ? 3 : 1;
  const taskCount = selected(bits, 1) ? 8 : 4;
  const actors = [
    context.domain.actor,
    context.domain.platform,
    'Specialist',
  ];
  const lines = [
    ...frontmatter,
    'journey',
    ...(!selected(bits, 6) ? [`  title ${context.title}`] : []),
  ];
  let task = 0;
  for (let section = 0; section < sectionCount; section += 1) {
    lines.push(`  section ${context.goal.stages[section % 4]}`);
    const sectionTasks = Math.ceil(taskCount / sectionCount);
    for (let local = 0; local < sectionTasks && task < taskCount; local += 1) {
      const score = selected(bits, 2)
        ? `${2 + (task % 3)}.5`
        : String(2 + (task % 4));
      let assignedActors = actors[0];
      if (selected(bits, 3)) {
        assignedActors = `${actors[task % actors.length]}, ${actors[(task + 1) % actors.length]}`;
      }
      if (selected(bits, 4)) {
        assignedActors = task % 2 === 0
          ? `${actors[2]}, ${actors[0]}`
          : `${actors[0]}, ${actors[2]}`;
      }
      if (selected(bits, 3) && !assignedActors.includes(actors[1])) {
        assignedActors = `${assignedActors}, ${actors[1]}`;
      }
      const actorSuffix = selected(bits, 5) && task === taskCount - 1
        ? ''
        : `: ${assignedActors}`;
      const label = selected(bits, 7)
        ? `${context.label(task)} with documented validation evidence`
        : context.label(task);
      lines.push(`    ${label}: ${score}${actorSuffix}`);
      task += 1;
    }
  }
  return scenario(
    'journey',
    lines,
    bits,
    {
      sectionCount,
      taskCount,
      decimalScoreCount: selected(bits, 2) ? taskCount : 0,
      actorCount: selected(bits, 3) ? 3 : 1,
      actorOrderVariants: selected(bits, 4) ? 2 : 1,
      actorlessTaskCount: selected(bits, 5) ? 1 : 0,
      frontmatterTitle: selected(bits, 6),
      longLabelCount: selected(bits, 7) ? taskCount : 0,
    },
    ['sections', 'scores', 'actors', 'text-layout'],
    [context.title],
  );
}

function renderRequirement(bits, context) {
  const lines = [
    'requirementDiagram',
    `  direction ${selected(bits, 0) ? 'TB' : 'LR'}`,
  ];
  const types = selected(bits, 1)
    ? ['requirement', 'functionalRequirement', 'performanceRequirement', 'interfaceRequirement']
    : Array(4).fill('functionalRequirement');
  for (let index = 0; index < 4; index += 1) {
    lines.push(
      `  ${types[index]} R${index + 1}${selected(bits, 7) && index === 0 ? ':::critical' : ''} {`,
      `    id: "REQ-${index + 1}"`,
      `    text: "${context.label(index)}"`,
      `    risk: ${selected(bits, 2) ? ['low', 'medium', 'high', 'medium'][index] : 'medium'}`,
      `    verifyMethod: ${selected(bits, 3) ? ['test', 'analysis', 'inspection', 'demonstration'][index] : 'test'}`,
      '  }',
    );
  }
  if (selected(bits, 4)) {
    lines.push(
      '  element implementation {',
      `    type: "${context.domain.platform}"`,
      '    docRef: "architecture/implementation"',
      '  }',
      '  element evidence {',
      '    type: "Validation suite"',
      '  }',
    );
  }
  if (selected(bits, 5)) {
    lines.push(
      '  R1 - contains -> R2',
      '  R1 - derives -> R3',
      '  R1 - traces -> R4',
    );
  } else {
    lines.push('  R1 - contains -> R2', '  R2 - contains -> R3', '  R3 - contains -> R4');
  }
  if (selected(bits, 6)) {
    lines.push('  R4 <- refines - R1', '  R3 <- copies - R2');
  }
  if (selected(bits, 4)) {
    lines.push('  implementation - satisfies -> R1', '  evidence - verifies -> R4');
  }
  if (selected(bits, 7)) {
    lines.push(
      '  classDef critical fill:#fee2e2,stroke:#b91c1c,color:#7f1d1d,stroke-width:3px',
      '  style R2 fill:#dcfce7,stroke:#15803d,color:#14532d',
    );
  }
  return scenario(
    'requirement',
    lines,
    bits,
    {
      direction: selected(bits, 0) ? 'TB' : 'LR',
      requirementTypeCount: selected(bits, 1) ? 4 : 1,
      riskKindCount: selected(bits, 2) ? 3 : 1,
      verificationKindCount: selected(bits, 3) ? 4 : 1,
      elementCount: selected(bits, 4) ? 2 : 0,
      relationshipKindCount: selected(bits, 5) ? 3 : 1,
      reverseRelationshipCount: selected(bits, 6) ? 2 : 0,
      styledNodeCount: selected(bits, 7) ? 2 : 0,
    },
    ['requirements', 'elements', 'relationships', 'styles'],
    ['REQ-1'],
  );
}

function renderGitGraph(bits, context) {
  const direction = selected(bits, 0) ? 'TB' : 'LR';
  const quoted = selected(bits, 7);
  const feature = quoted ? '"feature lane"' : 'feature';
  const second = quoted ? '"release lane"' : 'release';
  const lines = [
    `gitGraph ${direction}:`,
    `  commit id: "${context.goal.code}-root"` +
      `${selected(bits, 6) ? ' msg: "Initialize history"' : ''}`,
  ];
  if (selected(bits, 1)) {
    lines.push(
      `  branch ${feature}`,
      `  commit id: "${context.goal.code}-feature"` +
        `${selected(bits, 4) ? ' type: HIGHLIGHT' : ''}` +
        `${selected(bits, 6) ? ' msg: "Implement feature"' : ''}`,
      '  checkout main',
    );
  }
  if (selected(bits, 3)) {
    lines.push(
      `  branch ${second}`,
      `  commit id: "${context.goal.code}-release"` +
        `${selected(bits, 4) ? ' type: REVERSE' : ''}`,
      '  checkout main',
    );
  }
  if (quoted) {
    lines.push(
      '  branch "naming lane"',
      `  commit id: "${context.goal.code}-named"`,
      '  checkout main',
    );
  }
  lines.push(
    `  commit id: "${context.goal.code}-main"` +
      `${selected(bits, 5) ? ' tag: "candidate"' : ''}` +
      `${selected(bits, 6) ? ' msg: "Validate baseline"' : ''}`,
  );
  if (selected(bits, 2) && selected(bits, 1)) {
    lines.push(
      `  merge ${feature} id: "${context.goal.code}-merge"` +
        `${selected(bits, 5) ? ' tag: "approved"' : ''}` +
        `${selected(bits, 4) ? ' type: HIGHLIGHT' : ''}`,
    );
  } else if (selected(bits, 2)) {
    lines.push(
      '  branch integration',
      `  commit id: "${context.goal.code}-integration"`,
      '  checkout main',
      `  merge integration id: "${context.goal.code}-merge"`,
    );
  }
  if (selected(bits, 3) && selected(bits, 2)) {
    lines.push(`  merge ${second} id: "${context.goal.code}-release-merge"`);
  }
  lines.push(
    `  commit id: "${context.goal.code}-done"` +
      `${selected(bits, 4) ? ' type: REVERSE' : ''}` +
      `${selected(bits, 5) ? ' tag: "published"' : ''}`,
  );
  return scenario(
    'gitgraph',
    lines,
    bits,
    {
      direction,
      branchCount: 1 + Number(selected(bits, 1)) + Number(selected(bits, 3)),
      mergeCount: selected(bits, 2) ? 1 + Number(selected(bits, 3)) : 0,
      parallelBranchCount: selected(bits, 3) ? 2 : 0,
      specialCommitCount: selected(bits, 4) ? 3 : 0,
      tagCount: selected(bits, 5) ? 3 : 0,
      messageCount: selected(bits, 6) ? 3 : 0,
      quotedBranchCount: quoted
        ? Number(selected(bits, 1)) + Number(selected(bits, 3))
        : 0,
    },
    ['commits', 'branches', 'merges', 'metadata'],
    [context.goal.code],
  );
}

function renderMindmap(bits, context) {
  const layout = selected(bits, 6) ? 'tidy-tree' : 'dagre';
  const config = [
    '---',
    ...(selected(bits, 7) ? [`title: ${context.title}`] : []),
    'config:',
    `  layout: ${layout}`,
    '---',
  ];
  const root = selected(bits, 2)
    ? `root((${context.title}))`
    : `root(${context.title})`;
  const lines = [...config, 'mindmap', `  ${root}`];
  const childCount = selected(bits, 1) ? 6 : 3;
  for (let index = 0; index < childCount; index += 1) {
    let label = context.label(index);
    if (selected(bits, 4) && index === 0) {
      label = `**${context.goal.stages[0]} evidence**`;
    }
    if (selected(bits, 5) && index === 1) {
      label = `${context.goal.stages[1]} &amp; review<br/>checkpoint`;
    }
    const shapes = [
      label,
      `[${label}]`,
      `(${label})`,
      `((${label}))`,
      `)${label}(`,
      `{{${label}}}`,
    ];
    const child = selected(bits, 2) ? shapes[index % shapes.length] : label;
    const indent = selected(bits, 3) && index % 2 === 1 ? '      ' : '    ';
    lines.push(`${indent}${child}`);
    if (selected(bits, 0)) {
      lines.push(
        `${indent}  ${context.goal.stages[(index + 1) % 4]} detail`,
        `${indent}    ${context.goal.stages[(index + 2) % 4]} evidence`,
      );
    }
  }
  if (selected(bits, 7)) {
    lines.push('    %% Input-space metadata case');
  }
  return {
    ...scenario(
      'mindmap',
      lines,
      bits,
      {
        maxDepth: selected(bits, 0) ? 4 : 2,
        rootDegree: childCount,
        shapeKindCount: selected(bits, 2) ? 6 : 1,
        indentationPatternCount: selected(bits, 3) ? 2 : 1,
        markdownNodeCount: selected(bits, 4) ? 1 : 0,
        htmlEntityNodeCount: selected(bits, 5) ? 1 : 0,
        layout,
        metadataLineCount: selected(bits, 7) ? 2 : 0,
      },
      ['hierarchy', 'shapes', 'text-syntax', 'layouts'],
      [context.title],
    ),
    layout,
  };
}

function renderPacket(bits, context) {
  const config = selected(bits, 6)
    ? [
        '---',
        'config:',
        '  packet:',
        '    showBits: false',
        '---',
      ]
    : [];
  const fieldCount = selected(bits, 1) ? 8 : 4;
  const lines = [
    ...config,
    selected(bits, 0) ? 'packet-beta' : 'packet',
    ...(selected(bits, 7)
      ? [`  %% ${context.domain.name} frame`, `  title ${context.title}`]
      : []),
  ];
  let offset = 0;
  for (let index = 0; index < fieldCount; index += 1) {
    let width = selected(bits, 2) ? [2, 3, 8, 16, 5, 31, 6, 62][index] : 4;
    if (selected(bits, 3) && index === 0) {
      width = 1;
    }
    let label = context.label(index);
    if (selected(bits, 7) && index === 0) {
      label = `${context.goal.stages[0]} \\"flag\\"`;
    }
    if (selected(bits, 4) && index % 2 === 1) {
      lines.push(`  +${width}: "${label}"`);
    } else if (width === 1) {
      lines.push(`  ${offset}: "${label}"`);
    } else {
      lines.push(`  ${offset}-${offset + width - 1}: "${label}"`);
    }
    offset += width;
  }
  if (selected(bits, 5)) {
    lines.push(`  +${96 - (offset % 32)}: "Cross-row extension"`);
  }
  return scenario(
    'packet',
    lines,
    bits,
    {
      header: selected(bits, 0) ? 'packet-beta' : 'packet',
      fieldCount: fieldCount + Number(selected(bits, 5)),
      fieldWidthKindCount: selected(bits, 2) ? fieldCount : 1,
      singleBitFieldCount: selected(bits, 3) ? 1 : 0,
      countedFieldCount: selected(bits, 4) ? Math.floor(fieldCount / 2) : 0,
      rowCount: selected(bits, 5) ? Math.ceil((offset + 96) / 32) : Math.ceil(offset / 32),
      showBits: !selected(bits, 6),
      metadataLineCount: selected(bits, 7) ? 2 : 0,
    },
    ['fields', 'addressing', 'row-splitting', 'configuration'],
    [context.goal.stages[0]],
  );
}

function renderRadar(bits, context) {
  const axisCount = selected(bits, 0) ? 8 : 4;
  const axes = Array.from(
    { length: axisCount },
    (_, index) => `a${index + 1}["${context.goal.stages[index % 4]} ${index + 1}"]`,
  );
  const lines = [
    'radar-beta',
    `  title ${context.title}`,
    `  axis ${axes.join(', ')}`,
  ];
  const curveCount = selected(bits, 2) ? 3 : 1;
  for (let curve = 0; curve < curveCount; curve += 1) {
    const order = Array.from({ length: axisCount }, (_, index) =>
      selected(bits, 7) ? axisCount - index - 1 : index
    );
    const entries = order.map((axisIndex) => {
      const value = 35 + ((axisIndex * 11 + curve * 9) % 55);
      return selected(bits, 1) ? `a${axisIndex + 1}: ${value}` : String(value);
    });
    lines.push(
      `  curve c${curve + 1}["Profile ${curve + 1}"] { ${entries.join(', ')} }`,
    );
  }
  if (selected(bits, 3)) {
    lines.push('  min 0', '  max 100');
  }
  lines.push(
    `  graticule ${selected(bits, 4) ? 'polygon' : 'circle'}`,
    `  showLegend ${selected(bits, 5) ? 'false' : 'true'}`,
    `  ticks ${selected(bits, 6) ? 8 : 4}`,
  );
  return scenario(
    'radar',
    lines,
    bits,
    {
      axisCount,
      detailedEntryCount: selected(bits, 1) ? axisCount * curveCount : 0,
      curveCount,
      explicitRange: selected(bits, 3),
      graticule: selected(bits, 4) ? 'polygon' : 'circle',
      legendVisible: !selected(bits, 5),
      tickCount: selected(bits, 6) ? 8 : 4,
      reversedReferenceOrder: selected(bits, 7),
    },
    ['axes', 'curves', 'scale', 'graticule'],
    [context.title],
  );
}

function renderSankey(bits, context) {
  const config = [];
  if (selected(bits, 5) || selected(bits, 6) || selected(bits, 7)) {
    config.push('---', 'config:', '  sankey:');
    config.push(`    nodeAlignment: ${selected(bits, 5) ? 'left' : 'justify'}`);
    config.push(`    linkColor: ${selected(bits, 6) ? 'gradient' : 'source'}`);
    config.push(`    showValues: ${selected(bits, 7)}`);
    if (selected(bits, 7)) {
      config.push('    prefix: "$"', '    suffix: "k"');
    }
    config.push('---');
  }
  const lines = [
    ...config,
    selected(bits, 0) ? 'sankey-beta' : 'sankey',
  ];
  const source = selected(bits, 4)
    ? `"${context.goal.stages[0]}, intake"`
    : `${context.goal.stages[0]} intake`;
  const review = selected(bits, 4)
    ? `"${context.goal.stages[1]} ""reviewed"""`
    : `${context.goal.stages[1]} review`;
  lines.push(`${source},${review},60`);
  if (selected(bits, 1)) {
    lines.push(`${source},Escalation,25`, `${source},Rejected,5`);
  }
  if (selected(bits, 3)) {
    lines.push(`${review},Validation,45`, 'Validation,Delivery,42');
  } else {
    lines.push(`${review},Delivery,45`);
  }
  if (selected(bits, 2)) {
    lines.push('Escalation,Delivery,20', 'Manual,Delivery,8');
  }
  return scenario(
    'sankey',
    lines,
    bits,
    {
      header: selected(bits, 0) ? 'sankey-beta' : 'sankey',
      sourceOutDegree: selected(bits, 1) ? 3 : 1,
      deliveryInDegree: 1 + Number(selected(bits, 2)) * 2,
      stageCount: selected(bits, 3) ? 4 : 3,
      quotedRecordCount: selected(bits, 4) ? 1 : 0,
      alignment: selected(bits, 5) ? 'left' : 'justify',
      linkColor: selected(bits, 6) ? 'gradient' : 'source',
      showValues: selected(bits, 7),
    },
    ['flows', 'branching', 'merging', 'csv-syntax'],
    [context.goal.stages[0]],
  );
}

function renderTreemap(bits, context) {
  const config = [];
  if (selected(bits, 6) || selected(bits, 7)) {
    config.push('---', 'config:', '  treemap:');
    config.push(`    showValues: ${selected(bits, 6)}`);
    config.push(`    useMaxWidth: ${!selected(bits, 7)}`);
    if (selected(bits, 7)) {
      config.push('    padding: 8', '    diagramPadding: 18');
    }
    if (selected(bits, 6)) {
      config.push('    valueFormat: "$0,0"');
    }
    config.push('---');
  }
  const leafCount = selected(bits, 3) ? 8 : 4;
  const lines = [
    ...config,
    selected(bits, 0) ? 'treemap-beta' : 'treemap',
    `title ${context.title}`,
    '"Main"',
  ];
  if (selected(bits, 2)) {
    lines.push('    "Primary"');
  }
  for (let index = 0; index < leafCount; index += 1) {
    if (selected(bits, 1) && index === Math.ceil(leafCount / 2)) {
      lines.push('"Alternate"');
    }
    const indent = selected(bits, 2) && (!selected(bits, 1) || index < leafCount / 2)
      ? '        '
      : '    ';
    const separator = selected(bits, 4) ? ',' : ':';
    const classSuffix = selected(bits, 5) && index % 2 === 0 ? ':::priority' : '';
    lines.push(
      `${indent}"${context.goal.stages[index % 4]} ${index + 1}"` +
        `${separator} ${20 + index * 7}${classSuffix}`,
    );
  }
  if (selected(bits, 5)) {
    lines.push(
      'classDef priority fill:#fee2e2,stroke:#dc2626,' +
        'stroke-width:3px,color:#7f1d1d;',
    );
  }
  return scenario(
    'treemap',
    lines,
    bits,
    {
      header: selected(bits, 0) ? 'treemap-beta' : 'treemap',
      rootCount: selected(bits, 1) ? 2 : 1,
      maxDepth: selected(bits, 2) ? 3 : 2,
      leafCount,
      valueSyntax: selected(bits, 4) ? 'comma' : 'colon',
      styledLeafCount: selected(bits, 5) ? Math.ceil(leafCount / 2) : 0,
      showValues: selected(bits, 6),
      intrinsicSizing: selected(bits, 7),
    },
    ['hierarchy', 'values', 'classes', 'sizing'],
    [context.title],
  );
}

function renderVenn(bits, context) {
  const config = selected(bits, 7)
    ? [
        '---',
        'config:',
        '  venn:',
        '    width: 760',
        '    height: 460',
        '    padding: 24',
        '    useMaxWidth: false',
        '    useDebugLayout: true',
        '---',
      ]
    : [];
  const setCount = selected(bits, 0) ? 4 : 3;
  const lines = [...config, 'venn-beta', `  title ${context.title}`];
  const ids = ['A', 'B', 'C', 'D'];
  for (let index = 0; index < setCount; index += 1) {
    lines.push(
      `  set ${ids[index]}["${context.goal.stages[index % 4]}"]` +
        `${selected(bits, 1) ? `:${24 + index * 3}` : ''}`,
    );
  }
  if (selected(bits, 2)) {
    lines.push(
      `  union A,B${selected(bits, 4) ? '["Shared decision"]' : ''}:5`,
      `  union B,C${selected(bits, 4) ? '["Shared evidence"]' : ''}:4`,
    );
  }
  if (selected(bits, 3)) {
    lines.push(
      `  union A,B,C${selected(bits, 4) ? '["Validated outcome"]' : ''}:2`,
    );
  }
  if (selected(bits, 4)) {
    lines.push('text A,B "Shared decision area"');
  }
  if (selected(bits, 5)) {
    lines.push(
      '  style A fill:#ef4444,stroke:#7f1d1d,stroke-width:3px,fill-opacity:0.2',
      '  style B fill:rgb(59, 130, 246),stroke:#1e3a8a,fill-opacity:0.18',
    );
  }
  if (selected(bits, 6)) {
    lines.push(
      '  style A,B fill:rgba(34, 197, 94, 0.35),color:#052e16',
      ...(selected(bits, 3)
        ? ['  style A,B,C fill:#fef3c7,stroke:#b45309']
        : []),
    );
  }
  return scenario(
    'venn',
    lines,
    bits,
    {
      setCount,
      explicitSizeCount: selected(bits, 1) ? setCount : 0,
      pairwiseIntersectionCount: selected(bits, 2) ? 2 : 0,
      multiSetIntersectionCount: selected(bits, 3) ? 1 : 0,
      labelledIntersectionCount:
        selected(bits, 4)
          ? (selected(bits, 2) ? 2 : 0) + Number(selected(bits, 3))
          : 0,
      explicitIntersectionTextCount: selected(bits, 4) ? 1 : 0,
      styledSetCount: selected(bits, 5) ? 2 : 0,
      styledIntersectionCount: selected(bits, 6) ? 1 + Number(selected(bits, 3)) : 0,
      debugLayout: selected(bits, 7),
    },
    ['sets', 'intersections', 'styles', 'sizing'],
    [context.title],
  );
}

function renderIshikawa(bits, context) {
  const causeCount = selected(bits, 1) ? 6 : 3;
  const lines = [
    selected(bits, 0) ? 'ishikawa-beta' : 'ishikawa',
    selected(bits, 7)
      ? `${context.title} with end to end production verification evidence`
      : context.title,
  ];
  for (let index = 0; index < causeCount; index += 1) {
    const baseIndent = selected(bits, 5) && index % 2 === 1 ? '   ' : '  ';
    const label = selected(bits, 6)
      ? `${context.goal.stages[index % 4]} &amp; evidence<br/>review`
      : context.label(index);
    lines.push(`${baseIndent}${label}`);
    if (selected(bits, 2)) {
      lines.push(`${baseIndent}  Contributing factor ${index + 1}`);
      if (selected(bits, 3) && index === 0) {
        lines.push(
          `${baseIndent}    Detailed cause`,
          `${baseIndent}      Root condition`,
        );
      }
    }
  }
  if (selected(bits, 3)) {
    lines.push(
      '  Deep investigation',
      '    Contributing system',
      '      Failing subsystem',
      '        Root condition',
    );
  }
  if (selected(bits, 4)) {
    lines.push('  Additional upper cause', '  Additional lower cause');
  }
  return scenario(
    'ishikawa',
    lines,
    bits,
    {
      header: selected(bits, 0) ? 'ishikawa-beta' : 'ishikawa',
      topLevelCauseCount: causeCount + (selected(bits, 4) ? 2 : 0),
      nestedCauseCount: selected(bits, 2) ? causeCount : 0,
      maxDepth: selected(bits, 3) ? 4 : selected(bits, 2) ? 2 : 1,
      alternatingPairCount: selected(bits, 4) ? Math.ceil((causeCount + 2) / 2) : 0,
      indentationWidthKindCount: selected(bits, 5) ? 2 : 1,
      encodedLabelCount: selected(bits, 6) ? causeCount : 0,
      longLabelCount: selected(bits, 7) ? 1 : 0,
    },
    ['causes', 'hierarchy', 'indentation', 'text-layout'],
    [context.title],
  );
}

function renderCynefin(bits, context) {
  const config = [];
  if (selected(bits, 5) || selected(bits, 6) || selected(bits, 7)) {
    config.push('---', 'config:', '  cynefin:');
    config.push(`    showDomainDescriptions: ${selected(bits, 5)}`);
    config.push(`    boundaryAmplitude: ${selected(bits, 6) ? 0 : 16}`);
    config.push(`    useMaxWidth: ${!selected(bits, 7)}`);
    if (selected(bits, 7)) {
      config.push('    width: 700', '    height: 500', '    padding: 20');
    }
    config.push('    seed: 42', '---');
  }
  const domains = ['complex', 'complicated', 'clear', 'chaotic'];
  const itemCount = selected(bits, 0) ? 3 : 1;
  const lines = [...config, 'cynefin-beta', `  title ${context.title}`];
  domains.forEach((domain, domainIndex) => {
    lines.push(`  ${domain}`);
    if (selected(bits, 1) && domain === 'chaotic') {
      return;
    }
    for (let item = 0; item < itemCount; item += 1) {
      lines.push(
        `    "${context.goal.stages[(domainIndex + item) % 4]} ` +
          `${domainIndex + 1}.${item + 1}"`,
      );
    }
  });
  lines.push('  confusion');
  const confusionCount = selected(bits, 4) ? 5 : 1;
  for (let index = 0; index < confusionCount; index += 1) {
    lines.push(`    "Unknown ${index + 1}"`);
  }
  lines.push(
    selected(bits, 3)
      ? '  confusion --> complex : "classified"'
      : '  confusion --> complex',
  );
  if (selected(bits, 2)) {
    lines.push(
      selected(bits, 3)
        ? '  complex --> complicated : "pattern identified"'
        : '  complex --> complicated',
      selected(bits, 3)
        ? '  complicated --> clear : "practice codified"'
        : '  complicated --> clear',
      selected(bits, 3)
        ? '  chaotic --> complex : "stabilized"'
        : '  chaotic --> complex',
    );
  }
  return scenario(
    'cynefin',
    lines,
    bits,
    {
      itemCount: itemCount * (4 - Number(selected(bits, 1))) + confusionCount,
      emptyDomainCount: selected(bits, 1) ? 1 : 0,
      transitionCount: 1 + (selected(bits, 2) ? 3 : 0),
      labelledTransitionCount:
        selected(bits, 3) ? 1 + (selected(bits, 2) ? 3 : 0) : 0,
      confusionItemCount: confusionCount,
      descriptionsVisible: selected(bits, 5),
      boundaryAmplitude: selected(bits, 6) ? 0 : 16,
      intrinsicSizing: selected(bits, 7),
    },
    ['domains', 'transitions', 'confusion', 'configuration'],
    [context.title],
  );
}

function renderBlock(bits, context) {
  const lines = ['block'];
  lines.push(`  columns ${selected(bits, 0) ? 4 : 'auto'}`);
  if (selected(bits, 3)) {
    lines.push(
      '  block:platform',
      '    columns 2',
      `    A["${context.label(0)}"]`,
      '    block:runtime',
      `      B["${context.label(1)}"]`,
      '    end',
      '  end',
    );
  } else {
    lines.push(
      `  A["${context.label(0)}"]${selected(bits, 1) ? ':2' : ''}`,
      `  B["${context.label(1)}"]`,
    );
  }
  if (selected(bits, 2)) {
    lines.push('  space', '  space:2');
  }
  if (selected(bits, 1)) {
    lines.push('  Span["Wide stage"]:2');
  }
  lines.push(
    `  C["${context.label(2)}"]`,
    `  D["${context.label(3)}"]`,
  );
  if (selected(bits, 4)) {
    lines.push(
      '  Round("Rounded stage")',
      '  Database[("Stored state")]',
      '  Decision{"Decision point"}',
    );
  }
  if (selected(bits, 5)) {
    lines.push('  A --> B', '  A --> C', '  B --> D', '  C --> D');
  } else {
    lines.push('  A --> B', '  B --> C', '  C --> D');
  }
  if (selected(bits, 6)) {
    lines.push(
      '  A -- "accepted" ==> C',
      '  B -.-> D',
      '  C --o A',
    );
  }
  if (selected(bits, 7)) {
    lines.push(
      '  classDef active fill:#dcfce7,stroke:#15803d,color:#14532d;',
      '  class A,B active',
      '  style C fill:#dbeafe,stroke:#1d4ed8,stroke-width:3px',
    );
  }
  return scenario(
    'block',
    lines,
    bits,
    {
      columnMode: selected(bits, 0) ? 'explicit' : 'auto',
      spannedBlockCount: selected(bits, 1) ? 1 : 0,
      emptyCellCount: selected(bits, 2) ? 3 : 0,
      compositeDepth: selected(bits, 3) ? 2 : 0,
      shapeKindCount: selected(bits, 4) ? 3 : 1,
      maxOutDegree: selected(bits, 5) ? 2 : 1,
      specialEdgeKindCount: selected(bits, 6) ? 3 : 0,
      styledBlockCount: selected(bits, 7) ? 3 : 0,
    },
    ['grid', 'composites', 'shapes', 'edges'],
    [context.label(0)],
  );
}

function renderEventModeling(bits, context) {
  const relaxed = selected(bits, 0);
  const frameCount = selected(bits, 1) ? 8 : 4;
  const frameKeyword = relaxed ? 'timeframe' : 'tf';
  const resetKeyword = relaxed ? 'resetframe' : 'rf';
  const types = relaxed
    ? ['ui', 'command', 'event', 'readmodel', 'processor']
    : ['ui', 'cmd', 'evt', 'rmo', 'pcr'];
  const lines = ['eventmodeling'];
  for (let index = 0; index < frameCount; index += 1) {
    const keyword = selected(bits, 2) && index === 0 ? resetKeyword : frameKeyword;
    const type = types[index % types.length];
    const namespace = selected(bits, 4) ? `Domain${index % 2}.` : '';
    const id = `${namespace}${identifierToken(context.goal.stages[index % 4])}${index + 1}`;
    const relations = [];
    if (selected(bits, 5) && index > 0) {
      relations.push(`->> ${String(index).padStart(2, '0')}`);
    }
    if (selected(bits, 6) && index > 1) {
      relations.push(`->> ${String(index - 1).padStart(2, '0')}`);
    }
    const data = selected(bits, 7)
      ? ` { object: ${context.domain.code}, step: ${index + 1} }`
      : '';
    lines.push(
      `  ${keyword} ${String(index + 1).padStart(2, '0')} ${type} ` +
        `${id}${relations.length ? ` ${relations.join(' ')}` : ''}${data}`,
    );
  }
  if (selected(bits, 3)) {
    lines.push(
      `  ${frameKeyword} ${String(frameCount + 1).padStart(2, '0')} ` +
        `${relaxed ? 'event' : 'evt'} AlternateEvent`,
    );
  }
  return scenario(
    'eventmodeling',
    lines,
    bits,
    {
      syntax: relaxed ? 'relaxed' : 'compact',
      frameCount: frameCount + Number(selected(bits, 3)),
      resetFrameCount: selected(bits, 2) ? 1 : 0,
      entityTypeCount: selected(bits, 3) ? 6 : 5,
      namespaceCount: selected(bits, 4) ? 2 : 0,
      explicitRelationCount:
        (selected(bits, 5) ? frameCount - 1 : 0) +
        (selected(bits, 6) ? frameCount - 2 : 0),
      maxRelationCount: selected(bits, 6) ? 2 : selected(bits, 5) ? 1 : 0,
      inlineDataCount: selected(bits, 7) ? frameCount : 0,
    },
    ['frames', 'entity-types', 'swimlanes', 'relations'],
    [context.goal.stages[0]],
  );
}

function renderAgentflow(bits, context) {
  const direction = selected(bits, 0) ? 'TD' : 'LR';
  const lines = [
    '---',
    'config:',
    '  layout: dagre',
    '---',
    `agentflow-beta ${direction}`,
  ];
  if (selected(bits, 7)) {
    lines.push(
      '  connector tools["Tool API"]',
      '  tools@{ protocol: "http", endpoint: "https://example.com/tools" }',
    );
  }
  const openContainer = () => {
    if (selected(bits, 5)) {
      lines.push(`  flow team["${context.domain.platform} Team"]`);
    }
    if (selected(bits, 6)) {
      lines.push(`${selected(bits, 5) ? '  ' : ''}  flow specialist["Specialist Agent"]`);
    }
  };
  const closeContainer = () => {
    if (selected(bits, 6)) {
      lines.push(`${selected(bits, 5) ? '  ' : ''}  end`);
    }
    if (selected(bits, 5)) {
      lines.push('  end');
    }
  };
  openContainer();
  const indent = selected(bits, 5) && selected(bits, 6)
    ? '      '
    : selected(bits, 5) || selected(bits, 6)
      ? '    '
      : '  ';
  lines.push(
    `${indent}A["${context.label(0)}"]@{ shape: input }`,
    `${indent}B["${context.label(1)}"]@{ shape: ${selected(bits, 1) ? 'decision' : 'task'} }`,
    `${indent}C["${context.label(2)}"]@{ shape: ${selected(bits, 1) ? 'tool' : 'task'}` +
      `${selected(bits, 7) ? ', connectorRef: "tools.run", retry: 2' : ''} }`,
    `${indent}D["${context.label(3)}"]@{ shape: ${selected(bits, 1) ? 'action' : 'task'} }`,
  );
  closeContainer();
  lines.push('  A --> B');
  if (selected(bits, 2)) {
    lines.push('  B --> C & D');
  } else {
    lines.push('  B --> C', '  C --> D');
  }
  if (selected(bits, 3)) {
    lines.push('  B -- approved --> D', '  C -.- D');
  }
  if (selected(bits, 4)) {
    lines.push('  B --x A', '  D --x B');
  }
  return scenario(
    'agentflow',
    lines,
    bits,
    {
      direction,
      shapeKindCount: selected(bits, 1) ? 4 : 2,
      maxOutDegree: selected(bits, 2) ? 2 : 1,
      labelledEdgeCount: selected(bits, 3) ? 1 : 0,
      failureEdgeCount: selected(bits, 4) ? 2 : 0,
      flowDepth: Number(selected(bits, 5)) + Number(selected(bits, 6)),
      nestedFlowCount: selected(bits, 6) ? 1 : 0,
      connectorCount: selected(bits, 7) ? 1 : 0,
    },
    ['typed-nodes', 'branching', 'flows', 'connectors'],
    [context.label(0)],
  );
}

function renderUseCase(bits, context) {
  const lines = [
    'usecase-beta',
    `direction ${selected(bits, 0) ? 'TB' : 'LR'}`,
    `actor Primary("${context.domain.actor}")`,
  ];
  if (selected(bits, 1) || selected(bits, 6)) {
    lines.push(
      'actor Reviewer("Specialist")@{ type: hollow }',
    );
  }
  if (selected(bits, 1)) {
    lines.push('actor Operator("Operator")@{ type: awesome }');
  }
  lines.push(
    `systemBoundary platform["${context.domain.platform}"]`,
    `  U1("${context.label(0)}")`,
    selected(bits, 2)
      ? `  U2[${context.label(1)}]`
      : `  U2("${context.label(1)}")`,
  );
  if (!selected(bits, 3)) {
    lines.push(`  U3("${context.label(2)}")`);
  }
  lines.push(`  U4("${context.label(3)}")`, 'end');
  if (selected(bits, 3)) {
    lines.push(
      'systemBoundary package["Specialized package"]@{ type: package }',
      `  U3("${context.label(2)}")`,
      'end',
    );
  }
  lines.push('Primary --> U1', 'U1 --> U2');
  if (selected(bits, 4)) {
    lines.push('U2 ..> : include U3');
  } else {
    lines.push('U2 --> U3');
  }
  if (selected(bits, 5)) {
    lines.push('U4 ..> : extend U2');
  } else {
    lines.push('U3 --> U4');
  }
  if (selected(bits, 6)) {
    lines.push('Reviewer --|> Primary', 'U4 --|> U1');
  }
  if (selected(bits, 1)) {
    lines.push('Reviewer --> U3', 'Operator --> U4');
  }
  if (selected(bits, 7)) {
    lines.push(
      'note for U2 "Requires validated evidence"',
      'classDef emphasis stroke:#d33,stroke-width:3px',
      'class U1,U2 emphasis',
      'style U3 stroke:#06c,stroke-width:4px',
    );
  }
  return scenario(
    'usecase',
    lines,
    bits,
    {
      direction: selected(bits, 0) ? 'TB' : 'LR',
      actorCount: 1 + (selected(bits, 1) ? 2 : 0) +
        (selected(bits, 6) && !selected(bits, 1) ? 1 : 0),
      rectangularUseCaseCount: selected(bits, 2) ? 1 : 0,
      boundaryDepth: selected(bits, 3) ? 2 : 1,
      includeCount: selected(bits, 4) ? 1 : 0,
      extendCount: selected(bits, 5) ? 1 : 0,
      generalizationCount: selected(bits, 6) ? 2 : 0,
      styledElementCount: selected(bits, 7) ? 3 : 0,
    },
    ['actors', 'use-cases', 'boundaries', 'dependencies'],
    [context.domain.actor, context.label(0)],
  );
}

function renderWardley(bits, context) {
  const componentCount = selected(bits, 0) ? 8 : 4;
  const lines = [
    'wardley-beta',
    `title ${context.title}`,
    `anchor "${context.domain.actor}" [0.95, 0.90]`,
  ];
  for (let index = 0; index < componentCount; index += 1) {
    const strategy = selected(bits, 2)
      ? ` (${['build', 'buy', 'outsource', 'market'][index % 4]})`
      : '';
    lines.push(
      `component "Capability ${index + 1}" ` +
        `[${(0.84 - index * 0.07).toFixed(2)}, ${(0.25 + (index % 4) * 0.18).toFixed(2)}]` +
        `${strategy}`,
    );
  }
  if (selected(bits, 1)) {
    lines.push('component "Legacy constraint" [0.38, 0.32] (inertia)');
  }
  lines.push(`"${context.domain.actor}" -> "Capability 1"`);
  if (selected(bits, 3)) {
    lines.push(
      '"Capability 1" -> "Capability 2"',
      '"Capability 1" -> "Capability 3"',
      '"Capability 2" -> "Capability 4"',
      '"Capability 3" -> "Capability 4"',
    );
  } else {
    for (let index = 1; index < Math.min(componentCount, 5); index += 1) {
      lines.push(`"Capability ${index}" -> "Capability ${index + 1}"`);
    }
  }
  if (selected(bits, 4)) {
    lines.push(
      '"Capability 2" -.-> "Capability 3"',
      `"Capability 3" +'validates'> "Capability 4"`,
    );
  }
  if (selected(bits, 5)) {
    lines.push('"Capability 1" +> "Capability 4"', '"Capability 2" +<> "Capability 3"');
  }
  if (selected(bits, 6)) {
    lines.push(
      'pipeline "Capability 4" {',
      '  component "Manual option" [0.30]',
      '  component "Managed option" [0.78]',
      '}',
    );
  }
  if (selected(bits, 7)) {
    lines.push(
      'note "Validate the strategic assumption" [0.46, 0.52]',
      'annotations [0.12, 0.88]',
      'annotation 1,[0.34, 0.27] "Constraint"',
      'accelerator "Investment" [0.24, 0.84]',
      'deaccelerator "Migration cost" [0.44, 0.26]',
    );
  }
  return scenario(
    'wardley',
    lines,
    bits,
    {
      componentCount,
      inertiaCount: selected(bits, 1) ? 1 : 0,
      sourcingKindCount: selected(bits, 2) ? 4 : 0,
      maxOutDegree: selected(bits, 3) ? 2 : 1,
      styledLinkCount: selected(bits, 4) ? 2 : 0,
      flowLinkCount: selected(bits, 5) ? 2 : 0,
      pipelineComponentCount: selected(bits, 6) ? 2 : 0,
      annotationCount: selected(bits, 7) ? 4 : 0,
    },
    ['components', 'dependencies', 'pipelines', 'strategic-annotations'],
    [context.title],
  );
}

function renderZenUml(bits, context) {
  const lines = ['zenuml', `title ${context.title}`];
  if (selected(bits, 1)) {
    lines.push(
      `@Actor ${selected(bits, 0) ? `User as "${context.domain.actor}"` : 'User'}`,
      `group "${context.domain.platform}" {`,
      '  @EC2 Gateway',
      '  @Database Store',
      '}',
    );
  } else {
    lines.push(
      selected(bits, 0) ? `User as "${context.domain.actor}"` : 'User',
      selected(bits, 0) ? `Gateway as "${context.domain.platform}"` : 'Gateway',
      'Store',
    );
  }
  if (selected(bits, 4)) {
    lines.push('worker = new Worker(region="primary")');
  }
  if (selected(bits, 5)) {
    lines.push(
      'result = Gateway.submit(request) {',
      '  stored = Store.save(request)',
      '  return stored',
      '}',
    );
  } else if (selected(bits, 3)) {
    lines.push(
      'Gateway.submit(request) {',
      '  Store.validate(request)',
      '}',
    );
  } else {
    lines.push(
      selected(bits, 2)
        ? `User->Gateway: ${context.label(0)}`
        : `Gateway.submit(${identifierToken(context.goal.stages[0])})`,
    );
  }
  lines.push(
    selected(bits, 2)
      ? `User->Gateway: ${context.label(1)}`
      : `Gateway.verify(${identifierToken(context.goal.stages[1])})`,
  );
  if (selected(bits, 3)) {
    lines.push(
      'Gateway.process(request) {',
      '  Store.inspect(request)',
      '}',
    );
  }
  if (selected(bits, 6)) {
    lines.push(
      'if(accepted) {',
      '  Gateway->User: Accepted',
      '} else {',
      '  Gateway->User: Rejected',
      '}',
    );
  }
  if (selected(bits, 7)) {
    lines.push(
      'loop(each_item) {',
      '  Store.persist()',
      '}',
      'par {',
      '  Gateway->User: Notify',
      '  Gateway->Store: Audit',
      '}',
    );
  }
  return scenario(
    'zenuml',
    lines,
    bits,
    {
      aliasCount: selected(bits, 0) ? 2 : 0,
      participantGroupCount: selected(bits, 1) ? 1 : 0,
      asyncMessageCount: selected(bits, 2) ? 1 : 0,
      nestedCallDepth: selected(bits, 3) ? 2 : 1,
      createdParticipantCount: selected(bits, 4) ? 1 : 0,
      assignmentReturnCount: selected(bits, 5) ? 3 : 0,
      conditionalBranchCount: selected(bits, 6) ? 2 : 0,
      concurrentBranchCount: selected(bits, 7) ? 2 : 0,
    },
    ['participants', 'messages', 'control-regions', 'returns'],
    [context.title],
  );
}
