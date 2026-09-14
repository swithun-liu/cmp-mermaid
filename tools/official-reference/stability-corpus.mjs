// Independent release-candidate corpus. These cases are intentionally not part
// of the demo gallery: they model larger workflows and combined syntax that is
// closer to application documentation than isolated feature examples.
export const cases = [
  {
    id: 'rc_flow_checkout_saga',
    kind: 'flowchart',
    title: 'Checkout saga with compensation',
    scenario: 'Commerce checkout coordinating inventory, payment, shipment, and rollback paths.',
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
    layout: 'elk',
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
];
