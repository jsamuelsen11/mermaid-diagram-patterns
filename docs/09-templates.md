# Validated Starter Templates

Copy-paste skeletons embodying every rule in docs 01–08: quoted labels, semantic IDs,
frontmatter title, accessibility lines, happy-path-first declaration order, verified
palette classes. All templates in this file parse with mermaid-cli (see commit-time
validation). These are the templates the skill should emit and adapt — not invent
structure from scratch.

## Flowchart — process with decision and error path

```mermaid
---
title: Order Processing
---
flowchart TB
    accTitle: Order processing flow
    accDescr: An order is validated, charged, and fulfilled; failures are refunded.

    received(["Order Received"]) --> validate{"Items in stock?"}
    validate -- "Yes" --> charge["Charge Payment"] --> fulfill["Fulfill Order"] --> done(["Shipped"])
    validate -- "No" --> notify["Notify Customer"]:::warning
    charge -- "Payment failed" --> refund["Cancel & Refund"]:::errorPath

    classDef warning fill:#b45309,stroke:#92400e,color:#fff
    classDef errorPath fill:#dc2626,stroke:#991b1b,color:#fff
```

Pattern notes: happy path declared first as one chain; branches attached after;
stadium terminators; diamond decision with labeled outcomes; styling grouped last.

## Flowchart — system architecture with subgraphs

```mermaid
---
title: Service Architecture
---
flowchart LR
    accTitle: Service architecture
    accDescr: Clients reach services through a gateway; services share a database and queue.

    client["Web Client"]:::external --> gateway["API Gateway"]

    subgraph services["Services"]
        direction TB
        orders["Order Service"]
        billing["Billing Service"]
    end

    gateway --> orders & billing
    orders --> db[("Orders DB")]:::datastore
    orders -. "events" .-> queue[/"Message Queue"/]:::datastore
    queue -.-> billing

    classDef external fill:#64748b,stroke:#475569,color:#fff
    classDef datastore fill:#047857,stroke:#065f46,color:#fff
```

Pattern notes: subgraph with ID + quoted title; `&` fan-out; dotted edges = async;
cylinder = DB; shapes/colors as vocabulary, not decoration.

## Sequence diagram — API interaction

```mermaid
---
title: Login Flow
---
sequenceDiagram
    accTitle: Login flow
    accDescr: A user signs in; the gateway validates credentials with the auth service.

    autonumber
    actor user as User
    participant app as Web App
    participant auth as Auth Service

    user ->> app: Submit credentials
    app ->>+ auth: POST /sessions
    alt credentials valid
        auth -->>- app: 201 + session token
        app -->> user: Redirect to dashboard
    else invalid
        auth -->> app: 401 Unauthorized
        app -->> user: Show error
    end
```

Pattern notes: participants declared up front in narrative order with readable
aliases; `actor` for humans; `autonumber`; activation via `+`/`-`; branches as
`alt`/`else` blocks, not duplicate flows.

## State diagram — lifecycle

```mermaid
---
title: Order Lifecycle
---
stateDiagram-v2
    accTitle: Order lifecycle
    accDescr: Orders move from pending through paid and shipped; cancellation is possible before shipping.

    [*] --> Pending
    Pending --> Paid: payment confirmed
    Pending --> Cancelled: customer cancels
    Paid --> Shipped: dispatched
    Paid --> Cancelled: refund issued
    Shipped --> [*]
    Cancelled --> [*]
```

Pattern notes: `stateDiagram-v2`; `[*]` initial/final; transitions labeled with the
triggering *event*; one noun-state vocabulary.

## ER diagram — data model

```mermaid
---
title: Shop Data Model
---
erDiagram
    accTitle: Shop data model
    accDescr: Customers place orders containing line items that reference products.

    CUSTOMER ||--o{ ORDER : places
    ORDER ||--|{ LINE_ITEM : contains
    PRODUCT ||--o{ LINE_ITEM : "appears in"

    CUSTOMER {
        int id PK
        string email
    }
    ORDER {
        int id PK
        int customer_id FK
        datetime placed_at
    }
```

Pattern notes: every relationship labeled with a verb phrase; attributes limited to
keys plus defining fields; multi-word labels quoted.

## Gantt — phased plan

```mermaid
---
title: Release Plan
---
gantt
    dateFormat YYYY-MM-DD
    axisFormat %b %d

    section Build
        API implementation    :api, 2026-11-02, 10d
        Frontend              :fe, after api, 8d
    section Ship
        QA and hardening      :qa, after fe, 5d
        Launch                :milestone, after qa, 0d
```

Pattern notes: `after <id>` dependencies instead of hand-computed dates; `milestone`
for zero-duration events; sections as phases.

## Adaptation rules for the skill

1. Start from the template matching the selected diagram type; rename IDs/labels to
   the domain. Don't restructure unless the content demands it.
2. Delete what isn't needed (styling classes, `alt` blocks) rather than leaving
   placeholder content.
3. Only add classDefs when color carries meaning; pull fills exclusively from
   [08-safe-palette.md](08-safe-palette.md).
4. Re-validate after adaptation (`mmdc`) — templates guarantee a valid *starting*
   point, not a valid result.
