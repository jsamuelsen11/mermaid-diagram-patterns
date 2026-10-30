# Worked Examples: Typical Generated Diagram → Fixed

Before/after pairs showing the exact failure modes the skill exists to prevent, each
mapped to the rules that fix it. These make good few-shot material for the skill's
SKILL.md or references.

## Example 1 — Meaningless IDs, decorative pastels, broken dark mode

**Before** (parses, but unmaintainable and unreadable in dark mode — light pastel
fills with no text color, legacy `graph`, single-letter IDs, unquoted labels):

```mermaid
graph TD
    A[User Request] --> B{Auth?}
    B --> C[Process]
    B --> D[Reject]
    C --> E[(DB)]
    style A fill:#e6f3ff
    style C fill:#d4edda
    style D fill:#f8d7da
```

**After** (semantic IDs, quoted labels, decision outcomes labeled, color only where
it means something, full fill/stroke/color triples from the verified palette):

```mermaid
---
title: Request Authorization
---
flowchart TB
    accTitle: Request authorization flow
    accDescr: Authenticated requests are processed and stored; others are rejected.

    request["User Request"] --> authed{"Authenticated?"}
    authed -- "Yes" --> process["Process Request"] --> db[("App DB")]
    authed -- "No" --> reject["401 Reject"]:::errorPath

    classDef errorPath fill:#dc2626,stroke:#991b1b,color:#fff
```

Rules applied: 01 §3 (IDs/labels), 01 §7 + 04 §2 (color), 05 §3 (quoting),
05 §7 (`flowchart` over `graph`).

## Example 2 — Flowchart misused for a time-ordered interaction

**Before** (services as boxes, calls as edges — ordering, sync/async, and the
response path are invisible; numbered edge labels fake what sequence diagrams do
natively):

```mermaid
flowchart LR
    client["Client"] -- "1. login" --> api["API"]
    api -- "2. verify" --> auth["Auth"]
    auth -- "3. ok" --> api
    api -- "4. token" --> client
```

**After** (same content as a sequence diagram — lifelines, activation span, the
failure branch that the flowchart version silently omitted):

```mermaid
---
title: Login Interaction
---
sequenceDiagram
    accTitle: Login interaction
    accDescr: The client logs in via the API, which verifies credentials with Auth.

    autonumber
    participant client as Client
    participant api as API
    participant auth as Auth

    client ->> api: POST /login
    api ->>+ auth: Verify credentials
    alt valid
        auth -->>- api: OK
        api -->> client: 200 + token
    else invalid
        auth -->> api: Rejected
        api -->> client: 401
    end
```

Rule applied: 02 ("if time flows through the picture, it's a sequence diagram").

## Example 3 — The everything-diagram

**Before** (one diagram trying to show deployment, request path, and data pipeline
at once; 20+ nodes, guaranteed edge tangle — abbreviated here):

```text
flowchart TD
    dev --> git --> ci --> registry --> k8s
    user --> cdn --> lb --> web --> api --> cache & db
    api --> queue --> workers --> warehouse
    ci --> tests
    workers --> alerts
    k8s --> web & api & workers
    ...
```

**After**: not one better diagram — **three diagrams**, each one idea at one
abstraction level, linked by prose:

```mermaid
---
title: 1 — Deployment Pipeline
---
flowchart LR
    accTitle: Deployment pipeline
    accDescr: Code flows from commit through CI to the cluster.
    commit["Git Commit"] --> ci["CI Build + Tests"] --> registry["Image Registry"] --> cluster["Kubernetes"]
```

```mermaid
---
title: 2 — Request Path
---
flowchart LR
    accTitle: Request path
    accDescr: User requests pass through CDN and load balancer to web and API tiers.
    user["User"]:::external --> cdn["CDN"] --> lb["Load Balancer"] --> web["Web Tier"] --> api["API"]
    api --> cache[("Cache")]:::datastore & db[("Primary DB")]:::datastore

    classDef external fill:#64748b,stroke:#475569,color:#fff
    classDef datastore fill:#047857,stroke:#065f46,color:#fff
```

```mermaid
---
title: 3 — Async Data Pipeline
---
flowchart LR
    accTitle: Async data pipeline
    accDescr: API events flow through a queue to workers and the warehouse.
    api["API"] -. "events" .-> queue[/"Queue"/] -.-> workers["Workers"] --> warehouse[("Warehouse")]:::datastore

    classDef datastore fill:#047857,stroke:#065f46,color:#fff
```

Rules applied: 01 §2 (one idea, 5–15 nodes, one abstraction level), 03 §7 step 7
(still tangled → split).

## Example 4 — Parser landmines

**Before** (three independent breakages: bare `end` node, `--x` misparse risk from a
node ID starting with `x`, unquoted parentheses):

```text
flowchart LR
    start --> check[Check (fast path)]
    check --> xfer
    check --> end
```

**After**:

```mermaid
flowchart LR
    start(["Start"]) --> check["Check (fast path)"]
    check --> transfer["Transfer"]
    check --> finish(["End"])
```

Rules applied: 05 §1 (`end`), 05 §2 (`o`/`x` IDs — renamed, which also improves the
label), 05 §3 (quote everything).

## The meta-pattern

Every "after" is *less* syntax than a naive fix would add: fewer nodes, fewer colors,
fewer edge labels. Improving a generated diagram is mostly deletion plus relocation
of detail into prose — the skill should treat "add more styling" as a smell and
"split or simplify" as the default remedy.
