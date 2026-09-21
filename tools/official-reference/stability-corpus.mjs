// Independent stable corpus. These cases are intentionally not part
// of the demo gallery: they model larger workflows and combined syntax that is
// closer to application documentation than isolated feature examples.
export const cases = [
  {
    id: 'rc_flow_checkout_saga',
    kind: 'flowchart',
    title: 'Checkout saga with compensation',
    scenario: 'Commerce checkout coordinating inventory, payment, shipment, and rollback paths.',
    layout: 'dagre',
    aspectRatio: 1.65,
    source: String.raw`
flowchart LR
  Customer([Customer]) --> Cart[Review cart]
  Cart --> Validate{Cart valid?}
  Validate -- No --> Repair[Repair quantities] --> Cart
  Validate -- Yes --> Quote[Price and tax quote]
  subgraph Orchestration[Checkout orchestration]
    direction TB
    Quote --> Reserve[Reserve inventory]
    Reserve --> Inventory{Reserved?}
    Inventory -- No --> Release[Release reservations]
    Inventory -- Yes --> Authorize[Authorize payment]
    Authorize --> Payment{Authorized?}
    Payment -- No --> Release
    Payment -- Yes --> CreateOrder[Create order]
    CreateOrder --> Arrange[Arrange shipment]
    Arrange --> Shipment{Carrier accepted?}
    Shipment -- No --> Refund[Void payment]
    Refund --> Release
  end
  Shipment -- Yes --> Confirm[Send confirmation]
  Confirm --> Complete([Order accepted])
  Release --> Failed([Checkout failed])
`,
  },
  {
    id: 'rc_flow_multi_region_failover',
    kind: 'flowchart',
    title: 'Multi-region failover',
    scenario: 'Traffic management and data recovery during a regional outage.',
    layout: 'dagre',
    aspectRatio: 1.7,
    source: String.raw`
flowchart TB
  User([Client request]) --> DNS[Global traffic manager]
  DNS --> Health{Primary healthy?}
  subgraph Primary[Primary region]
    direction LR
    EdgeA[Edge gateway] --> ApiA[API pool]
    ApiA --> CacheA[(Regional cache)]
    ApiA --> DbA[(Primary database)]
    DbA --> Stream[Replication stream]
  end
  subgraph Secondary[Secondary region]
    direction LR
    EdgeB[Standby gateway] --> ApiB[Warm API pool]
    ApiB --> CacheB[(Regional cache)]
    ApiB --> DbB[(Replica database)]
  end
  Health -- Yes --> EdgeA
  Health -- No --> Promote{Replica caught up?}
  Stream --> DbB
  Promote -- Yes --> Switch[Promote replica and shift DNS] --> EdgeB
  Promote -- No --> Degraded[Serve read-only mode] --> EdgeB
  EdgeA --> Response([Response])
  EdgeB --> Response
  Metrics[Health probes] --> Health
  Response --> Metrics
`,
  },
  {
    id: 'rc_flow_release_train',
    kind: 'flowchart',
    title: 'Release train and rollback',
    scenario: 'A guarded mobile release with parallel validation and staged rollout.',
    layout: 'dagre',
    aspectRatio: 1.8,
    source: String.raw`
flowchart LR
  Commit([Merge to main]) --> Build[Reproducible build]
  Build --> Unit{Unit tests pass?}
  Unit -- No --> Fix[Return to owner] --> Commit
  Unit -- Yes --> Parallel{Start validation}
  Parallel --> Android[Android device matrix]
  Parallel --> IOS[iOS device matrix]
  Parallel --> Web[Web browser matrix]
  Android --> Gate{All platforms green?}
  IOS --> Gate
  Web --> Gate
  Gate -- No --> Fix
  Gate -- Yes --> Sign[Sign release artifacts]
  Sign --> Canary[Deploy 5 percent canary]
  Canary --> Observe{SLOs healthy?}
  Observe -- No --> Rollback[Rollback and freeze]
  Rollback --> Incident[Open incident review]
  Observe -- Yes --> Half[Deploy 50 percent]
  Half --> ObserveHalf{Error budget healthy?}
  ObserveHalf -- No --> Rollback
  ObserveHalf -- Yes --> Global[Global rollout]
  Global --> Done([Release complete])
`,
  },
  {
    id: 'rc_flow_incident_response',
    kind: 'flowchart',
    title: 'Production incident response',
    scenario: 'Incident triage, mitigation, communication, and follow-up ownership.',
    layout: 'dagre',
    aspectRatio: 1.55,
    source: String.raw`
flowchart TB
  Alert([Alert or customer report]) --> Confirm[Confirm impact]
  Confirm --> Severity{Severity}
  Severity -- SEV-1 --> Page[Page incident commander]
  Severity -- SEV-2 --> Assign[Assign service owner]
  Severity -- SEV-3 --> Ticket[Create backlog ticket]
  Page --> WarRoom[Open response channel]
  Assign --> WarRoom
  WarRoom --> Parallel{Parallel response}
  Parallel --> Diagnose[Inspect metrics, logs, and traces]
  Parallel --> Communicate[Publish status update]
  Parallel --> Preserve[Preserve evidence]
  Diagnose --> Mitigation{Safe mitigation known?}
  Mitigation -- Yes --> Apply[Apply rollback or traffic shift]
  Mitigation -- No --> Contain[Disable affected feature]
  Apply --> Verify{SLO recovered?}
  Contain --> Verify
  Verify -- No --> Diagnose
  Verify -- Yes --> Resolve[Resolve incident]
  Communicate --> Resolve
  Preserve --> Resolve
  Resolve --> Review[Blameless review]
  Review --> Actions[Owners and due dates]
  Ticket --> Actions
  Actions --> Closed([Closed])
`,
  },
  {
    id: 'rc_flow_event_ingestion',
    kind: 'flowchart',
    title: 'Event ingestion and quarantine',
    scenario: 'A streaming pipeline handling validation, deduplication, enrichment, and replay.',
    layout: 'dagre',
    aspectRatio: 1.8,
    source: String.raw`
flowchart LR
  SDK[Mobile and Web SDKs] --> Gateway[Ingestion gateway]
  Partner[Partner batch files] --> Gateway
  Gateway --> Auth{Credential valid?}
  Auth -- No --> Reject[Reject with audit record]
  Auth -- Yes --> Schema{Schema valid?}
  Schema -- No --> Quarantine[(Quarantine topic)]
  Schema -- Yes --> Dedupe[Deduplicate by event id]
  Dedupe --> Duplicate{Seen before?}
  Duplicate -- Yes --> Metrics[Increment duplicate metric]
  Duplicate -- No --> Enrich[Add tenant and geo context]
  Enrich --> Privacy{Consent permits use?}
  Privacy -- No --> Redact[Redact restricted fields]
  Privacy -- Yes --> Route[Route by event family]
  Redact --> Route
  Route --> Realtime[(Realtime topic)]
  Route --> Warehouse[(Warehouse stream)]
  Route --> Archive[(Immutable archive)]
  Quarantine --> Review[Operator review]
  Review --> Fixed{Repairable?}
  Fixed -- Yes --> Replay[Replay corrected event] --> Schema
  Fixed -- No --> Reject
`,
  },
  {
    id: 'rc_flow_access_governance',
    kind: 'flowchart',
    title: 'Privileged access governance',
    scenario: 'Approval, policy evaluation, provisioning, expiry, and emergency revocation.',
    layout: 'dagre',
    aspectRatio: 1.65,
    source: String.raw`
flowchart TB
  Request([Access request]) --> Identity[Resolve employee and device]
  Identity --> Risk{Risk signals acceptable?}
  Risk -- No --> Deny[Reject and notify]
  Risk -- Yes --> Policy[Evaluate resource policy]
  Policy --> Auto{Eligible for auto approval?}
  Auto -- Yes --> Grant[Issue time-bound grant]
  Auto -- No --> Manager[Manager review]
  Manager --> ManagerDecision{Approved?}
  ManagerDecision -- No --> Deny
  ManagerDecision -- Yes --> Security{Security review required?}
  Security -- Yes --> SecurityReview[Security owner review]
  Security -- No --> Grant
  SecurityReview --> SecurityDecision{Approved?}
  SecurityDecision -- No --> Deny
  SecurityDecision -- Yes --> Grant
  Grant --> Provision[Provision least privilege]
  Provision --> Audit[(Append audit event)]
  Audit --> Monitor[Monitor privileged activity]
  Monitor --> Expired{Expired or revoked?}
  Expired -- No --> Monitor
  Expired -- Yes --> Remove[Remove grant]
  Remove --> Complete([Access closed])
`,
  },
  {
    id: 'rc_xy_service_latency',
    kind: 'xychart',
    title: 'Service latency percentiles',
    scenario: 'Four latency series and request volume across a fourteen-day incident window.',
    layout: 'dagre',
    aspectRatio: 1.4,
    source: String.raw`
---
config:
  xyChart:
    showLegend: true
---
xychart
  title "Checkout latency and volume"
  x-axis ["D-13", "D-12", "D-11", "D-10", "D-9", "D-8", "D-7", "D-6", "D-5", "D-4", "D-3", "D-2", "D-1", "Now"]
  y-axis "Normalized value" 0 --> 320
  line "p50 latency" [72, 70, 74, 77, 81, 79, 83, 90, 96, 88, 84, 80, 76, 74]
  line "p95 latency" [140, 145, 151, 160, 168, 176, 224, 285, 260, 210, 188, 172, 158, 150]
  line "p99 latency" [188, 194, 201, 215, 228, 240, 298, 315, 304, 276, 250, 231, 214, 205]
  bar "Request volume" [118, 124, 130, 142, 151, 164, 176, 205, 198, 184, 170, 158, 149, 143]
`,
  },
  {
    id: 'rc_xy_checkout_conversion',
    kind: 'xychart',
    title: 'Checkout conversion by market',
    scenario: 'A horizontal comparison with labels for twelve markets and two conversion stages.',
    layout: 'dagre',
    aspectRatio: 1.4,
    source: String.raw`
---
config:
  xyChart:
    showLegend: true
    showDataLabel: true
    showDataLabelOutsideBar: true
---
xychart horizontal
  title "Checkout conversion by market"
  x-axis [US, CA, MX, BR, GB, DE, FR, ES, JP, KR, AU, IN]
  y-axis "Percent" 0 --> 100
  bar "Payment started" [82, 80, 73, 68, 84, 81, 79, 76, 88, 86, 83, 65]
  line "Order completed" [74, 72, 61, 56, 77, 73, 71, 68, 81, 78, 76, 52]
`,
  },
  {
    id: 'rc_xy_capacity_forecast',
    kind: 'xychart',
    title: 'Capacity plan with demand forecast',
    scenario: 'Monthly provisioned capacity, observed demand, and forecast headroom.',
    layout: 'dagre',
    aspectRatio: 1.4,
    source: String.raw`
xychart
  title "Regional capacity plan"
  x-axis [Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec]
  y-axis "Thousands of requests per second" 0 --> 900
  bar "Provisioned" [420, 420, 480, 480, 560, 560, 640, 640, 720, 720, 820, 820]
  line "Observed peak" [350, 372, 401, 430, 468, 505, 548, 590, 621, 665, 718, 760]
  line "Forecast peak" [360, 385, 415, 449, 486, 526, 568, 610, 653, 699, 748, 802]
`,
  },
  {
    id: 'rc_xy_queue_backlog',
    kind: 'xychart',
    title: 'Queue backlog recovery',
    scenario: 'Backlog and consumer throughput during a recovery window.',
    layout: 'dagre',
    aspectRatio: 1.4,
    source: String.raw`
---
config:
  xyChart:
    showLegend: true
---
xychart
  title "Queue recovery after dependency outage"
  x-axis ["10:00", "10:05", "10:10", "10:15", "10:20", "10:25", "10:30", "10:35", "10:40", "10:45", "10:50", "10:55"]
  y-axis "Messages per second" 0 --> 500
  bar "Backlog" [45, 130, 245, 390, 470, 430, 350, 270, 195, 124, 62, 18]
  line "Consumer throughput" [180, 170, 155, 140, 130, 210, 280, 330, 370, 405, 430, 448]
  line "Producer throughput" [190, 205, 220, 232, 240, 238, 230, 218, 210, 205, 198, 192]
`,
  },
  {
    id: 'rc_xy_error_budget',
    kind: 'xychart',
    title: 'Error budget burn',
    scenario: 'Daily error budget consumption against warning and critical thresholds.',
    layout: 'dagre',
    aspectRatio: 1.4,
    source: String.raw`
xychart
  title "Thirty-day error budget burn"
  x-axis [D1, D3, D5, D7, D9, D11, D13, D15, D17, D19, D21, D23, D25, D27, D29]
  y-axis "Budget consumed percent" 0 --> 100
  line "Actual burn" [2, 4, 7, 11, 15, 19, 25, 34, 48, 57, 63, 69, 75, 81, 86]
  line "Warning boundary" [10, 14, 18, 22, 26, 30, 34, 38, 42, 46, 50, 54, 58, 62, 66]
  line "Critical boundary" [20, 24, 28, 32, 36, 40, 44, 48, 52, 56, 60, 64, 68, 72, 76]
`,
  },
  {
    id: 'rc_sequence_checkout_saga',
    kind: 'sequence',
    title: 'Checkout saga orchestration',
    scenario: 'A checkout request coordinating inventory, payment, order storage, and compensation.',
    layout: 'dagre',
    aspectRatio: 0.8,
    source: String.raw`
sequenceDiagram
  autonumber
  actor Customer
  participant App
  participant Gateway
  participant Inventory
  participant Payment
  participant Orders
  participant Shipping
  Customer->>App: Confirm checkout
  App->>+Gateway: POST /checkout
  Gateway->>+Inventory: Reserve items
  alt Inventory available
    Inventory-->>-Gateway: Reservation token
    Gateway->>+Payment: Authorize total
    alt Payment authorized
      Payment-->>-Gateway: Authorization id
      Gateway->>+Orders: Create order
      Orders-->>-Gateway: Order id
      Gateway->>Shipping: Request shipment
      Shipping-->>Gateway: Shipment accepted
      Gateway-->>App: Order confirmed
    else Payment rejected
      Payment-->>Gateway: Decline reason
      Gateway->>Inventory: Release reservation
      Gateway-->>App: Payment failed
    end
  else Inventory unavailable
    Inventory-->>Gateway: Missing items
    Gateway-->>App: Cart changed
  end
  deactivate Gateway
  App-->>Customer: Show final status
`,
  },
  {
    id: 'rc_sequence_oauth_refresh',
    kind: 'sequence',
    title: 'OAuth token refresh race',
    scenario: 'Concurrent API requests coordinate a single token refresh and retry.',
    layout: 'dagre',
    aspectRatio: 0.78,
    source: String.raw`
sequenceDiagram
  participant UI as Mobile UI
  participant Client as API Client
  participant Lock as Refresh Lock
  participant Auth as Authorization Server
  participant API as Resource Server
  par Profile request
    UI->>Client: GET /profile
    Client->>API: Access token A
    API-->>Client: 401 expired
  and Orders request
    UI->>Client: GET /orders
    Client->>API: Access token A
    API-->>Client: 401 expired
  end
  critical Acquire refresh ownership
    Client->>Lock: compareAndSet(false, true)
  option Another request owns refresh
    Lock-->>Client: wait for new token
  end
  Client->>+Auth: Refresh token
  alt Refresh accepted
    Auth-->>-Client: Access token B
    Client->>Lock: publish token B
    Client->>API: Retry profile
    Client->>API: Retry orders
    API-->>Client: 200 responses
    Client-->>UI: Deliver both results
  else Refresh revoked
    Auth-->>Client: invalid_grant
    Client->>Lock: clear credentials
    Client-->>UI: Require sign-in
  end
`,
  },
  {
    id: 'rc_sequence_media_upload',
    kind: 'sequence',
    title: 'Multipart media upload',
    scenario: 'Upload negotiation, parallel part transfer, verification, and asynchronous processing.',
    layout: 'dagre',
    aspectRatio: 0.78,
    source: String.raw`
sequenceDiagram
  actor Creator
  participant App
  participant API
  participant Store as Object Storage
  participant Queue
  participant Worker
  Creator->>App: Select video
  App->>+API: Create upload session
  API-->>-App: Signed part URLs
  par Upload part 1
    App->>Store: PUT part 1
    Store-->>App: ETag 1
  and Upload part 2
    App->>Store: PUT part 2
    Store-->>App: ETag 2
  and Upload part 3
    App->>Store: PUT part 3
    Store-->>App: ETag 3
  end
  App->>+API: Complete upload with ETags
  API->>Store: Verify object
  alt Checksum matches
    Store-->>API: Object metadata
    API->>Queue: Publish processing job
    API-->>-App: Processing
    Queue->>Worker: Transcode and scan
    loop Progress updates
      Worker-->>API: Stage progress
      App->>API: Poll status
      API-->>App: Current stage
    end
    Worker-->>API: Ready
    API-->>App: Playback URL
  else Checksum mismatch
    Store-->>API: Invalid checksum
    API-->>App: Restart required
  end
`,
  },
  {
    id: 'rc_sequence_webhook_retry',
    kind: 'sequence',
    title: 'Webhook delivery with retries',
    scenario: 'Signed webhook delivery, exponential retry, dead-lettering, and replay.',
    layout: 'dagre',
    aspectRatio: 0.82,
    source: String.raw`
sequenceDiagram
  participant Events
  participant Dispatcher
  participant Secrets
  participant Partner
  participant DLQ as Dead Letter Queue
  participant Operator
  Events->>Dispatcher: Domain event
  Dispatcher->>Secrets: Resolve signing key
  Secrets-->>Dispatcher: Active key
  loop Up to five attempts
    Dispatcher->>Partner: POST signed payload
    alt 2xx response
      Partner-->>Dispatcher: Accepted
      Dispatcher->>Events: Mark delivered
    else Retryable response
      Partner-->>Dispatcher: 429 or 5xx
      Note over Dispatcher: Exponential backoff with jitter
    else Permanent response
      Partner-->>Dispatcher: 400 or 410
      Dispatcher->>DLQ: Store failed delivery
    end
  end
  opt Attempts exhausted
    Dispatcher->>DLQ: Store retry history
    DLQ-->>Operator: Alert
    Operator->>DLQ: Replay after repair
    DLQ->>Dispatcher: Requeue delivery
  end
`,
  },
  {
    id: 'rc_sequence_realtime_sync',
    kind: 'sequence',
    title: 'Offline-first document synchronization',
    scenario: 'Local edits reconcile with server state while presence and attachments update in parallel.',
    layout: 'dagre',
    aspectRatio: 0.82,
    source: String.raw`
sequenceDiagram
  actor Editor
  participant UI
  participant Local as Local Database
  participant Sync
  participant API
  participant Presence
  participant Files
  Editor->>UI: Reconnect document
  UI->>Local: Read pending operations
  Local-->>UI: Local revision and queue
  UI->>+Sync: Start synchronization
  par Reconcile text
    Sync->>API: Fetch changes since revision
    API-->>Sync: Remote operations
    Sync->>Sync: Transform concurrent operations
    Sync->>API: Push transformed local operations
    API-->>Sync: Accepted revision
  and Restore presence
    Sync->>Presence: Join room
    Presence-->>Sync: Active collaborators
  and Resume attachments
    Sync->>Files: Query incomplete uploads
    Files-->>Sync: Missing chunks
  end
  Sync->>Local: Commit merged revision
  Local-->>Sync: Durable
  Sync-->>-UI: Synced document
  UI-->>Editor: Render merged state
`,
  },
  {
    id: 'rc_sequence_cache_stampede',
    kind: 'sequence',
    title: 'Cache stampede prevention',
    scenario: 'Concurrent cache misses elect one loader and distribute the refreshed value.',
    layout: 'dagre',
    aspectRatio: 0.82,
    source: String.raw`
sequenceDiagram
  participant A as Request A
  participant B as Request B
  participant Cache
  participant Lock
  participant DB as Database
  par First lookup
    A->>Cache: GET product:42
    Cache-->>A: Miss
  and Concurrent lookup
    B->>Cache: GET product:42
    Cache-->>B: Miss
  end
  critical Elect loader
    A->>Lock: SET NX product:42
    Lock-->>A: Acquired
  option Loader already elected
    B->>Lock: SET NX product:42
    Lock-->>B: Rejected
  end
  A->>+DB: SELECT product 42
  DB-->>-A: Product row
  A->>Cache: SET product:42 with jittered TTL
  A->>Lock: Release
  loop Wait with bounded backoff
    B->>Cache: GET product:42
    Cache-->>B: Value or miss
  end
  A-->>A: Return value
  B-->>B: Return refreshed value
`,
  },
  {
    id: 'rc_class_commerce_domain',
    kind: 'class',
    title: 'Commerce domain model',
    scenario: 'Aggregate roots, repositories, payment strategy, inventory, and fulfillment.',
    layout: 'dagre',
    aspectRatio: 1.25,
    source: String.raw`
classDiagram
  direction LR
  class Order {
    +OrderId id
    +OrderStatus status
    +Money total
    +addItem(product, quantity)
    +confirm()
    +cancel(reason)
  }
  class OrderItem {
    +ProductId productId
    +int quantity
    +Money unitPrice
  }
  class Customer {
    +CustomerId id
    +Email email
  }
  class Payment {
    +PaymentId id
    +PaymentStatus status
    +authorize()
    +capture()
    +void()
  }
  class PaymentGateway {
    <<interface>>
    +authorize(request)
    +capture(id)
  }
  class InventoryReservation {
    +ReservationId id
    +reserve()
    +release()
  }
  class Shipment {
    +ShipmentId id
    +TrackingCode tracking
    +dispatch()
  }
  class OrderRepository {
    <<interface>>
    +find(id) Order
    +save(order)
  }
  Customer "1" --> "0..*" Order : places
  Order "1" *-- "1..*" OrderItem : contains
  Order "1" o-- "0..1" Payment : payment
  Order "1" o-- "0..1" InventoryReservation : inventory
  Order "1" o-- "0..1" Shipment : fulfillment
  Payment --> PaymentGateway : delegates
  OrderRepository ..> Order : persists
`,
  },
  {
    id: 'rc_class_workflow_engine',
    kind: 'class',
    title: 'Workflow engine architecture',
    scenario: 'Definitions, executable instances, typed steps, scheduling, and state persistence.',
    layout: 'dagre',
    aspectRatio: 1.25,
    source: String.raw`
classDiagram
  direction TB
  namespace Definition {
    class WorkflowDefinition {
      +String key
      +int version
      +List~StepDefinition~ steps
      +validate() Result
    }
    class StepDefinition {
      <<abstract>>
      +String id
      +Duration timeout
    }
    class HttpStep {
      +URI endpoint
      +Map~String,String~ headers
    }
    class DecisionStep {
      +Expression condition
    }
  }
  namespace Runtime {
    class WorkflowInstance {
      +InstanceId id
      +InstanceStatus status
      +advance(event)
    }
    class StepExecution {
      +ExecutionId id
      +int attempt
      +ExecutionStatus status
    }
    class Scheduler {
      +schedule(instance)
      +retry(execution)
    }
    class StateStore {
      <<interface>>
      +load(id) WorkflowInstance
      +commit(instance)
    }
  }
  StepDefinition <|-- HttpStep
  StepDefinition <|-- DecisionStep
  WorkflowDefinition "1" *-- "1..*" StepDefinition
  WorkflowInstance "1" *-- "0..*" StepExecution
  Scheduler --> StateStore
  WorkflowInstance --> WorkflowDefinition : executes
  StepExecution --> StepDefinition : realizes
`,
  },
  {
    id: 'rc_class_authorization_model',
    kind: 'class',
    title: 'Authorization policy model',
    scenario: 'Users, groups, roles, permissions, scoped grants, and policy evaluation.',
    layout: 'dagre',
    aspectRatio: 1.3,
    source: String.raw`
classDiagram
  class Principal {
    <<interface>>
    +PrincipalId id
  }
  class User {
    +String email
    +UserStatus status
  }
  class Group {
    +String name
  }
  class Role {
    +String name
    +Set~Permission~ permissions
  }
  class Permission {
    +String action
    +String resourceType
  }
  class Grant {
    +Instant startsAt
    +Instant expiresAt
    +Scope scope
  }
  class Policy {
    +Effect effect
    +Expression condition
  }
  class Evaluator {
    +evaluate(request) Decision
  }
  class AuditSink {
    <<interface>>
    +append(decision)
  }
  Principal <|.. User
  Principal <|.. Group
  Group "0..*" o-- "0..*" User : members
  Grant --> Principal : subject
  Grant --> Role : assigns
  Role "1" *-- "1..*" Permission
  Policy --> Permission : constrains
  Evaluator --> Grant : resolves
  Evaluator --> Policy : applies
  Evaluator --> AuditSink : records
`,
  },
  {
    id: 'rc_class_notification_platform',
    kind: 'class',
    title: 'Notification delivery platform',
    scenario: 'Templates, channel adapters, preferences, routing, retries, and delivery receipts.',
    layout: 'dagre',
    aspectRatio: 1.25,
    source: String.raw`
classDiagram
  class Notification {
    +NotificationId id
    +Recipient recipient
    +Map~String,Any~ variables
  }
  class Template {
    +TemplateId id
    +Locale locale
    +render(variables) Message
  }
  class PreferenceService {
    +allowed(recipient, channel) bool
  }
  class Router {
    +route(notification) List~Channel~
  }
  class Channel {
    <<interface>>
    +send(message) DeliveryReceipt
  }
  class EmailChannel
  class PushChannel
  class SmsChannel
  class RetryPolicy {
    +int maxAttempts
    +Duration nextDelay(attempt)
  }
  class DeliveryStore {
    <<interface>>
    +save(receipt)
    +pending() List~DeliveryReceipt~
  }
  Notification --> Template : renders with
  Router --> PreferenceService : checks
  Router --> Channel : selects
  Channel <|.. EmailChannel
  Channel <|.. PushChannel
  Channel <|.. SmsChannel
  Channel --> RetryPolicy : follows
  Channel --> DeliveryStore : records
`,
  },
  {
    id: 'rc_class_document_editor',
    kind: 'class',
    title: 'Collaborative document editor',
    scenario: 'Documents, blocks, operations, revisions, presence, and conflict transformation.',
    layout: 'dagre',
    aspectRatio: 1.25,
    source: String.raw`
classDiagram
  class Document {
    +DocumentId id
    +Revision revision
    +List~Block~ blocks
    +apply(operation)
  }
  class Block {
    <<abstract>>
    +BlockId id
    +Position position
  }
  class TextBlock {
    +String text
    +List~Mark~ marks
  }
  class MediaBlock {
    +URI source
    +Dimensions size
  }
  class Operation {
    +OperationId id
    +Revision baseRevision
    +transform(other) Operation
  }
  class Revision {
    +long sequence
    +Instant committedAt
  }
  class Session {
    +SessionId id
    +UserId user
    +Cursor cursor
  }
  class SyncEngine {
    +merge(local, remote) MergeResult
  }
  class DocumentStore {
    <<interface>>
    +load(id) Document
    +commit(document, operations)
  }
  Block <|-- TextBlock
  Block <|-- MediaBlock
  Document "1" *-- "1..*" Block
  Document "1" o-- "0..*" Revision
  Document "1" o-- "0..*" Session
  SyncEngine --> Operation
  SyncEngine --> DocumentStore
`,
  },
  {
    id: 'rc_state_order_fulfillment',
    kind: 'state',
    title: 'Order fulfillment lifecycle',
    scenario: 'Payment, allocation, shipment, cancellation, return, and refund states.',
    layout: 'dagre',
    aspectRatio: 1.1,
    source: String.raw`
stateDiagram-v2
  [*] --> Draft
  Draft --> AwaitingPayment : submit
  AwaitingPayment --> Cancelled : payment timeout
  AwaitingPayment --> Allocating : payment captured
  Allocating --> Backordered : stock unavailable
  Backordered --> Allocating : replenished
  Backordered --> Cancelled : customer cancels
  Allocating --> Packing : stock allocated
  Packing --> Cancelled : warehouse rejects
  Packing --> Shipped : carrier accepts
  Shipped --> Delivered : delivery confirmed
  Shipped --> DeliveryException : carrier exception
  DeliveryException --> Shipped : redelivery
  DeliveryException --> Returned : returned to sender
  Delivered --> ReturnRequested : return opened
  ReturnRequested --> Returned : item received
  ReturnRequested --> Delivered : request denied
  Returned --> Refunded : inspection passed
  Returned --> Delivered : inspection failed
  Cancelled --> Refunded : payment existed
  Cancelled --> [*]
  Refunded --> [*]
  Delivered --> [*] : return window closed
`,
  },
  {
    id: 'rc_state_payment_transaction',
    kind: 'state',
    title: 'Payment transaction state machine',
    scenario: 'Authorization, challenge, capture, partial refund, reversal, and terminal outcomes.',
    layout: 'dagre',
    aspectRatio: 1.1,
    source: String.raw`
stateDiagram-v2
  state risk <<choice>>
  [*] --> Created
  Created --> Validating : submit
  Validating --> risk
  risk --> Declined : blocked
  risk --> Challenging : step up required
  risk --> Authorizing : low risk
  Challenging --> Authorizing : challenge passed
  Challenging --> Declined : challenge failed
  Authorizing --> Authorized : issuer approved
  Authorizing --> Declined : issuer declined
  Authorized --> Capturing : capture requested
  Authorized --> Voided : void requested
  Capturing --> Captured : capture confirmed
  Capturing --> Authorized : retryable failure
  Captured --> PartiallyRefunded : partial refund
  PartiallyRefunded --> PartiallyRefunded : another partial refund
  PartiallyRefunded --> Refunded : full amount refunded
  Captured --> Refunded : full refund
  Captured --> Reversed : chargeback
  Declined --> [*]
  Voided --> [*]
  Refunded --> [*]
  Reversed --> [*]
`,
  },
  {
    id: 'rc_state_deployment_rollout',
    kind: 'state',
    title: 'Progressive deployment rollout',
    scenario: 'Build, canary, staged rollout, automated rollback, and incident review.',
    layout: 'dagre',
    aspectRatio: 1.05,
    source: String.raw`
stateDiagram-v2
  [*] --> Preparing
  state Preparing {
    [*] --> Building
    Building --> Testing
    Testing --> Signing
    Signing --> [*]
  }
  Preparing --> Canary : artifacts ready
  state Canary {
    [*] --> DeployingFivePercent
    DeployingFivePercent --> Observing
    Observing --> Approved : SLO healthy
    Observing --> Rejected : SLO violated
  }
  Canary --> RollingOut : approved
  Canary --> RollingBack : rejected
  state RollingOut {
    [*] --> TwentyFivePercent
    TwentyFivePercent --> FiftyPercent
    FiftyPercent --> OneHundredPercent
    OneHundredPercent --> [*]
  }
  RollingOut --> Completed
  RollingOut --> RollingBack : guardrail violated
  RollingBack --> IncidentReview : previous version restored
  IncidentReview --> Preparing : fix prepared
  Completed --> [*]
`,
  },
  {
    id: 'rc_state_media_processing',
    kind: 'state',
    title: 'Media processing pipeline',
    scenario: 'Upload verification, concurrent transforms, moderation, publication, and retry.',
    layout: 'dagre',
    aspectRatio: 1.05,
    source: String.raw`
stateDiagram-v2
  state split <<fork>>
  state merge <<join>>
  [*] --> Uploading
  Uploading --> Verifying : all parts uploaded
  Uploading --> Failed : session expired
  Verifying --> Failed : checksum mismatch
  Verifying --> split : object verified
  split --> Transcoding
  split --> Thumbnailing
  split --> Moderating
  Transcoding --> merge
  Thumbnailing --> merge
  Moderating --> Rejected : policy violation
  Moderating --> merge : accepted
  merge --> Packaging
  Packaging --> Ready
  Packaging --> RetryableFailure : worker timeout
  RetryableFailure --> Packaging : retry budget remains
  RetryableFailure --> Failed : retry budget exhausted
  Ready --> Published : metadata complete
  Ready --> Archived : owner removes asset
  Published --> Archived : retention expires
  Rejected --> [*]
  Failed --> [*]
  Archived --> [*]
`,
  },
  {
    id: 'rc_state_support_ticket',
    kind: 'state',
    title: 'Support ticket lifecycle',
    scenario: 'Automated triage, ownership, customer waiting, escalation, and reopening.',
    layout: 'dagre',
    aspectRatio: 1.1,
    source: String.raw`
stateDiagram-v2
  [*] --> New
  New --> Triaged : classifier result
  Triaged --> Assigned : queue selected
  Assigned --> Investigating : agent accepts
  Investigating --> WaitingForCustomer : need information
  WaitingForCustomer --> Investigating : customer replies
  WaitingForCustomer --> AutoClosed : response timeout
  Investigating --> Escalated : specialist required
  Escalated --> Investigating : guidance received
  Investigating --> PendingChange : product fix required
  PendingChange --> Investigating : fix released
  Investigating --> Resolved : solution delivered
  Resolved --> Closed : customer confirms
  Resolved --> Reopened : issue persists
  Reopened --> Assigned
  AutoClosed --> Reopened : customer replies
  Closed --> [*]
  AutoClosed --> [*]
`,
  },
  {
    id: 'rc_er_commerce_platform',
    kind: 'er',
    title: 'Commerce platform data model',
    scenario: 'Customers, catalog, orders, payments, fulfillment, promotions, and returns.',
    layout: 'dagre',
    aspectRatio: 1.25,
    source: String.raw`
erDiagram
  CUSTOMER {
    bigint id PK
    string email UK
    timestamp created_at
  }
  ADDRESS {
    bigint id PK
    bigint customer_id FK
    string country
  }
  PRODUCT {
    bigint id PK
    string sku UK
    bigint category_id FK
  }
  CATEGORY {
    bigint id PK
    string name UK
  }
  ORDER {
    bigint id PK
    bigint customer_id FK
    bigint shipping_address_id FK
    string status
  }
  ORDER_ITEM {
    bigint order_id PK, FK
    bigint product_id PK, FK
    int quantity
    decimal unit_price
  }
  PAYMENT {
    bigint id PK
    bigint order_id FK
    string status
  }
  SHIPMENT {
    bigint id PK
    bigint order_id FK
    string tracking_code UK
  }
  PROMOTION {
    bigint id PK
    string code UK
  }
  RETURN {
    bigint id PK
    bigint order_id FK
    string reason
  }
  CUSTOMER ||--o{ ADDRESS : owns
  CUSTOMER ||--o{ ORDER : places
  ADDRESS ||--o{ ORDER : ships_to
  CATEGORY ||--o{ PRODUCT : groups
  ORDER ||--|{ ORDER_ITEM : contains
  PRODUCT ||--o{ ORDER_ITEM : appears_in
  ORDER ||--o{ PAYMENT : receives
  ORDER ||--o| SHIPMENT : creates
  ORDER }o--o{ PROMOTION : applies
  ORDER ||--o{ RETURN : permits
`,
  },
  {
    id: 'rc_er_learning_platform',
    kind: 'er',
    title: 'Learning platform data model',
    scenario: 'Courses, modules, lessons, enrollment, progress, assessment, and certificates.',
    layout: 'dagre',
    aspectRatio: 1.25,
    source: String.raw`
erDiagram
  LEARNER {
    bigint id PK
    string email UK
  }
  INSTRUCTOR {
    bigint id PK
    string display_name
  }
  COURSE {
    bigint id PK
    bigint instructor_id FK
    string title
  }
  MODULE {
    bigint id PK
    bigint course_id FK
    int position
  }
  LESSON {
    bigint id PK
    bigint module_id FK
    string content_type
  }
  ENROLLMENT {
    bigint learner_id PK, FK
    bigint course_id PK, FK
    timestamp enrolled_at
  }
  PROGRESS {
    bigint learner_id PK, FK
    bigint lesson_id PK, FK
    decimal completion
  }
  ASSESSMENT {
    bigint id PK
    bigint course_id FK
  }
  ATTEMPT {
    bigint id PK
    bigint assessment_id FK
    bigint learner_id FK
    decimal score
  }
  CERTIFICATE {
    bigint id PK
    bigint enrollment_id FK
    timestamp issued_at
  }
  INSTRUCTOR ||--o{ COURSE : authors
  COURSE ||--|{ MODULE : contains
  MODULE ||--|{ LESSON : contains
  LEARNER ||--o{ ENROLLMENT : creates
  COURSE ||--o{ ENROLLMENT : accepts
  LEARNER ||--o{ PROGRESS : records
  LESSON ||--o{ PROGRESS : measures
  COURSE ||--o{ ASSESSMENT : evaluates
  ASSESSMENT ||--o{ ATTEMPT : receives
  LEARNER ||--o{ ATTEMPT : submits
  ENROLLMENT ||--o| CERTIFICATE : earns
`,
  },
  {
    id: 'rc_er_messaging_system',
    kind: 'er',
    title: 'Messaging system data model',
    scenario: 'Conversations, membership, messages, reactions, attachments, reads, and devices.',
    layout: 'dagre',
    aspectRatio: 1.25,
    source: String.raw`
erDiagram
  USER {
    bigint id PK
    string handle UK
  }
  DEVICE {
    bigint id PK
    bigint user_id FK
    string push_token UK
  }
  CONVERSATION {
    bigint id PK
    string kind
  }
  MEMBERSHIP {
    bigint conversation_id PK, FK
    bigint user_id PK, FK
    string role
  }
  MESSAGE {
    bigint id PK
    bigint conversation_id FK
    bigint sender_id FK
    string body
  }
  ATTACHMENT {
    bigint id PK
    bigint message_id FK
    string media_type
  }
  REACTION {
    bigint message_id PK, FK
    bigint user_id PK, FK
    string emoji PK
  }
  READ_CURSOR {
    bigint conversation_id PK, FK
    bigint user_id PK, FK
    bigint message_id FK
  }
  USER ||--o{ DEVICE : registers
  USER ||--o{ MEMBERSHIP : joins
  CONVERSATION ||--|{ MEMBERSHIP : includes
  CONVERSATION ||--o{ MESSAGE : contains
  USER ||--o{ MESSAGE : sends
  MESSAGE ||--o{ ATTACHMENT : carries
  MESSAGE ||--o{ REACTION : receives
  USER ||--o{ REACTION : adds
  MEMBERSHIP ||--o| READ_CURSOR : tracks
  MESSAGE ||--o{ READ_CURSOR : last_read
`,
  },
  {
    id: 'rc_er_subscription_billing',
    kind: 'er',
    title: 'Subscription billing data model',
    scenario: 'Accounts, plans, subscriptions, invoices, line items, payments, and entitlements.',
    layout: 'dagre',
    aspectRatio: 1.25,
    source: String.raw`
erDiagram
  ACCOUNT {
    bigint id PK
    string billing_email
  }
  PLAN {
    bigint id PK
    string code UK
    decimal monthly_price
  }
  SUBSCRIPTION {
    bigint id PK
    bigint account_id FK
    bigint plan_id FK
    string status
  }
  INVOICE {
    bigint id PK
    bigint account_id FK
    date period_start
    date period_end
  }
  INVOICE_ITEM {
    bigint id PK
    bigint invoice_id FK
    bigint subscription_id FK
    decimal amount
  }
  PAYMENT_METHOD {
    bigint id PK
    bigint account_id FK
    string provider_token UK
  }
  PAYMENT_ATTEMPT {
    bigint id PK
    bigint invoice_id FK
    bigint payment_method_id FK
    string status
  }
  ENTITLEMENT {
    bigint id PK
    bigint subscription_id FK
    string feature_key
  }
  ACCOUNT ||--o{ SUBSCRIPTION : owns
  PLAN ||--o{ SUBSCRIPTION : configures
  ACCOUNT ||--o{ INVOICE : receives
  INVOICE ||--|{ INVOICE_ITEM : contains
  SUBSCRIPTION ||--o{ INVOICE_ITEM : charges
  ACCOUNT ||--o{ PAYMENT_METHOD : stores
  INVOICE ||--o{ PAYMENT_ATTEMPT : collects
  PAYMENT_METHOD ||--o{ PAYMENT_ATTEMPT : funds
  SUBSCRIPTION ||--o{ ENTITLEMENT : grants
`,
  },
  {
    id: 'rc_er_inventory_network',
    kind: 'er',
    title: 'Warehouse inventory network',
    scenario: 'Warehouses, bins, stock, suppliers, purchase orders, transfers, and reservations.',
    layout: 'dagre',
    aspectRatio: 1.25,
    source: String.raw`
erDiagram
  WAREHOUSE {
    bigint id PK
    string code UK
  }
  BIN {
    bigint id PK
    bigint warehouse_id FK
    string location UK
  }
  SKU {
    bigint id PK
    string code UK
  }
  STOCK {
    bigint bin_id PK, FK
    bigint sku_id PK, FK
    int on_hand
  }
  SUPPLIER {
    bigint id PK
    string name
  }
  PURCHASE_ORDER {
    bigint id PK
    bigint supplier_id FK
    string status
  }
  PURCHASE_LINE {
    bigint order_id PK, FK
    bigint sku_id PK, FK
    int quantity
  }
  TRANSFER {
    bigint id PK
    bigint source_warehouse_id FK
    bigint target_warehouse_id FK
  }
  RESERVATION {
    bigint id PK
    bigint sku_id FK
    bigint warehouse_id FK
    int quantity
  }
  WAREHOUSE ||--|{ BIN : contains
  BIN ||--o{ STOCK : stores
  SKU ||--o{ STOCK : counted_as
  SUPPLIER ||--o{ PURCHASE_ORDER : receives
  PURCHASE_ORDER ||--|{ PURCHASE_LINE : contains
  SKU ||--o{ PURCHASE_LINE : replenishes
  WAREHOUSE ||--o{ TRANSFER : sources
  WAREHOUSE ||--o{ TRANSFER : targets
  WAREHOUSE ||--o{ RESERVATION : allocates
  SKU ||--o{ RESERVATION : reserves
`,
  },
  {
    id: 'rc_gantt_mobile_release',
    kind: 'gantt',
    title: 'Cross-platform mobile release',
    scenario: 'Parallel feature work, integration, security review, store review, and rollout.',
    layout: 'dagre',
    aspectRatio: 2.0,
    source: String.raw`
gantt
  title Cross-platform mobile release
  dateFormat YYYY-MM-DD
  axisFormat %b %d
  tickInterval 1week
  weekday monday
  excludes weekends
  todayMarker off
  section Product
  Requirements freeze :milestone, req, 2026-10-05, 0d
  UX specification :ux, 2026-10-05, 8d
  Acceptance review :review, after ux, 3d
  section Engineering
  Shared core :core, 2026-10-06, 12d
  Android client :android, after req, 14d
  iOS client :ios, after req, 14d
  Web client :web, after req, 10d
  Integration :crit, integration, after core android ios web, 5d
  section Quality
  Test design :testplan, 2026-10-08, 8d
  Regression :regression, after integration testplan, 5d
  Security review :security, after integration, 4d
  Release candidate :milestone, rc, after regression security, 0d
  section Release
  Store review :store, after rc, 5d
  Staged rollout :rollout, after store, 7d
  General availability :milestone, ga, after rollout, 0d
`,
  },
  {
    id: 'rc_gantt_database_migration',
    kind: 'gantt',
    title: 'Zero-downtime database migration',
    scenario: 'Schema expansion, backfill, dual write, verification, cutover, and cleanup.',
    layout: 'dagre',
    aspectRatio: 2.0,
    source: String.raw`
gantt
  title Zero-downtime database migration
  dateFormat YYYY-MM-DD
  axisFormat %b %d
  tickInterval 1week
  excludes weekends
  todayMarker off
  section Preparation
  Capacity review :done, capacity, 2026-11-02, 3d
  Rollback rehearsal :done, rollback, after capacity, 3d
  Expand schema :schema, after rollback, 2d
  section Data movement
  Historical backfill :backfill, after schema, 10d
  Consistency sampling :sampling, 2026-11-12, 7d
  section Application
  Enable dual write :dual, after schema, 8d
  Shadow reads :shadow, after dual, 5d
  Cutover approval :milestone, approval, after backfill sampling shadow, 0d
  Read cutover :crit, cutover, after approval, 2d
  section Cleanup
  Observe new path :observe, after cutover, 5d
  Remove old writes :cleanup, after observe, 3d
  Contract schema :milestone, contract, after cleanup, 0d
`,
  },
  {
    id: 'rc_gantt_regional_launch',
    kind: 'gantt',
    title: 'Regional product launch',
    scenario: 'Localization, compliance, infrastructure, go-to-market, and launch gates.',
    layout: 'dagre',
    aspectRatio: 2.0,
    source: String.raw`
gantt
  title Regional product launch
  dateFormat YYYY-MM-DD
  axisFormat %b %d
  tickInterval 1week
  excludes weekends
  todayMarker off
  section Localization
  Copy inventory :copy, 2027-01-04, 5d
  Translation :translation, after copy, 10d
  Linguistic QA :lqa, after translation, 5d
  section Compliance
  Data mapping :mapping, 2027-01-04, 8d
  Legal review :legal, after mapping, 8d
  Policy approval :milestone, policy, after legal, 0d
  section Infrastructure
  Regional capacity :capacity, 2027-01-06, 10d
  Disaster recovery drill :drill, after capacity, 4d
  section Go to market
  Partner readiness :partners, 2027-01-11, 10d
  Support training :support, after translation, 5d
  Launch review :milestone, review, after lqa policy drill partners support, 0d
  Phased launch :crit, launch, after review, 7d
`,
  },
  {
    id: 'rc_gantt_incident_hardening',
    kind: 'gantt',
    title: 'Post-incident hardening plan',
    scenario: 'Root cause, containment, resilience work, validation, and follow-up review.',
    layout: 'dagre',
    aspectRatio: 2.0,
    source: String.raw`
gantt
  title Post-incident hardening
  dateFormat YYYY-MM-DD
  axisFormat %b %d
  tickInterval 1week
  excludes weekends
  todayMarker off
  section Investigation
  Evidence preservation :done, evidence, 2027-02-01, 2d
  Root cause analysis :done, rca, after evidence, 4d
  Review published :milestone, review, after rca, 0d
  section Immediate controls
  Capacity guardrail :active, guardrail, 2027-02-02, 5d
  Emergency rollback path :rollback, 2027-02-03, 6d
  section Durable fixes
  Remove contention :crit, contention, after rca, 10d
  Add load shedding :shedding, after rca, 8d
  Repair retry policy :retry, after rca, 5d
  section Validation
  Failure injection :chaos, after contention shedding retry, 4d
  Peak load test :load, after chaos, 3d
  SLO review :milestone, slo, after load guardrail rollback, 0d
`,
  },
  {
    id: 'rc_gantt_data_platform_upgrade',
    kind: 'gantt',
    title: 'Data platform engine upgrade',
    scenario: 'Compatibility analysis, dual-running jobs, migration waves, and retirement.',
    layout: 'dagre',
    aspectRatio: 2.0,
    source: String.raw`
gantt
  title Data platform engine upgrade
  dateFormat YYYY-MM-DD
  axisFormat %b %d
  tickInterval 2week
  excludes weekends
  todayMarker off
  section Foundation
  Dependency inventory :inventory, 2027-03-01, 10d
  Compatibility harness :harness, 2027-03-03, 12d
  New engine cluster :cluster, 2027-03-08, 10d
  section Pilot
  Dual-run critical jobs :pilot, after inventory harness cluster, 10d
  Result reconciliation :reconcile, after pilot, 5d
  Pilot approval :milestone, approval, after reconcile, 0d
  section Migration waves
  Low-risk jobs :wave1, after approval, 10d
  Medium-risk jobs :wave2, after wave1, 10d
  Critical jobs :crit, wave3, after wave2, 12d
  section Retirement
  Freeze old deployments :freeze, after wave3, 5d
  Archive execution history :archive, after freeze, 5d
  Retire old cluster :milestone, retire, after archive, 0d
`,
  },
  {
    id: 'rc_pie_cloud_cost',
    kind: 'pie',
    title: 'Cloud infrastructure cost allocation',
    scenario: 'A twelve-category monthly cost allocation with legend values.',
    layout: 'dagre',
    aspectRatio: 1.25,
    source: String.raw`
---
config:
  pie:
    legendPosition: right
    donutHole: 0.38
---
pie showData
  title Monthly cloud cost allocation
  "Compute" : 31.4
  "Object storage" : 12.8
  "Databases" : 14.6
  "Caching" : 5.2
  "Networking" : 9.7
  "Observability" : 6.4
  "Message queues" : 4.1
  "Search" : 5.9
  "Data warehouse" : 4.8
  "Security" : 2.7
  "Support" : 1.5
  "Other" : 0.9
`,
  },
  {
    id: 'rc_pie_traffic_sources',
    kind: 'pie',
    title: 'Acquisition traffic sources',
    scenario: 'Traffic composition across organic, paid, referral, direct, and partner channels.',
    layout: 'dagre',
    aspectRatio: 1.25,
    source: String.raw`
---
config:
  pie:
    legendPosition: bottom
    textPosition: 0.62
---
pie
  title Weekly acquisition traffic
  "Organic search" : 34
  "Paid search" : 18
  "Direct" : 16
  "Social" : 11
  "Partner referrals" : 8
  "Email campaigns" : 6
  "App store discovery" : 4
  "Other" : 3
`,
  },
  {
    id: 'rc_pie_incident_causes',
    kind: 'pie',
    title: 'Production incident causes',
    scenario: 'Incident root-cause distribution with the largest category highlighted.',
    layout: 'dagre',
    aspectRatio: 1.25,
    source: String.raw`
---
config:
  pie:
    highlightSlice: Unsafe deployment
    legendPosition: right
---
pie showData
  title Quarterly incident root causes
  "Unsafe deployment" : 27
  "Dependency outage" : 18
  "Capacity exhaustion" : 16
  "Configuration error" : 14
  "Data quality" : 9
  "Certificate expiry" : 6
  "Network fault" : 5
  "Unknown" : 5
`,
  },
  {
    id: 'rc_pie_subscription_mix',
    kind: 'pie',
    title: 'Subscription plan mix',
    scenario: 'A donut chart showing active customer distribution across plan tiers.',
    layout: 'dagre',
    aspectRatio: 1.25,
    source: String.raw`
---
config:
  pie:
    donutHole: 0.56
    legendPosition: center
---
pie showData
  title Active subscriptions by plan
  "Free" : 58420
  "Starter" : 18430
  "Professional" : 9275
  "Business" : 3120
  "Enterprise" : 755
`,
  },
  {
    id: 'rc_pie_storage_allocation',
    kind: 'pie',
    title: 'Tenant storage allocation',
    scenario: 'Storage consumption split by content family with a small free-space slice.',
    layout: 'dagre',
    aspectRatio: 1.25,
    source: String.raw`
---
config:
  pie:
    legendPosition: left
    textPosition: 0.5
---
pie
  title Tenant storage allocation
  "Video originals" : 41.2
  "Video derivatives" : 19.6
  "Images" : 12.3
  "Documents" : 8.8
  "Database backups" : 7.4
  "Search indexes" : 4.5
  "Logs" : 3.9
  "Free space" : 2.3
`,
  },
  {
    id: 'rc_journey_account_onboarding',
    kind: 'journey',
    title: 'Account onboarding journey',
    scenario: 'A customer creates an account, verifies identity, and completes initial setup.',
    layout: 'dagre',
    aspectRatio: 2.1,
    source: String.raw`
journey
  title Account onboarding
  section Discover
    Compare plans: 4: Customer
    Read privacy summary: 3: Customer
  section Register
    Enter account details: 4: Customer
    Verify email address: 3: Customer, Identity Service
    Complete identity check: 2: Customer, Identity Service
  section Configure
    Select preferences: 4: Customer
    Invite team members: 3: Customer, Administrator
  section Adopt
    Finish guided setup: 5: Customer
    Confirm first result: 5: Customer, Support
`,
  },
  {
    id: 'rc_journey_incident_response',
    kind: 'journey',
    title: 'Incident response journey',
    scenario: 'Operations and engineering coordinate detection, mitigation, and follow-up.',
    layout: 'dagre',
    aspectRatio: 2.2,
    source: String.raw`
journey
  title Production incident response
  section Detect
    Receive alert: 2: On-call Engineer
    Confirm customer impact: 2: On-call Engineer, Support
  section Coordinate
    Open response channel: 3: Incident Commander
    Assign investigation tracks: 3: Incident Commander, Service Owner
    Publish status update: 4: Incident Commander, Communications
  section Mitigate
    Identify safe rollback: 2: Service Owner
    Execute mitigation: 3: Service Owner, On-call Engineer
    Verify recovery: 4: On-call Engineer, Support
  section Learn
    Preserve evidence: 4: Service Owner
    Complete incident review: 5: Incident Commander, Service Owner
`,
  },
  {
    id: 'rc_journey_checkout_recovery',
    kind: 'journey',
    title: 'Checkout and recovery journey',
    scenario: 'A shopper completes checkout while payment recovery and support paths remain visible.',
    layout: 'dagre',
    aspectRatio: 2.15,
    source: String.raw`
journey
  title Checkout and payment recovery
  section Prepare
    Review cart: 5: Shopper
    Confirm delivery address: 4: Shopper
    Apply promotion: 3: Shopper, Pricing Service
  section Pay
    Choose payment method: 4: Shopper
    Complete authentication: 2: Shopper, Payment Provider
    Handle payment retry: 1: Shopper, Payment Provider, Support
  section Confirm
    Reserve inventory: 3: Inventory Service
    Receive order confirmation: 5: Shopper, Order Service
    Track shipment: 4: Shopper, Carrier
`,
  },
  {
    id: 'rc_journey_release_coordination',
    kind: 'journey',
    title: 'Cross-platform release journey',
    scenario: 'Engineering, quality, and operations coordinate a guarded cross-platform release.',
    layout: 'dagre',
    aspectRatio: 2.25,
    source: String.raw`
journey
  title Cross-platform release
  section Plan
    Confirm release scope: 4: Product, Engineering
    Review compatibility risks: 3: Android, Desktop, iOS, Web
  section Build
    Produce signed artifacts: 3: Android, Desktop, iOS, Web
    Publish release notes: 4: Product, Engineering
  section Validate
    Run device matrix: 3: Android, iOS, Quality
    Run browser matrix: 3: Web, Quality
    Verify desktop packages: 3: Desktop, Quality
  section Release
    Approve rollout: 4: Product, Quality, Operations
    Monitor production health: 4: Engineering, Operations
    Confirm global availability: 5: Product, Operations
`,
  },
  {
    id: 'rc_journey_privileged_access',
    kind: 'journey',
    title: 'Privileged access journey',
    scenario: 'A time-bound access request moves through policy, approval, use, and revocation.',
    layout: 'dagre',
    aspectRatio: 2.2,
    source: String.raw`
journey
  accTitle: Privileged access lifecycle
  accDescr {
    The journey follows a requester from justification through approval,
    monitored use, expiry, and access removal.
  }
  title Privileged access lifecycle
  section Request
    Describe business need: 4: Requester
    Select least privilege role: 3: Requester, Resource Owner
  section Evaluate
    Run policy checks: 4: Policy Engine
    Review elevated risk: 2: Security Reviewer, Resource Owner
    Approve time-bound grant: 4: Resource Owner
  section Use
    Provision access: 3: Access Service
    Perform approved work: 4: Requester
    Monitor privileged activity: 3: Security Operations
  section Close
    Expire the grant: 5: Access Service
    Confirm access removal: 5: Requester, Resource Owner
`,
  },
  {
    id: 'rc_requirement_checkout_assurance',
    kind: 'requirement',
    title: 'Checkout assurance model',
    scenario: 'Payment requirements trace from system goals through interfaces and verification evidence.',
    layout: 'dagre',
    aspectRatio: 1.7,
    source: String.raw`
requirementDiagram
  direction LR
  requirement checkout_platform {
    id: "PAY-SYS-1"
    text: "Complete checkout without duplicate orders"
    risk: high
    verifyMethod: demonstration
  }
  functionalRequirement idempotent_order {
    id: "PAY-FN-2"
    text: "Create at most one order for each payment attempt"
    risk: high
    verifyMethod: test
  }
  interfaceRequirement gateway_contract {
    id: "PAY-IF-3"
    text: "Preserve the external payment contract"
    risk: medium
    verifyMethod: inspection
  }
  performanceRequirement authorization_latency {
    id: "PAY-PF-4"
    text: "Return authorization within the latency budget"
    risk: medium
    verifyMethod: analysis
  }
  element checkout_service {
    type: "Application service"
    docRef: "design/checkout-service"
  }
  element contract_suite {
    type: "Contract suite"
    docRef: "tests/payment-contract"
  }
  checkout_platform - contains -> idempotent_order
  checkout_platform - contains -> gateway_contract
  idempotent_order - derives -> authorization_latency
  checkout_service - satisfies -> idempotent_order
  contract_suite - verifies -> gateway_contract
  gateway_contract - traces -> authorization_latency
`,
  },
  {
    id: 'rc_requirement_device_safety',
    kind: 'requirement',
    title: 'Device safety controls',
    scenario: 'Safety requirements decompose into physical limits, design constraints, and test evidence.',
    layout: 'dagre',
    aspectRatio: 1.45,
    source: String.raw`
requirementDiagram
  direction TB
  requirement safe_operation {
    id: "SAFE-1"
    text: "Maintain safe operation during every supported mode"
    risk: high
    verifyMethod: demonstration
  }
  physicalRequirement thermal_limit {
    id: "SAFE-1.1"
    text: "Keep enclosure temperature below the approved limit"
    risk: high
    verifyMethod: test
  }
  designConstraint shutdown_guard {
    id: "SAFE-1.2"
    text: "Enter a protected state after a sensor fault"
    risk: high
    verifyMethod: inspection
  }
  interfaceRequirement alarm_contract {
    id: "SAFE-1.3"
    text: "Publish an operator alarm before automatic shutdown"
    risk: medium
    verifyMethod: analysis
  }
  element thermal_controller {
    type: "Control component"
    docRef: "architecture/thermal-controller"
  }
  element safety_lab {
    type: "Verification facility"
    docRef: "evidence/safety-validation"
  }
  safe_operation - contains -> thermal_limit
  safe_operation - contains -> shutdown_guard
  shutdown_guard - derives -> alarm_contract
  thermal_controller - satisfies -> shutdown_guard
  safety_lab - verifies -> thermal_limit
  safety_lab - verifies -> alarm_contract
`,
  },
  {
    id: 'rc_requirement_data_retention',
    kind: 'requirement',
    title: 'Data retention traceability',
    scenario: 'Policy, storage, deletion, and audit requirements remain traceable across implementation assets.',
    layout: 'dagre',
    aspectRatio: 1.65,
    source: String.raw`
requirementDiagram
  direction RL
  requirement retention_policy {
    id: "DATA-1"
    text: "Retain records only for the approved business period"
    risk: high
    verifyMethod: inspection
  }
  functionalRequirement deletion_workflow {
    id: "DATA-1.1"
    text: "Delete expired records and derived copies"
    risk: high
    verifyMethod: test
  }
  performanceRequirement deletion_window {
    id: "DATA-1.2"
    text: "Complete deletion within the approved window"
    risk: medium
    verifyMethod: analysis
  }
  designConstraint immutable_audit {
    id: "DATA-1.3"
    text: "Preserve immutable deletion evidence"
    risk: medium
    verifyMethod: inspection
  }
  element lifecycle_worker {
    type: "Background worker"
    docRef: "design/lifecycle-worker"
  }
  element evidence_store {
    type: "Audit store"
    docRef: "design/evidence-store"
  }
  retention_policy - contains -> deletion_workflow
  deletion_workflow - refines -> deletion_window
  deletion_workflow - derives -> immutable_audit
  lifecycle_worker - satisfies -> deletion_workflow
  evidence_store - satisfies -> immutable_audit
  deletion_window <- verifies - lifecycle_worker
`,
  },
  {
    id: 'rc_requirement_failover_evidence',
    kind: 'requirement',
    title: 'Regional failover evidence',
    scenario: 'Availability targets connect to routing, recovery, observability, and validation evidence.',
    layout: 'dagre',
    aspectRatio: 1.55,
    source: String.raw`
requirementDiagram
  direction BT
  requirement regional_service {
    id: "SRE-1"
    text: "Continue serving requests after a regional outage"
    risk: high
    verifyMethod: demonstration
  }
  functionalRequirement traffic_shift {
    id: "SRE-1.1"
    text: "Shift traffic to a healthy region"
    risk: high
    verifyMethod: test
  }
  performanceRequirement recovery_target {
    id: "SRE-1.2"
    text: "Restore full capacity within the recovery target"
    risk: medium
    verifyMethod: analysis
  }
  interfaceRequirement health_signal {
    id: "SRE-1.3"
    text: "Expose health signals to the traffic manager"
    risk: medium
    verifyMethod: inspection
  }
  element traffic_manager {
    type: "Routing service"
    docRef: "operations/traffic-manager"
  }
  element resilience_suite {
    type: "Failure injection suite"
    docRef: "evidence/regional-failover"
  }
  regional_service - contains -> traffic_shift
  regional_service - contains -> recovery_target
  traffic_shift - refines -> health_signal
  traffic_manager - satisfies -> traffic_shift
  resilience_suite - verifies -> regional_service
  resilience_suite - verifies -> recovery_target
`,
  },
  {
    id: 'rc_requirement_access_controls',
    kind: 'requirement',
    title: 'Styled access-control requirements',
    scenario: 'Critical controls, implementation elements, metadata, and reusable styles in one review model.',
    layout: 'dagre',
    aspectRatio: 1.6,
    source: String.raw`
---
title: Access control requirements
config:
  theme: default
  look: classic
  layout: dagre
---
requirementDiagram
  accTitle: Access control requirement model
  accDescr {
    Authentication and authorization controls are connected to implementation
    components and independent verification evidence.
  }
  direction LR
  requirement protected_access:::critical {
    id: "AUTH-1"
    text: "**Authenticate** every protected request"
    risk: high
    verifyMethod: test
  }
  functionalRequirement least_privilege {
    id: "AUTH-1.1"
    text: "Authorize only the minimum required capability"
    risk: high
    verifyMethod: inspection
  }
  element policy_gateway {
    type: "Policy enforcement point"
    docRef: "architecture/policy-gateway"
  }
  element security_suite {
    type: "Independent verification suite"
    docRef: "tests/access-control"
  }
  classDef critical fill:#fee2e2,stroke:#b91c1c,color:#7f1d1d,stroke-width:3px
  style least_privilege fill:#dcfce7,stroke:#15803d,color:#14532d
  class policy_gateway critical
  protected_access - contains -> least_privilege
  policy_gateway - satisfies -> protected_access
  security_suite - verifies -> protected_access
  security_suite - verifies -> least_privilege
`,
  },
  {
    id: 'rc_gitgraph_release_train',
    kind: 'gitgraph',
    title: 'Multi-branch release train',
    scenario: 'A release train integrates nested feature work, a hotfix cherry-pick, and a tagged merge.',
    layout: 'dagre',
    aspectRatio: 1.8,
    source: String.raw`
gitGraph LR:
  commit id: "bootstrap"
  commit id: "baseline"
  branch develop order: 2
  commit id: "api-contract"
  branch experiment order: 3
  commit id: "prototype" type: HIGHLIGHT
  checkout develop
  commit id: "integration"
  merge experiment id: "accept-experiment"
  branch release order: 1
  commit id: "candidate" tag: "rc.1"
  checkout main
  commit id: "urgent-fix" type: REVERSE
  checkout release
  cherry-pick id: "urgent-fix"
  commit id: "verified"
  checkout main
  merge release id: "v2.0.0" tag: "stable"
`,
  },
  {
    id: 'rc_gitgraph_parallel_bottom_to_top',
    kind: 'gitgraph',
    title: 'Parallel bottom-to-top delivery',
    scenario: 'Independent client and service work share parent ranks before a bottom-to-top merge.',
    layout: 'dagre',
    aspectRatio: 1.45,
    source: String.raw`
---
config:
  gitGraph:
    parallelCommits: true
---
gitGraph BT:
  commit id: "approved-plan"
  branch service order: 2
  commit id: "service-api"
  commit id: "service-tests"
  checkout main
  branch client order: 1
  commit id: "client-ui"
  commit id: "client-tests"
  checkout main
  commit id: "release-notes"
  merge service id: "service-ready"
  merge client id: "delivery-ready" tag: "candidate"
`,
  },
  {
    id: 'rc_gitgraph_ordered_top_to_bottom',
    kind: 'gitgraph',
    title: 'Ordered top-to-bottom branches',
    scenario: 'Explicit fractional branch orders place verification lanes around a reordered main branch.',
    layout: 'dagre',
    aspectRatio: 1.35,
    source: String.raw`
---
config:
  gitGraph:
    mainBranchOrder: 2
---
gitGraph TB:
  commit id: "proposal"
  branch implementation order: 1
  commit id: "build"
  branch validation order: 4
  commit id: "integration-tests" type: HIGHLIGHT
  checkout implementation
  commit id: "review"
  merge validation id: "verified"
  checkout main
  commit id: "approval"
  merge implementation id: "accepted" tag: "ready"
`,
  },
  {
    id: 'rc_gitgraph_metadata_and_theme',
    kind: 'gitgraph',
    title: 'Accessible themed history',
    scenario: 'Metadata, Unicode, custom theme variables, and quoted branch names remain visible.',
    layout: 'dagre',
    aspectRatio: 1.65,
    source: String.raw`
---
title: Regional release history
config:
  theme: base
  themeVariables:
    git0: "#0f766e"
    git1: "#c2410c"
    gitInv0: "#ccfbf1"
    gitBranchLabel0: "#ffffff"
    commitLineColor: "#475569"
    commitLabelColor: "#0f172a"
    tagLabelColor: "#134e4a"
    tagLabelBackground: "#ccfbf1"
    tagLabelBorder: "#0f766e"
    textColor: "#0f172a"
---
gitGraph
  accTitle: Regional release history
  accDescr {
    The release candidate is validated before it is merged into the main branch.
  }
  commit id: "准备"
  branch "release candidate"
  commit id: "verify-日本語" type: HIGHLIGHT tag: "候选版本"
  checkout main
  commit id: "approval"
  merge "release candidate" id: "发布" tag: "v3"
`,
  },
  {
    id: 'rc_gitgraph_horizontal_labels',
    kind: 'gitgraph',
    title: 'Long horizontal commit labels',
    scenario: 'Horizontal labels, hidden branch decorations, merges, and cherry-picks remain unclipped.',
    layout: 'dagre',
    aspectRatio: 2.1,
    source: String.raw`
---
config:
  gitGraph:
    showBranches: false
    rotateCommitLabel: false
---
gitGraph LR:
  commit id: "initialize-release-coordination"
  branch verification
  commit id: "complete-cross-platform-validation"
  checkout main
  commit id: "publish-release-documentation"
  merge verification id: "accept-validation-results"
  branch maintenance
  commit id: "prepare-follow-up-correction"
  checkout main
  cherry-pick id: "prepare-follow-up-correction"
  commit id: "close-release-window" tag: "complete"
`,
  },
  {
    id: 'rc_packet_transport_header',
    kind: 'packet',
    title: 'Transport protocol header',
    scenario: 'Explicit ranges, single-bit flags, and multi-row fields form a complete transport header.',
    aspectRatio: 2.4,
    source: String.raw`
---
title: Transport protocol header
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
`,
  },
  {
    id: 'rc_packet_counted_fields',
    kind: 'packet',
    title: 'Counted binary frame',
    scenario: 'Count syntax automatically advances field positions and splits long payloads.',
    aspectRatio: 2.4,
    source: String.raw`
packet
  title Counted binary frame
  +4: "Version"
  +4: "Flags"
  +16: "Length"
  +8: "Checksum"
  +72: "Payload"
`,
  },
  {
    id: 'rc_packet_compact_no_bits',
    kind: 'packet',
    title: 'Compact packet layout',
    scenario: 'Custom dimensions and hidden bit numbers produce a compact intrinsic diagram.',
    aspectRatio: 2.7,
    source: String.raw`
---
config:
  packet:
    rowHeight: 26
    bitWidth: 22
    bitsPerRow: 16
    showBits: false
    paddingX: 3
    paddingY: 4
    useMaxWidth: false
---
packet
  title Compact packet
  +4: "Type"
  +4: "Flags"
  +8: "Length"
  +32: "Payload"
`,
  },
  {
    id: 'rc_packet_beta_frame',
    kind: 'packet',
    title: 'Packet beta frame',
    scenario: 'The packet-beta header shares explicit and counted field semantics.',
    aspectRatio: 2.5,
    source: String.raw`
packet-beta
  title Packet beta frame
  0-7: "Revision"
  +8: "Kind"
  +16: "Identifier"
  +64: "Body"
`,
  },
  {
    id: 'rc_packet_accessible_unicode',
    kind: 'packet',
    title: 'Accessible international frame',
    scenario: 'Metadata, comments, Unicode, entities, and escaped quotes remain visible and ordered.',
    aspectRatio: 2.4,
    source: String.raw`
packet
  title 地域 frame
  accTitle: International frame structure
  accDescr: Regional fields and payload ranges
  %% Mixed scripts exercise the quoted string converter.
  +8: "種類 &amp; mode"
  +8: "東京 \"edge\""
  +16: "서울"
  +32: "São Paulo"
`,
  },
  {
    id: 'rc_mindmap_product_strategy',
    kind: 'mindmap',
    title: 'Product strategy hierarchy',
    scenario: 'A product strategy connects outcomes, customer needs, platform investments, and measurable signals.',
    layout: 'cose-bilkent',
    aspectRatio: 1.6,
    source: String.raw`
---
title: Product strategy
config:
  layout: cose-bilkent
---
mindmap
  root((Product strategy))
    Customer outcomes
      Faster onboarding
        Guided setup
        Progressive profiling
      Reliable daily use
        Offline recovery
        Clear error states
    Platform investments
      Shared design system
        Accessible components
        Cross-platform tokens
      Delivery automation
        Reproducible builds
        Release evidence
    Success signals
      Activation rate
      Task completion
      Support volume
`,
  },
  {
    id: 'rc_mindmap_incident_response',
    kind: 'mindmap',
    title: 'Incident response responsibilities',
    scenario: 'A top-to-bottom response map assigns detection, mitigation, communication, and follow-up work.',
    layout: 'dagre',
    aspectRatio: 1.25,
    source: String.raw`
---
title: Incident response
config:
  layout: dagre
---
mindmap
  root((Production incident))
    Detect
      Customer reports
      Service alerts
      Dependency health
    Assess
      Impact
      Severity
      Ownership
    Mitigate
      Roll back
      Shift traffic
      Disable feature
    Communicate
      Status updates
      Stakeholder brief
    Learn
      Timeline
      Root cause
      Follow-up actions
`,
  },
  {
    id: 'rc_mindmap_platform_architecture',
    kind: 'mindmap',
    title: 'Platform architecture map',
    scenario: 'A bidirectional tidy tree presents client and service concerns around one platform boundary.',
    layout: 'tidy-tree',
    aspectRatio: 1.9,
    source: String.raw`
---
title: Platform architecture
config:
  layout: tidy-tree
---
mindmap
  root((Shared platform))
    Clients
      Android
        Offline cache
        Background sync
      iOS
        Secure storage
        Push updates
      Web
        Progressive loading
        Browser storage
    Services
      Gateway
        Authentication
        Rate limits
      Domain APIs
        Commands
        Queries
      Data
        Primary store
        Event archive
`,
  },
  {
    id: 'rc_mindmap_shape_and_text_matrix',
    kind: 'mindmap',
    title: 'Shape and text matrix',
    scenario: 'Every supported Mindmap shape carries markdown, wrapped, escaped, or Unicode labels.',
    layout: 'cose-bilkent',
    aspectRatio: 1.7,
    source: String.raw`
---
config:
  layout: cose-bilkent
  mindmap:
    maxNodeWidth: 130
---
mindmap
  root((**Evidence map**))
    default branch
      Plain label
    square[Square &amp; entities]
      long[A deliberately long label that wraps inside a square node]
    rounded(*Rounded emphasis*)
      child(Secondary rounded node)
    circle((Circle))
      unicode((品質確認))
    cloud)Cloud(
      weather)Operational signal(
    bang))Bang((
      alert))Escalation((
    hex{{Hexagon}}
      gate{{Release gate}}
`,
  },
  {
    id: 'rc_mindmap_governance_theme',
    kind: 'mindmap',
    title: 'Themed governance map',
    scenario: 'Custom theme variables, sizing controls, comments, and a deep governance hierarchy render together.',
    layout: 'dagre',
    aspectRatio: 1.35,
    source: String.raw`
---
title: Delivery governance
config:
  layout: dagre
  mindmap:
    padding: 18
    maxNodeWidth: 140
    useMaxWidth: false
  theme: base
  themeVariables:
    mainBkg: "#f8fafc"
    nodeBorder: "#334155"
    git0: "#0f766e"
    gitBranchLabel0: "#ffffff"
    cScale0: "#ccfbf1"
    cScale1: "#dbeafe"
    cScale2: "#fef3c7"
    cScaleLabel0: "#134e4a"
    cScaleLabel1: "#1e3a8a"
    cScaleLabel2: "#78350f"
---
mindmap
  root((Delivery governance))
    Definition
      Scope
      Owners
      Acceptance criteria
    Verification
      Automated checks
        Unit tests
        Integration tests
      Manual review
        Visual evidence
        Accessibility
    Release
      %% The release branch intentionally includes operational controls.
      Change approval
      Rollback plan
      Monitoring window
`,
  },
  {
    id: 'rc_quadrant_product_portfolio',
    kind: 'quadrant',
    title: 'Product investment portfolio',
    scenario: 'Product initiatives are prioritized by customer value and delivery confidence.',
    aspectRatio: 1,
    source: String.raw`
quadrantChart
  title Product investment portfolio
  x-axis Lower confidence --> Higher confidence
  y-axis Lower customer value --> Higher customer value
  quadrant-1 Commit
  quadrant-2 Validate
  quadrant-3 Pause
  quadrant-4 Optimize
  Search relevance: [0.82, 0.88]
  Checkout recovery: [0.73, 0.69]
  Reporting refresh: [0.46, 0.62]
  Legacy cleanup: [0.28, 0.24]
`,
  },
  {
    id: 'rc_quadrant_incident_risk',
    kind: 'quadrant',
    title: 'Incident risk assessment',
    scenario: 'Operational risks are plotted by likelihood and customer impact.',
    aspectRatio: 1,
    source: String.raw`
quadrantChart
  title Incident risk assessment
  x-axis Unlikely --> Likely
  y-axis Limited impact --> Severe impact
  quadrant-1 Mitigate now
  quadrant-2 Prepare response
  quadrant-3 Monitor
  quadrant-4 Reduce exposure
  Database saturation:::critical: [0.84, 0.91]
  Queue backlog:::warning: [0.67, 0.63]
  Certificate expiry:::warning: [0.38, 0.76]
  Dashboard delay: [0.24, 0.18]
  classDef critical color: #dc2626, radius: 12, stroke-color: #7f1d1d, stroke-width: 3px
  classDef warning color: #f59e0b, radius: 9, stroke-color: #92400e, stroke-width: 2px
`,
  },
  {
    id: 'rc_quadrant_delivery_framework',
    kind: 'quadrant',
    title: 'Delivery framework without observations',
    scenario: 'An empty framework centers its axis and quadrant labels before initiatives exist.',
    aspectRatio: 1,
    source: String.raw`
quadrantChart
  title Delivery framework
  x-axis Low urgency --> High urgency
  y-axis Low impact --> High impact
  quadrant-1 Execute
  quadrant-2 Schedule
  quadrant-3 Eliminate
  quadrant-4 Delegate
`,
  },
  {
    id: 'rc_quadrant_governance_theme',
    kind: 'quadrant',
    title: 'Themed governance portfolio',
    scenario: 'Frontmatter changes dimensions, axis placement, sizing mode, and every visible color family.',
    aspectRatio: 1.2,
    source: String.raw`
---
title: Governance portfolio
config:
  quadrantChart:
    chartWidth: 540
    chartHeight: 450
    yAxisPosition: right
    pointRadius: 7
    useMaxWidth: false
  themeVariables:
    quadrant1Fill: "#dcfce7"
    quadrant2Fill: "#dbeafe"
    quadrant3Fill: "#fee2e2"
    quadrant4Fill: "#fef3c7"
    quadrantPointFill: "#0f172a"
    quadrantExternalBorderStrokeFill: "#334155"
---
quadrantChart
  accTitle: Governance portfolio
  accDescr: Delivery controls organized by confidence and evidence
  x-axis Lower confidence --> Higher confidence
  y-axis Less evidence --> More evidence
  quadrant-1 Approve
  quadrant-2 Investigate
  quadrant-3 Reject
  quadrant-4 Recheck
  Automated tests: [0.83, 0.86]
  Manual review: [0.62, 0.71]
  Rollback rehearsal: [0.48, 0.54]
`,
  },
  {
    id: 'rc_quadrant_unicode_market',
    kind: 'quadrant',
    title: 'International market assessment',
    scenario: 'Unicode labels, comments, quoted punctuation, and boundary coordinates render together.',
    aspectRatio: 1,
    source: String.raw`
quadrantChart
  %% International labels exercise the same parser states as Latin text.
  title 市場ポートフォリオ
  x-axis "低い到達度" --> "高い到達度"
  y-axis "低い関与 ❤" --> "高い関与 ❤"
  quadrant-1 拡大する
  quadrant-2 検証する
  quadrant-3 再評価する
  quadrant-4 維持する
  東京: [1, 1]
  서울: [0, 0]
  "São Paulo": [0.58, 0.72] color: #2563eb, radius: 11
`,
  },
  {
    id: 'rc_timeline_release_history',
    kind: 'timeline',
    title: 'Product release history',
    scenario: 'Multiple periods and stacked events exercise the horizontal renderer.',
    aspectRatio: 1.7,
    source: String.raw`
timeline
  title Product release history
  2023 : Private preview
  2024 : Public beta : Partner rollout
       : Accessibility review
  2025 : General availability
`,
  },
  {
    id: 'rc_timeline_vertical_program',
    kind: 'timeline',
    title: 'Vertical program milestones',
    scenario: 'Sections, tasks, and event stacks exercise the top-down renderer.',
    aspectRatio: 1,
    source: String.raw`
timeline TD
  title Vertical program milestones
  section Foundation
    Architecture : Contract review : Prototype
    Platform : Core implementation
  section Validation
    Release candidate : Automated tests : Manual review
    Launch : Monitoring : Rollback rehearsal
`,
  },
  {
    id: 'rc_timeline_sectionless_palette',
    kind: 'timeline',
    title: 'Sectionless multicolor delivery',
    scenario: 'Each period receives a distinct color when no sections are declared.',
    aspectRatio: 1.8,
    source: String.raw`
timeline
  title Sectionless multicolor delivery
  Discover : User research
  Design : Architecture
  Build : Implementation
  Validate : Acceptance
  Operate : Monitoring
`,
  },
  {
    id: 'rc_timeline_configured_theme',
    kind: 'timeline',
    title: 'Configured accessible timeline',
    scenario: 'Spacing, sizing, colors, and accessibility metadata are configured together.',
    aspectRatio: 1.6,
    source: String.raw`
---
config:
  theme: base
  look: classic
  timeline:
    leftMargin: 190
    padding: 30
    useMaxWidth: false
    disableMulticolor: false
  themeVariables:
    cScale0: "#dbeafe"
    cScale1: "#dcfce7"
    cScaleLabel0: "#1e3a8a"
    cScaleLabel1: "#14532d"
---
timeline
  title Configured delivery milestones
  accTitle: Delivery milestones
  accDescr: Build and validation phases
  section Build
    Candidate : API complete : UI complete
  section Validate
    Approval : Automated checks : Manual review
`,
  },
  {
    id: 'rc_timeline_unicode_vertical',
    kind: 'timeline',
    title: 'International vertical history',
    scenario: 'Unicode, entities, HTML breaks, comments, and vertical layout render together.',
    aspectRatio: 1,
    source: String.raw`
timeline TD
  %% International milestones intentionally mix scripts.
  title 地域リリース
  section アジア
    2025 : 東京 &amp; 서울
  section Americas
    2026 : São Paulo <br> general availability
`,
  },
  {
    id: 'rc_kanban_release_workflow',
    kind: 'kanban',
    title: 'Release workflow board',
    scenario: 'Three workflow stages organize delivery tasks in source order.',
    aspectRatio: 2.1,
    source: String.raw`
kanban
  backlog[Backlog]
    scope[Confirm release scope]
    owners[Assign component owners]
  progress[In progress]
    implementation[Complete native renderer]
    integration[Run integration suite]
  done[Done]
    approval[Architecture approval]
`,
  },
  {
    id: 'rc_kanban_priority_triage',
    kind: 'kanban',
    title: 'Priority triage board',
    scenario: 'Ticket, assignee, and every supported priority marker render on task cards.',
    aspectRatio: 2,
    source: String.raw`
kanban
  triage[Triage]
    outage[Restore service]@{ ticket: OPS-101, assigned: Ada, priority: 'Very High' }
    latency[Reduce latency]@{ ticket: OPS-102, assigned: Lin, priority: High }
    cleanup[Remove stale data]@{ priority: Medium }
  planned[Planned]
    docs[Refresh runbook]@{ priority: Low }
    polish[Polish dashboard]@{ priority: 'Very Low' }
`,
  },
  {
    id: 'rc_kanban_wrapped_empty_stage',
    kind: 'kanban',
    title: 'Wrapped tasks and empty stage',
    scenario: 'Long task text wraps while an adjacent empty stage keeps its minimum height.',
    aspectRatio: 1.8,
    source: String.raw`
kanban
  planned[Planned work]
    long[Implement deterministic rendering verification across desktop and mobile targets]
  waiting[Waiting for approval]
  complete[Complete]
    shipped[Publish verified artifacts]
`,
  },
  {
    id: 'rc_kanban_configured_links',
    kind: 'kanban',
    title: 'Configured linked tickets',
    scenario: 'Section width, palette variables, classic look, and external ticket links combine.',
    aspectRatio: 1.8,
    source: String.raw`
---
config:
  theme: base
  look: classic
  kanban:
    sectionWidth: 230
    ticketBaseUrl: "https://issues.example/#TICKET#"
  themeVariables:
    cScale2: "#dbeafe"
    cScale3: "#dcfce7"
    cScaleLabel2: "#1e3a8a"
    cScaleLabel3: "#14532d"
---
kanban
  build[Build]
    parser[Translate parser]@{ ticket: KB-201, assigned: Ada, priority: High }
  verify[Verify]
    parity[Review visual parity]@{ ticket: KB-202, assigned: Lin, priority: Low }
`,
  },
  {
    id: 'rc_kanban_unicode_indentation',
    kind: 'kanban',
    title: 'International nested board',
    scenario: 'Unicode, comments, anonymous nodes, and deeper indentation share one board.',
    aspectRatio: 1.9,
    source: String.raw`
kanban
  todo[準備]
    [仕様を確認]
      nested[詳細レビュー]
    %% Deeper indentation remains in the current section.
        evidence[証拠を保存]
  done[완료]
    release[서울 출시]
`,
  },
  {
    id: 'rc_radar_release_readiness',
    kind: 'radar',
    title: 'Release readiness comparison',
    scenario: 'Two release candidates are compared across five delivery readiness axes.',
    aspectRatio: 1,
    source: String.raw`
---
title: Release readiness
---
radar-beta
  axis tests["Automated tests"], parity["Visual parity"], docs["Documentation"]
  axis rollout["Rollout"], rollback["Rollback"]
  curve candidate["Candidate"] { 91, 84, 88, 72, 95 }
  curve baseline["Baseline"] { 78, 76, 82, 86, 89 }
  min 20
  max 100
  ticks 5
`,
  },
  {
    id: 'rc_radar_operational_risk',
    kind: 'radar',
    title: 'Operational risk profile',
    scenario: 'Detailed entries are reordered against declared axes on a polygon grid.',
    aspectRatio: 1,
    source: String.raw`
radar-beta:
  title Operational risk
  axis impact["Impact"], likelihood["Likelihood"], exposure["Exposure"], recovery["Recovery"]
  curve current["Current"] { recovery: 45, likelihood: 72, impact: 88, exposure: 64 }
  curve mitigated["Mitigated"] { exposure 32, impact 46, recovery 81, likelihood 38 }
  graticule polygon
  showLegend true
  ticks 4
  max 100
`,
  },
  {
    id: 'rc_radar_compact_capacity',
    kind: 'radar',
    title: 'Compact capacity profile',
    scenario: 'Intrinsic sizing, compact margins, scaled axes, and a hidden legend share one chart.',
    aspectRatio: 1.2,
    source: String.raw`
---
config:
  radar:
    width: 480
    height: 360
    marginTop: 32
    marginRight: 44
    marginBottom: 36
    marginLeft: 44
    axisScaleFactor: 0.82
    axisLabelFactor: 0.94
    curveTension: 0.05
    useMaxWidth: false
---
radar-beta
  title Capacity profile
  axis cpu["CPU"], memory["Memory"], network["Network"], storage["Storage"]
  curve reserved["Reserved"] { 72, 81, 64, 77 }
  curve used["Used"] { 58, 69, 51, 63 }
  showLegend false
  max 100
`,
  },
  {
    id: 'rc_radar_inferred_scale',
    kind: 'radar',
    title: 'Inferred maximum scorecard',
    scenario: 'The renderer derives its maximum from several circular curves and uses seven ticks.',
    aspectRatio: 1,
    source: String.raw`
radar-beta
  title Inferred service score
  axis latency["Latency"], throughput["Throughput"], availability["Availability"]
  axis efficiency["Efficiency"], support["Support"], adoption["Adoption"]
  curve alpha["Alpha"] { 42, 67, 88, 53, 71, 64 }
  curve beta["Beta"] { 76, 58, 82, 69, 61, 79 }
  curve gamma["Gamma"] { 63, 73, 91, 74, 68, 57 }
  ticks 7
  graticule circle
`,
  },
  {
    id: 'rc_radar_accessible_regions',
    kind: 'radar',
    title: 'Accessible regional scorecard',
    scenario: 'Accessibility metadata, comments, entities, escaped labels, and Unicode remain ordered.',
    aspectRatio: 1,
    source: String.raw`
radar-beta
  title 地域 readiness
  accTitle: Accessible regional readiness
  accDescr {
    Regional readiness values across four delivery axes.
  }
  %% Mixed scripts exercise labels and detailed entry references.
  axis tokyo["東京"], seoul["서울"], sao["São Paulo"], quality["Quality \"index\""]
  curve observed["Observed &amp; verified"] { quality: 86, tokyo: 93, sao: 75, seoul: 82 }
  curve target["Target"] { tokyo: 96, seoul: 91, sao: 88, quality: 94 }
  graticule polygon
  max 100
`,
  },
  {
    id: 'rc_sankey_platform_delivery',
    kind: 'sankey',
    title: 'Platform delivery flow',
    scenario: 'A production delivery graph combines branching, merging, and multiple stages.',
    aspectRatio: 1.5,
    source: String.raw`
sankey
Demand,Planning,120
Planning,Build,85
Planning,Deferred,35
Build,Verification,70
Build,Rework,15
Rework,Verification,12
Verification,Release,76
Verification,Rejected,6
`,
  },
  {
    id: 'rc_sankey_revenue_allocation',
    kind: 'sankey',
    title: 'Revenue allocation',
    scenario: 'Currency labels, outlined text, custom node colors, and source links are combined.',
    aspectRatio: 1.5,
    source: String.raw`
---
config:
  sankey:
    prefix: "$"
    suffix: "M"
    labelStyle: outlined
    linkColor: source
    nodeColors:
      Revenue: "#2563eb"
      Product: "#16a34a"
      Operations: "#dc2626"
---
sankey
Revenue,Product,72.5
Revenue,Operations,38.25
Revenue,Research,19.25
Product,Growth,54
Operations,Reliability,31
Research,Growth,16
`,
  },
  {
    id: 'rc_sankey_regional_routing',
    kind: 'sankey',
    title: 'Regional routing',
    scenario: 'Quoted CSV labels with commas and escaped quotes use target-colored links.',
    aspectRatio: 1.5,
    source: String.raw`
---
config:
  sankey:
    linkColor: target
    nodeAlignment: right
---
sankey
"North, primary","Validation ""green""",42
"South, secondary","Validation ""green""",36
"Validation ""green""",Deployment,70
"Validation ""green""",Hold,8
`,
  },
  {
    id: 'rc_sankey_compact_processing',
    kind: 'sankey',
    title: 'Compact processing graph',
    scenario: 'Intrinsic dimensions, narrow nodes, compact padding, and hidden values are exercised.',
    aspectRatio: 1.75,
    source: String.raw`
---
title: Compact processing
config:
  sankey:
    width: 700
    height: 400
    nodeWidth: 7
    nodePadding: 5
    nodeAlignment: center
    showValues: false
    useMaxWidth: false
---
sankey-beta
Input,Decode,90
Decode,Transform,82
Decode,Invalid,8
Transform,Store,77
Transform,Retry,5
`,
  },
  {
    id: 'rc_sankey_observability_pipeline',
    kind: 'sankey',
    title: 'Observability pipeline',
    scenario: 'A fixed-color flow joins telemetry sources into storage and alerting outputs.',
    aspectRatio: 1.5,
    source: String.raw`
---
config:
  sankey:
    linkColor: "#64748b"
    nodeAlignment: left
---
sankey
Metrics,Collector,58
Logs,Collector,76
Traces,Collector,42
Collector,Hot storage,96
Collector,Cold storage,54
Collector,Alerts,26
`,
  },
  {
    id: 'rc_treemap_delivery_portfolio',
    kind: 'treemap',
    title: 'Delivery portfolio',
    scenario: 'Nested product areas and work streams compare release investment at three levels.',
    aspectRatio: 1.45,
    source: String.raw`
treemap-beta
title Delivery portfolio
"Client"
    "Android"
        "Foundation": 38
        "Features": 62
    "iOS"
        "Foundation": 34
        "Features": 56
"Platform"
    "Reliability": 48
    "Developer tooling": 29
`,
  },
  {
    id: 'rc_treemap_styled_risk',
    kind: 'treemap',
    title: 'Styled risk allocation',
    scenario: 'Section and leaf classes override fills, strokes, text, widths, and font style.',
    aspectRatio: 1.4,
    source: String.raw`
treemap-beta
"Release risk"
    "Critical":::critical
        "Parser gap": 24
        "Layout drift": 18
    "Controlled": 42:::verified
    "Accepted": 16
classDef critical fill:#fee2e2,stroke:#dc2626,stroke-width:3px,color:#7f1d1d;
classDef verified fill:#dcfce7,stroke:#16a34a,color:#14532d,font-style:italic;
`,
  },
  {
    id: 'rc_treemap_financial_formatting',
    kind: 'treemap',
    title: 'Formatted annual budget',
    scenario: 'Intrinsic sizing, compact padding, custom fonts, and currency values combine.',
    aspectRatio: 1.6,
    source: String.raw`
---
config:
  treemap:
    useMaxWidth: false
    padding: 6
    diagramPadding: 20
    nodeWidth: 120
    nodeHeight: 48
    borderWidth: 2
    valueFontSize: 13
    labelFontSize: 15
    valueFormat: '$0,0'
---
treemap
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
    id: 'rc_treemap_percentage_share',
    kind: 'treemap',
    title: 'Percentage market share',
    scenario: 'Fractional leaves are formatted as percentages across several outer groups.',
    aspectRatio: 1.35,
    source: String.raw`
---
config:
  treemap:
    valueFormat: '.1%'
---
treemap-beta
"Core market"
    "Company A": 0.35
    "Company B": 0.25
"Growth market"
    "Company C": 0.15
    "Others": 0.25
`,
  },
  {
    id: 'rc_treemap_accessible_regions',
    kind: 'treemap',
    title: 'Accessible regional capacity',
    scenario: 'Metadata, comments, hidden values, mixed scripts, and irregular indentation coexist.',
    aspectRatio: 1.4,
    source: String.raw`
---
title: Regional capacity
config:
  treemap:
    showValues: false
    labelFontSize: 15
---
treemap
accTitle: Accessible regional capacity
accDescr {
  Capacity grouped by international operating region.
}
%% Different indentation widths still express parent-child relationships.
"アジア"
  "North Asia"
      "東京": 44
      "서울": 36
"Europa"
    "München": 28
    "Zürich": 22
`,
  },
  {
    id: 'rc_venn_release_decision',
    kind: 'venn',
    title: 'Release decision model',
    scenario: 'Three weighted review domains and their pairwise and central overlaps drive a launch decision.',
    aspectRatio: 1.75,
    source: String.raw`
venn-beta
  title Release decision
  set Value["Customer value"]:30
  set Delivery["Delivery confidence"]:26
  set Safety["Operational safety"]:22
  union Value,Delivery["Validated scope"]:9
  union Value,Safety["Responsible outcome"]:7
  union Delivery,Safety["Controlled rollout"]:8
  union Value,Delivery,Safety["Launch"]:3
`,
  },
  {
    id: 'rc_venn_capability_catalog',
    kind: 'venn',
    title: 'Capability catalog',
    scenario: 'Indented and explicit text nodes populate independent and overlapping ownership areas.',
    aspectRatio: 1.75,
    source: String.raw`
venn-beta
  set Client["Client capabilities"]:24
    text C1["Rendering"]
    text C2["Offline state"]
  set Service["Service capabilities"]:22
    text S1["Persistence"]
    text S2["Scheduling"]
  union Client,Service["Shared contract"]:8
    text CS1["Schema"]
text Client,Service CS2["Telemetry"]
`,
  },
  {
    id: 'rc_venn_styled_ownership',
    kind: 'venn',
    title: 'Styled ownership map',
    scenario: 'Independent set, shared-region, and text styles exercise merged paint declarations.',
    aspectRatio: 1.75,
    source: String.raw`
venn-beta
  title Ownership review
  set Product["Product"]:22
    text P1["Roadmap"]
  set Engineering["Engineering"]:20
    text E1["Implementation"]
  union Product,Engineering["Planning"]:7
  style Product fill:#2563eb,stroke:#1e3a8a,stroke-width:4px,fill-opacity:0.2
  style Engineering fill:rgb(22, 163, 74),stroke:#14532d,fill-opacity:0.18
  style Product,Engineering fill:rgba(250, 204, 21, 0.35),color:#111827
  style P1 color:#dc2626
  style E1 color:#166534
`,
  },
  {
    id: 'rc_venn_intrinsic_geometry',
    kind: 'venn',
    title: 'Intrinsic geometry controls',
    scenario: 'A fixed viewport, compact padding, debug guides, and theme variables shape a weighted overlap.',
    aspectRatio: 1.6,
    source: String.raw`
---
title: Intrinsic service boundary
config:
  theme: neutral
  themeVariables:
    venn1: "#7c3aed"
    venn2: "#0891b2"
    vennTitleTextColor: "#18181b"
  venn:
    width: 720
    height: 450
    padding: 20
    useMaxWidth: false
    useDebugLayout: true
---
venn-beta
  set Runtime["Runtime"]:28
    text R1["Lifecycle"]
  set Storage["Storage"]:24
    text S1["Durability"]
  union Runtime,Storage["State contract"]:8
`,
  },
  {
    id: 'rc_venn_international_regions',
    kind: 'venn',
    title: 'International regional collaboration',
    scenario: 'Comments, quoted identifiers, Unicode, and a three-region overlap remain stable.',
    aspectRatio: 1.75,
    source: String.raw`
venn-beta
  title 地域 collaboration
  %% Quoted identifiers preserve spaces while labels preserve mixed scripts.
  set "Tokyo Team"["東京"]:24
  set "Seoul Team"["서울"]:22
  set "Sao Paulo Team"["São Paulo"]:20
  union "Tokyo Team","Seoul Team"["共同運用"]:7
  union "Tokyo Team","Seoul Team","Sao Paulo Team"["Global"]:3
`,
  },
  {
    id: 'rc_ishikawa_release_regression',
    kind: 'ishikawa',
    title: 'Release regression analysis',
    scenario: 'Four alternating ownership domains and nested evidence explain a release regression.',
    aspectRatio: 1.85,
    source: String.raw`
ishikawa-beta
Release regression
  Process
    Missing review
    Incomplete checklist
  People
    Ownership gap
  Platform
    Capacity limit
      Saturated worker pool
  Environment
    Regional dependency
`,
  },
  {
    id: 'rc_ishikawa_service_latency',
    kind: 'ishikawa',
    title: 'Service latency analysis',
    scenario: 'A broad cause tree stresses alternating branches and uneven descendant counts.',
    aspectRatio: 2,
    source: String.raw`
ishikawa
Elevated service latency
  Application
    Synchronous fan-out
    Large response payload
    Excessive retries
  Data
    Missing index
      Full table scan
  Network
    Cross-region route
  Operations
    Delayed mitigation
    Incomplete runbook
`,
  },
  {
    id: 'rc_ishikawa_normalized_indentation',
    kind: 'ishikawa',
    title: 'Normalized indentation hierarchy',
    scenario: 'The first cause establishes a baseline below an over-indented effect and irregular child depths.',
    aspectRatio: 1.8,
    source: String.raw`
ishikawa-beta
      Checkout failure
Client
   Stale state
       Missing refresh
Service
  Dependency timeout
     Retry storm
`,
  },
  {
    id: 'rc_ishikawa_responsive_regions',
    kind: 'ishikawa',
    title: 'Responsive regional analysis',
    scenario: 'Frontmatter config, comments, entities, and multilingual labels render in a responsive viewport.',
    aspectRatio: 1.8,
    source: String.raw`
---
title: Regional reliability analysis
config:
  theme: neutral
  ishikawa:
    diagramPadding: 30
    useMaxWidth: true
---
%% Mixed scripts and HTML-compatible text exercise sanitization.
ishikawa-beta
顧客影響 &amp; latency
  東京 edge
    Cache&lt;br/&gt;miss
  서울 routing
    Retry storm
  São Paulo capacity
    Saturated workers
`,
  },
  {
    id: 'rc_ishikawa_long_evidence',
    kind: 'ishikawa',
    title: 'Long evidence hierarchy',
    scenario: 'Long effect and cause labels exercise multiline wrapping across a deep hierarchy.',
    aspectRatio: 2,
    source: String.raw`
---
config:
  ishikawa:
    diagramPadding: 18
    useMaxWidth: false
---
ishikawa
End to end production compatibility verification failure
  Asynchronous replication completion evidence missing
    Cross region validation label not recorded
      Deterministic rendering verification evidence unavailable
  Operational readiness and rollback evidence incomplete
    Customer facing result confirmation evidence delayed
`,
  },
  {
    id: 'rc_cynefin_product_portfolio',
    kind: 'cynefin',
    title: 'Product portfolio decisions',
    scenario: 'A complete framework classifies product work across all five decision domains.',
    aspectRatio: 4 / 3,
    source: String.raw`
cynefin-beta
  title Product portfolio decisions
  complex
    "Discover a new market"
    "Prototype recommendation quality"
  complicated
    "Model unit economics"
    "Review regulatory constraints"
  clear
    "Run release checklist"
  chaotic
    "Contain active data loss"
  confusion
    "Unclassified customer signal"
`,
  },
  {
    id: 'rc_cynefin_operational_transitions',
    kind: 'cynefin',
    title: 'Operational domain transitions',
    scenario: 'Operational work moves between domains as teams learn, codify, and stabilize.',
    aspectRatio: 4 / 3,
    source: String.raw`
cynefin-beta
  title Operational transitions
  complex
    "Probe intermittent latency"
  complicated
    "Analyze dependency behavior"
  clear
    "Automate known mitigation"
  chaotic
    "Restore critical traffic"
  complex --> complicated : "Evidence converges"
  complicated --> clear : "Runbook approved"
  clear --> chaotic : "Control fails"
  chaotic --> complex : "Impact contained"
`,
  },
  {
    id: 'rc_cynefin_confusion_backlog',
    kind: 'cynefin',
    title: 'Unclassified work backlog',
    scenario: 'A crowded confusion domain exercises the compact overflow presentation and outgoing decisions.',
    aspectRatio: 4 / 3,
    source: String.raw`
cynefin-beta
  confusion
    "Unknown ownership"
    "Unknown blast radius"
    "Unknown urgency"
    "Unknown dependency"
    "Unknown customer segment"
    "Unknown compliance impact"
  confusion --> chaotic : "Immediate containment"
  confusion --> complex : "Safe experiment"
`,
  },
  {
    id: 'rc_cynefin_intrinsic_workshop',
    kind: 'cynefin',
    title: 'Intrinsic workshop template',
    scenario: 'A compact fixed-size worksheet uses straight boundaries and hides explanatory subtitles.',
    aspectRatio: 1.4,
    source: String.raw`
---
title: Decision workshop
config:
  cynefin:
    width: 640
    height: 480
    padding: 18
    showDomainDescriptions: false
    boundaryAmplitude: 0
    seed: 101
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
    id: 'rc_cynefin_accessible_regions',
    kind: 'cynefin',
    title: 'Accessible regional framework',
    scenario: 'Theme overrides, metadata, entities, comments, and multilingual labels form one responsive diagram.',
    aspectRatio: 4 / 3,
    source: String.raw`
---
title: Regional decisions
config:
  theme: dark
  themeVariables:
    cynefin:
      boundaryColor: "#e2e8f0"
      cliffColor: "#fb7185"
      confusionBg: "#581c87"
  cynefin:
    boundaryAmplitude: 6
    seed: 202
    useMaxWidth: true
---
cynefin-beta
  accTitle: Regional Cynefin framework
  accDescr {
    International operating decisions grouped by uncertainty.
  }
  %% Mixed scripts and an encoded ampersand exercise shared decoding.
  complex
    "東京 discovery"
  complicated
    "München &amp; analysis"
  clear
    "서울 runbook"
  chaotic
    "São Paulo incident"
`,
  },
  {
    id: 'rc_block_service_platform',
    kind: 'block',
    title: 'Layered service platform',
    scenario: 'Nested service boundaries combine explicit grids, storage shapes, and cross-boundary links.',
    aspectRatio: 1.7,
    source: String.raw`
block
  columns 3
  clients(["Clients"]) gateway["Gateway"] auth("Authorized?")
  block:services:2
    columns 2
    catalog["Catalog"] orders["Orders"]
    billing["Billing"] notify["Notifications"]
  end
  data[("Primary data")]
  clients --> gateway
  gateway --> auth
  auth --> catalog
  orders --> data
  billing --> data
`,
  },
  {
    id: 'rc_block_delivery_pipeline',
    kind: 'block',
    title: 'Delivery pipeline grid',
    scenario: 'Spans, spaces, labelled links, and mixed edge patterns model a guarded delivery path.',
    aspectRatio: 1.8,
    source: String.raw`
block
  columns 4
  source["Source"]:2 build["Build"] package["Package"]
  space verify("Verified?"):2 publish["Publish"]
  rollback["Rollback"]:2 complete((("Complete"))):2
  source -- "commit" --> build
  build ==> package
  package -.-> verify
  verify --> publish
  verify --x rollback
  publish --> complete
`,
  },
  {
    id: 'rc_block_direction_matrix',
    kind: 'block',
    title: 'Directional handoff matrix',
    scenario: 'Every block-arrow direction and axis alias appears in a dense operational handoff grid.',
    aspectRatio: 1.9,
    source: String.raw`
block-beta
  columns 4
  intake["Intake"] east<["East"]>(right) review["Review"] south<["South"]>(down)
  west<["West"]>(left) horizontal<["Horizontal"]>(x) vertical<["Vertical"]>(y) north<["North"]>(up)
  retry["Retry"] mixed<["Escalate"]>(right, down) resolve["Resolve"] done(("Done"))
  audit["Audit"] validate["Validate"] publish["Publish"] archive["Archive"]
`,
  },
  {
    id: 'rc_block_styled_boundaries',
    kind: 'block',
    title: 'Styled ownership boundaries',
    scenario: 'Composite palette slots, reusable classes, and inline overrides distinguish ownership.',
    aspectRatio: 1.65,
    source: String.raw`
---
config:
  theme: redux-color
  look: classic
  themeVariables:
    bkgColorArray: ["#e0f2fe", "#dcfce7", "#fef3c7"]
    borderColorArray: ["#0369a1", "#15803d", "#b45309"]
---
block
  columns 3
  block:edge
    ingress["Ingress"]
    cache[("Cache")]
  end
  block:compute
    worker["Worker"]
    scheduler["Scheduler"]
  end
  block:storage
    database[("Database")]
    archive[["Archive"]]
  end
  classDef active fill:#dcfce7,stroke:#15803d,color:#14532d;
  class worker,scheduler active
  style archive fill:#fef3c7,stroke:#b45309,stroke-width:3px
`,
  },
  {
    id: 'rc_block_regional_architecture',
    kind: 'block',
    title: 'Regional architecture map',
    scenario: 'Intrinsic sizing, title metadata, entities, comments, Unicode, and overflow spans share one diagram.',
    aspectRatio: 1.75,
    source: String.raw`
---
title: Regional processing architecture
config:
  theme: dark
  block:
    padding: 18
    useMaxWidth: false
---
block
  %% The first row intentionally exceeds the configured column count.
  columns 2
  entry["Paris &amp; 東京"]:3
  route("Region?") right<["Route"]>(right)
  seoul["서울 processor"] sao[("São Paulo store")]
  route --> seoul
  route --> sao
`,
  },
  {
    id: 'rc_eventmodeling_order_lifecycle',
    kind: 'eventmodeling',
    title: 'Order lifecycle event model',
    scenario: 'A complete state-change and state-view loop crosses all three default lanes.',
    aspectRatio: 1.7,
    source: String.raw`
eventmodeling
  tf 01 ui OrderForm
  tf 02 cmd SubmitOrder { orderId: O-42 }
  tf 03 evt OrderSubmitted
  tf 04 rmo OrderSummary
  tf 05 ui OrderStatus
`,
  },
  {
    id: 'rc_eventmodeling_inventory_translation',
    kind: 'eventmodeling',
    title: 'Inventory translation flow',
    scenario: 'A reset event starts an automation pattern in qualified namespace lanes.',
    aspectRatio: 1.45,
    source: String.raw`
eventmodeling
  rf 01 evt External.InventoryChanged
  tf 02 readmodel Inventory.ExternalInventory
  tf 03 processor Inventory.Projector
  tf 04 command Inventory.ApplyChange
  tf 05 event Inventory.ChangeApplied
`,
  },
  {
    id: 'rc_eventmodeling_multi_event_projection',
    kind: 'eventmodeling',
    title: 'Multi-event order projection',
    scenario: 'A read model explicitly joins four independent event streams.',
    aspectRatio: 1.9,
    source: String.raw`
eventmodeling
  rf 01 evt OrderCreated
  rf 02 evt ItemAdded
  rf 03 evt ItemRemoved
  rf 04 evt OrderCancelled
  tf 05 rmo OrderHistory ->> 01 ->> 02 ->> 03 ->> 04
  tf 06 ui OrderHistoryPage
`,
  },
  {
    id: 'rc_eventmodeling_explicit_payload_trace',
    kind: 'eventmodeling',
    title: 'Explicit payload trace',
    scenario: 'Commands and events carry inline payloads through an explicitly connected flow.',
    aspectRatio: 1.55,
    source: String.raw`
eventmodeling
  rf 01 ui Checkout
  tf 02 cmd AuthorizePayment ->> 01 { paymentId: P-7, amount: 125.50 }
  tf 03 evt PaymentAuthorized ->> 02 { paymentId: P-7, status: approved }
`,
  },
  {
    id: 'rc_eventmodeling_themed_regional',
    kind: 'eventmodeling',
    title: 'Themed regional event model',
    scenario: 'Title metadata, theme overrides, comments, entities, and multilingual payload data share one intrinsic diagram.',
    aspectRatio: 1.6,
    source: String.raw`
---
title: Regional request lifecycle
config:
  theme: dark
  eventmodeling:
    padding: 20
    rowHeight: 40
    useMaxWidth: false
  themeVariables:
    emUiFill: "#164e63"
    emCommandFill: "#1d4ed8"
    emEventFill: "#b45309"
---
eventmodeling
  %% Entity-safe mixed scripts are carried by the payload.
  tf 01 ui RegionalRequest { region: 東京 &amp; 서울 }
  tf 02 cmd QueueRequest
  tf 03 evt RequestQueued
`,
  },
  {
    id: 'rc_agentflow_release_intelligence',
    kind: 'agentflow',
    title: 'Release intelligence workflow',
    scenario: 'Two collaborating flows use typed work nodes and a top-level handoff.',
    layout: 'dagre',
    aspectRatio: 4 / 3,
    source: String.raw`
---
config:
  layout: dagre
  theme: default
  look: classic
---
agentflow-beta TB
  request["Release request"]@{ shape: input }
  flow planner["Planning Agent"]
    inspect["Inspect changes"]@{ shape: task }
    classify["Classify risk"]@{ shape: decision }
    inspect --> classify
  end
  flow publisher["Publishing Agent"]
    package["Build artifacts"]@{ shape: tool }
    announce["Publish release"]@{ shape: action }
    package --> announce
  end
  request --> planner --> publisher
`,
  },
  {
    id: 'rc_agentflow_failure_recovery',
    kind: 'agentflow',
    title: 'Failure recovery workflow',
    scenario: 'Labelled sequence, failure, and reference edges remain visually distinct.',
    layout: 'dagre',
    aspectRatio: 1.5,
    source: String.raw`
---
config:
  layout: dagre
  theme: default
  look: classic
---
agentflow-beta LR
  validate["Validate package"]@{ shape: decision }
  deploy["Deploy"]@{ shape: action }
  repair["Repair package"]@{ shape: task }
  runbook["Recovery runbook"]@{ shape: refdoc }
  validate -- valid --> deploy
  validate -- invalid --> repair
  repair --x validate
  repair -.- runbook
`,
  },
  {
    id: 'rc_agentflow_shared_policy',
    kind: 'agentflow',
    title: 'Shared policy reference',
    scenario: 'Nested agents reference a global policy without moving it into either flow.',
    layout: 'dagre',
    aspectRatio: 4 / 3,
    source: String.raw`
---
config:
  layout: dagre
  theme: default
  look: classic
---
agentflow-beta TB
  global
    policy["Deployment policy"]@{ shape: refdoc }
  end
  flow delivery["Delivery Team"]
    flow verifier["Verification Agent"]
      verify["Verify evidence"]@{ shape: task }
      verify -.- policy
    end
    flow approver["Approval Agent"]
      approve["Approve release"]@{ shape: decision }
      approve -.- policy
    end
    verifier --> approver
  end
`,
  },
  {
    id: 'rc_agentflow_collapsed_enrichment',
    kind: 'agentflow',
    title: 'Collapsed enrichment flow',
    scenario: 'Edges crossing an intentionally collapsed flow terminate at its summary node.',
    layout: 'dagre',
    aspectRatio: 1.4,
    source: String.raw`
---
config:
  layout: dagre
  theme: default
  look: classic
---
agentflow-beta TB
  source["Incoming record"]@{ shape: input }
  flow enrichment["Enrichment Agent"]
    normalize["Normalize fields"]@{ shape: task }
    lookup["Lookup context"]@{ shape: tool }
    normalize --> lookup
  end
  enrichment@{ view: "collapsed" }
  sink["Store result"]@{ shape: action }
  source --> normalize
  lookup --> sink
`,
  },
  {
    id: 'rc_agentflow_regional_connector',
    kind: 'agentflow',
    title: 'Regional connector workflow',
    scenario: 'Configuration, metadata, accessibility text, entities, and multilingual labels share one diagram.',
    layout: 'dagre',
    aspectRatio: 1.6,
    source: String.raw`
---
title: Regional support workflow
config:
  layout: dagre
  theme: neutral
  look: classic
  agentflow:
    nodeSpacing: 62
    rankSpacing: 74
    useMaxWidth: false
---
agentflow-beta LR
  accTitle: Regional support workflow
  accDescr: International requests are sent to an external support connector
  connector support["Support API"]
  support@{ protocol: "http", endpoint: "https://example.com/support" }
  request["東京 &amp; 서울 request"]@{ shape: input, value: "São Paulo" }
  create["create_ticket"]@{
    shape: tool
    connectorRef: "support.create"
    params: "request :: String"
    returns: "Ticket"
  }
  request --> create
`,
  },
  {
    id: 'rc_architecture_cloud_runtime',
    kind: 'architecture',
    title: 'Cloud runtime',
    scenario: 'Nested runtime groups connect an edge gateway to compute and persistence.',
    aspectRatio: 1.7,
    source: String.raw`
architecture-beta
  group cloud(cloud)[Cloud runtime]
  group compute(server)[Compute] in cloud
  group persistence(database)[Persistence] in cloud
  service edge(internet)[Edge gateway] in cloud
  service api(server)[Public API] in compute
  service worker(server)[Worker] in compute
  service data(database)[Primary data] in persistence
  edge{group}:R --> L:api{group}
  api:B --> T:worker
  worker{group}:R --> L:data{group}
`,
  },
  {
    id: 'rc_architecture_junction_routes',
    kind: 'architecture',
    title: 'Junction routes',
    scenario: 'Two invisible junctions branch and merge a multi-service route.',
    aspectRatio: 1.55,
    source: String.raw`
architecture-beta
  service ingress(internet)[Ingress]
  junction fanout
  service commands(server)[Commands]
  service queries(server)[Queries]
  junction merge
  service store(database)[Store]
  ingress:R --> L:fanout
  fanout:T --> B:commands
  fanout:B --> T:queries
  commands:R --> L:merge
  queries:R --> L:merge
  merge:R --> L:store
`,
  },
  {
    id: 'rc_architecture_alignment_grid',
    kind: 'architecture',
    title: 'Aligned processing grid',
    scenario: 'Combined row and column directives constrain six connected services.',
    aspectRatio: 1.45,
    source: String.raw`
architecture-beta
  service input_a(server)[Input A]
  service input_b(server)[Input B]
  service process_a(server)[Process A]
  service process_b(server)[Process B]
  service output_a(disk)[Output A]
  service output_b(disk)[Output B]
  input_a:B --> T:process_a
  input_b:B --> T:process_b
  process_a:B --> T:output_a
  process_b:B --> T:output_b
  align row input_a input_b
  align row process_a process_b
  align row output_a output_b
  align column input_a process_a output_a
  align column input_b process_b output_b
`,
  },
  {
    id: 'rc_architecture_edge_contracts',
    kind: 'architecture',
    title: 'Directional edge contracts',
    scenario: 'Straight and bent routes combine labels, arrows, and bidirectional markers.',
    aspectRatio: 1.7,
    source: String.raw`
architecture-beta
  service client(internet)[Client]
  service gateway(server)[Gateway]
  service cache(disk)[Cache]
  service database(database)[Database]
  client:R --> L:gateway
  gateway:B -[lookup]-> T:cache
  cache:R <--> L:database
  database:T <-- B:gateway
`,
  },
  {
    id: 'rc_architecture_configured_regional',
    kind: 'architecture',
    title: 'Configured regional services',
    scenario: 'Seeded configuration, icon text, metadata, and Unicode labels share one group.',
    aspectRatio: 1.8,
    source: String.raw`
---
title: Regional service architecture
config:
  architecture:
    padding: 36
    iconSize: 72
    nodeSeparation: 96
    idealEdgeLengthMultiplier: 1.6
    edgeElasticity: 0.5
    numIter: 1200
    seed: 31
---
architecture-beta
  accTitle: Regional service architecture
  accDescr: Requests move through Tokyo, Seoul, and Sao Paulo.
  group regions(cloud)[Regions]
  service tokyo "東京"[受付] in regions
  service seoul(server)[서울 검토] in regions
  service sao(database)[São Paulo] in regions
  tokyo:R --> L:seoul
  seoul:R -[承認]-> L:sao
  align row tokyo seoul sao
`,
  },
  {
    id: 'rc_c4_context_ecosystem',
    kind: 'c4',
    title: 'Partner ordering ecosystem',
    scenario: 'Customers, operators, and partner systems surround the central ordering platform.',
    aspectRatio: 1.7,
    source: String.raw`
C4Context
  title Partner ordering ecosystem
  Person(customer, "Customer", "Creates and tracks orders")
  Person_Ext(operator, "Support Operator", "Resolves exceptions")
  System(platform, "Ordering Platform", "Coordinates fulfilment")
  System_Ext(payment, "Payment Network", "Authorizes transactions")
  SystemQueue_Ext(carrier, "Carrier Events", "Publishes delivery updates")
  Rel(customer, platform, "Places orders")
  Rel(operator, platform, "Investigates orders")
  Rel(platform, payment, "Authorizes payment", "HTTPS")
  Rel(carrier, platform, "Publishes status", "Events")
`,
  },
  {
    id: 'rc_c4_container_eventing',
    kind: 'c4',
    title: 'Event-driven containers',
    scenario: 'A bounded application routes commands and events through storage and queues.',
    aspectRatio: 1.75,
    source: String.raw`
C4Container
  Person(user, "Operations User")
  System_Boundary(ops, "Operations Platform") {
    Container(portal, "Operations Portal", "Kotlin/Wasm")
    Container(command, "Command API", "Kotlin/JVM")
    ContainerQueue(events, "Operations Events", "Kafka")
    ContainerDb(store, "Operations Store", "PostgreSQL")
  }
  Rel(user, portal, "Uses", "HTTPS")
  Rel(portal, command, "Submits commands", "JSON")
  Rel(command, store, "Persists state", "SQL")
  Rel(command, events, "Publishes changes", "Events")
`,
  },
  {
    id: 'rc_c4_component_policy',
    kind: 'c4',
    title: 'Policy evaluation components',
    scenario: 'Styled components collaborate inside a policy service boundary.',
    aspectRatio: 1.65,
    source: String.raw`
C4Component
  Container_Boundary(policy, "Policy Service") {
    Component(endpoint, "Policy Endpoint", "HTTP")
    Component(evaluator, "Policy Evaluator", "Kotlin")
    ComponentDb(rules, "Rule Repository", "SQL")
    ComponentQueue(audit, "Audit Publisher", "Kafka")
  }
  Rel(endpoint, evaluator, "Evaluates request")
  Rel(evaluator, rules, "Loads rules")
  Rel(evaluator, audit, "Publishes decision")
  UpdateElementStyle(evaluator, $bgColor="#1d4ed8", $fontColor="#ffffff", $borderColor="#1e3a8a", $shape="component")
  UpdateRelStyle(evaluator, audit, $textColor="#9a3412", $lineColor="#ea580c", $offsetX="8", $offsetY="-10")
`,
  },
  {
    id: 'rc_c4_dynamic_recovery',
    kind: 'c4',
    title: 'Checkout recovery sequence',
    scenario: 'A numbered dynamic flow shows a failed authorization and compensating response.',
    aspectRatio: 1.8,
    source: String.raw`
C4Dynamic
  Person(customer, "Customer")
  Container(checkout, "Checkout UI", "Kotlin/Wasm")
  Container(orchestrator, "Checkout Orchestrator", "Kotlin/JVM")
  System_Ext(payment, "Payment Provider")
  ContainerQueue(events, "Order Events", "Kafka")
  Rel(customer, checkout, "Confirms purchase")
  Rel(checkout, orchestrator, "Starts checkout")
  Rel(orchestrator, payment, "Requests authorization")
  Rel_Back(payment, orchestrator, "Declines payment")
  Rel(orchestrator, events, "Publishes failure")
  Rel(orchestrator, checkout, "Returns recovery options")
`,
  },
  {
    id: 'rc_c4_deployment_multiregion',
    kind: 'c4',
    title: 'Configured multi-region deployment',
    scenario: 'Nested deployment nodes, metadata, multilingual labels, and scoped sizing share one view.',
    aspectRatio: 1.8,
    source: String.raw`
---
title: Multi-region deployment
config:
  c4:
    diagramMarginX: 42
    diagramMarginY: 18
    c4ShapeInRow: 2
    c4BoundaryInRow: 2
    wrap: true
    containerFontSize: 15
---
C4Deployment
  accTitle: Multi-region deployment
  accDescr: Traffic reaches Tokyo and Seoul before data replication completes.
  Deployment_Node(cloud, "Global Cloud", "Managed infrastructure") {
    Node_L(tokyo, "東京 Region", "Linux") {
      Container(apiTokyo, "受付 API", "Kotlin/JVM")
    }
    Node_R(seoul, "서울 Region", "Linux") {
      Container(apiSeoul, "검토 API", "Kotlin/JVM")
    }
    Node(data, "Data Plane", "PostgreSQL") {
      ContainerDb(primary, "São Paulo Primary", "PostgreSQL")
    }
  }
  UpdateLayoutConfig($c4ShapeInRow="2", $c4BoundaryInRow="2")
  Rel(apiTokyo, primary, "Replicates")
  Rel(apiSeoul, primary, "Replicates")
`,
  },
  {
    id: 'rc_railroad_ir_protocol_frame',
    kind: 'railroad',
    title: 'Protocol frame grammar',
    scenario: 'Explicit Railroad constructors describe a framed message with optional metadata and repeated fields.',
    aspectRatio: 1.8,
    source: String.raw`
railroad-beta
  frame = sequence(
    terminal("SOF"),
    nonterminal("version"),
    optional(nonterminal("metadata")),
    oneOrMore(nonterminal("field")),
    special("checksum"),
    terminal("EOF")
  ) ;
  field = sequence(nonterminal("name"), terminal("="), nonterminal("value")) ;
`,
  },
  {
    id: 'rc_railroad_ebnf_command_language',
    kind: 'railroad',
    title: 'Command language grammar',
    scenario: 'A command parser combines alternatives, optional flags, and repeated arguments.',
    aspectRatio: 1.75,
    source: String.raw`
railroad-ebnf-beta
  command = action [ flag ] { argument } ;
  action = "deploy" | "inspect" | "rollback" ;
  flag = "--dry-run" | "--force" ;
  argument = name "=" value ;
`,
  },
  {
    id: 'rc_railroad_ebnf_query_pipeline',
    kind: 'railroad',
    title: 'Query pipeline grammar',
    scenario: 'ISO EBNF constructs describe filters, projections, ordering, and bounded-looking clauses.',
    aspectRatio: 1.8,
    source: String.raw`
railroad-ebnf-beta
  (* Query pipeline *)
  query = source, { filter }, [ projection ], [ ordering ] ;
  source = "from" identifier ;
  filter = "where" expression ;
  projection = "select" identifier { "," identifier } ;
  ordering = "order" "by" identifier ;
  identifier = letter, { letter | digit | "_" }, ? normalized name ? ;
`,
  },
  {
    id: 'rc_railroad_abnf_message_envelope',
    kind: 'railroad',
    title: 'Message envelope grammar',
    scenario: 'ABNF repetition and numeric terminals define a transport envelope and payload.',
    aspectRatio: 1.8,
    source: String.raw`
railroad-abnf-beta
  envelope = version SP request-id SP payload ;
  version = "v" 1*2DIGIT ;
  request-id = 8*16%x30-39 ;
  payload = 1*( %x20-7E ) ;
`,
  },
  {
    id: 'rc_railroad_peg_policy_expression',
    kind: 'railroad',
    title: 'Policy expression grammar',
    scenario: 'PEG lookahead, ordered choice, repetition, and any-character fallback model policy expressions.',
    aspectRatio: 1.8,
    source: String.raw`
railroad-peg-beta
  # Reserved words cannot become identifiers.
  Policy <- Rule ("," Rule)* ;
  Rule <- !Reserved Identifier "=" Value ;
  Reserved <- "allow" / "deny" ;
  Identifier <- &Letter Letter Letter* ;
  Letter <- "a" / "b" / "c" / "_" ;
  Value <- "true" / "false" / . ;
`,
  },
  {
    id: 'rc_treeview_application_workspace',
    kind: 'treeview',
    title: 'Application workspace',
    scenario: 'A nested source tree combines directories, files, highlights, and descriptions.',
    aspectRatio: 1.55,
    source: String.raw`
treeView-beta
    application/
        src/
            App.kt :::highlight ## application entry point
            Router.kt ## navigation routes
        tests/
            AppTest.kt
        README.md
`,
  },
  {
    id: 'rc_treeview_box_drawing_monorepo',
    kind: 'treeview',
    title: 'Box-drawing monorepo',
    scenario: 'Tree-command output preserves a deeply nested package hierarchy.',
    aspectRatio: 1.5,
    source: String.raw`
treeView-beta
├── packages/
│   ├── core/
│   │   ├── parser.kt
│   │   └── renderer.kt
│   └── compose/
│       └── canvas.kt
├── tests/
│   └── parity.kt
└── README.md
`,
  },
  {
    id: 'rc_treeview_icon_catalog',
    kind: 'treeview',
    title: 'Configured icon catalog',
    scenario: 'Default, mapped, explicit, and suppressed icons share a single hierarchy.',
    aspectRatio: 1.6,
    source: String.raw`
---
config:
  treeView:
    showIcons: true
    filenameIcons:
      Dockerfile: folder
    extensionIcons:
      .ts: file
      .txt: none
---
treeView-beta
    source/
        App.ts icon(file)
        utils.ts
    Dockerfile
    notes.txt
`,
  },
  {
    id: 'rc_treeview_accessible_release',
    kind: 'treeview',
    title: 'Accessible release tree',
    scenario: 'Metadata and multilingual labels describe a release artifact hierarchy.',
    aspectRatio: 1.6,
    source: String.raw`
treeView-beta
  title Release artifacts
  accTitle: Accessible release artifact tree
  accDescr: Packages for Tokyo, Seoul, and Sao Paulo.
    releases/
        東京/
            android.aar
        서울/
            shared.framework
        São Paulo/
            web.zip
`,
  },
  {
    id: 'rc_treeview_themed_highlights',
    kind: 'treeview',
    title: 'Themed highlighted tree',
    scenario: 'Scoped spacing and theme variables style highlighted rows and descriptions.',
    aspectRatio: 1.65,
    source: String.raw`
---
config:
  treeView:
    rowIndent: 24
    paddingX: 8
    paddingY: 6
    lineThickness: 2
  themeVariables:
    treeView:
      labelFontSize: 18px
      labelColor: "#17324d"
      lineColor: "#486581"
      descriptionColor: "#2f855a"
      highlightBg: "rgba(255, 193, 7, 0.15)"
      highlightStroke: "#d69e2e"
---
treeView-beta
    validation/
        parser-tests.kt :::highlight ## syntax coverage
        visual-tests.kt ## geometry coverage
        release-gates.kt ## publication coverage
`,
  },
  {
    id: 'rc_swimlanes_support_escalation',
    kind: 'swimlanes',
    title: 'Support escalation',
    scenario: 'Labelled handoffs cross customer, support, and engineering lanes.',
    aspectRatio: 1.8,
    source: String.raw`
swimlane-beta LR
  subgraph Customer
    request[Request service]
    receive[Receive update]
  end
  subgraph Support
    triage[Triage request]
    answer[Send answer]
  end
  subgraph Engineering
    investigate[Investigate issue]
    fix[Prepare fix]
  end
  request --> triage
  triage -->|Known issue| answer
  triage -->|Needs code change| investigate
  investigate --> fix --> answer
  answer --> receive
`,
  },
  {
    id: 'rc_swimlanes_vertical_cycle',
    kind: 'swimlanes',
    title: 'Vertical review cycle',
    scenario: 'A TB ownership layout includes a labelled retry cycle.',
    aspectRatio: 1.35,
    source: String.raw`
swimlane-beta TB
  subgraph Intake
    collect[Collect request]
    validate[Validate details]
  end
  subgraph Review
    review[Review request]
    decide{Ready?}
  end
  subgraph Delivery
    schedule[Schedule work]
    complete[Complete work]
  end
  collect --> validate --> review --> decide
  decide -->|Yes| schedule --> complete
  decide -->|No| collect
`,
  },
  {
    id: 'rc_swimlanes_default_and_nested',
    kind: 'swimlanes',
    title: 'Default and nested lanes',
    scenario: 'A loose node and a nested team remain in their correct lane containers.',
    aspectRatio: 1.7,
    source: String.raw`
swimlane-beta LR
  external([External request])
  subgraph Fulfillment
    subgraph Warehouse
      pick[Pick items]
      pack[Pack order]
    end
    ship[Ship order]
  end
  external --> pick --> pack --> ship
`,
  },
  {
    id: 'rc_swimlanes_configured_routes',
    kind: 'swimlanes',
    title: 'Configured routing',
    scenario: 'Scoped layering and line-hop settings exercise the alternate rank path.',
    aspectRatio: 1.8,
    source: String.raw`
---
config:
  swimlane:
    lineHops: gap
    ignoreCrossLaneEdges: false
    optimizeRanksByCrossings: true
    automaticLaneOrdering: true
---
swimlane-beta TB
  subgraph Product
    plan[Plan]
  end
  subgraph Engineering
    build[Build]
  end
  subgraph Quality
    verify[Verify]
  end
  plan --> build --> verify
  plan --> verify
`,
  },
  {
    id: 'rc_swimlanes_regional_accessibility',
    kind: 'swimlanes',
    title: 'Regional accessible handoff',
    scenario: 'Accessibility metadata, entities, styling, and multilingual labels share one diagram.',
    aspectRatio: 1.8,
    source: String.raw`
---
title: Regional handoff
config:
  theme: redux-color
  look: neo
---
swimlane-beta LR
  accTitle: Regional handoff
  accDescr: Work moves from Tokyo to Seoul and Sao Paulo.
  subgraph jp [受付]
    tokyo[東京 &amp; intake]
  end
  subgraph kr [검토]
    seoul[서울 review]
  end
  subgraph br [São Paulo]
    approve[Approve]
  end
  tokyo -->|검증| seoul --> approve
  classDef active fill:#dcfce7,stroke:#15803d,color:#14532d;
  class approve active;
`,
  },
];
