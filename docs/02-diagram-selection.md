# Choosing the Right Diagram Type

The most impactful decision happens before any syntax is written. Generated diagrams
default to flowcharts for everything; the fix is to match the diagram type to the
*shape of the information*.

## Decision table

| The content is… | Use | Not |
|---|---|---|
| Steps, branches, decisions in a process | `flowchart` | — |
| Parties exchanging messages **over time** (API calls, protocols, auth flows) | `sequenceDiagram` | flowchart with "then X calls Y" edges |
| A thing moving through **states/lifecycle** (order status, connection state, job lifecycle) | `stateDiagram-v2` | flowchart of statuses |
| Data entities and their **relationships/cardinality** | `erDiagram` | flowchart with cylinder nodes |
| Types/classes, inheritance, composition | `classDiagram` | — |
| Work over calendar time, phases, dependencies | `gantt` | — |
| Part-to-whole proportions | `pie` | — |
| Branch/merge history conventions | `gitGraph` | flowchart |
| User experience across steps with sentiment | `journey` | — |
| Chronological milestones (no durations) | `timeline` | gantt |
| Prioritization on two axes | `quadrantChart` | — |
| System context / containers (C4 style) | `C4Context` (verify renderer support) | — |
| Hierarchical brainstorm / topic breakdown | `mindmap` (verify renderer support) | flowchart |

Rules of thumb:

- **If time flows through the picture, it's a sequence diagram.** The single most
  common misuse is a flowchart where boxes are services and edges are "calls" —
  request/response ordering, async boundaries, and lifelines are exactly what
  `sequenceDiagram` encodes natively.
- **If the nodes are the same object at different moments, it's a state diagram.**
  A flowchart asks "what happens next?"; a state diagram asks "what is it now, and
  what transitions are legal?"
- **If the edges have cardinality ("one order has many items"), it's an ER diagram.**
- **Flowchart is the fallback**, and still the right choice for genuine processes,
  architectures (boxes-and-arrows with grouping via subgraphs), and dependency graphs.

## Type-specific quality notes

### Flowchart
- Use `flowchart`, not the legacy `graph` keyword — same engine, but `flowchart`
  documents intent and supports all current features.
- See [03-layout-patterns.md](03-layout-patterns.md) — layout quality is almost
  entirely a flowchart problem.

### Sequence diagram
- Declare all `participant`s explicitly at the top, in the order they should appear
  left-to-right (first-use order otherwise controls it implicitly). Use
  `participant api as API Gateway` for readable names, `actor` for humans.
- Use `autonumber` when prose refers to specific steps.
- Use activations (`->>+` / `-->>-`) to show the span of synchronous work; use `-)`
  for async messages.
- Use `alt`/`opt`/`par`/`loop` blocks instead of drawing branches as separate flows.
- `Note over A,B: …` for annotations rather than fake messages.
- Group related participants with `box Name … end` blocks when there are ≥4
  participants on both sides of a boundary.
- Correct arrow vocabulary: `->>` sync request, `-->>` response/return (dashed),
  `-)` async fire-and-forget, `--x` failed/lost message.

### State diagram
- Always `stateDiagram-v2` (v1 is legacy).
- Use `[*]` for initial/final markers; label transitions with the *event* that causes
  them (`Pending --> Paid: payment confirmed`).
- Use composite states for sub-lifecycles instead of a second diagram when small.

### ER diagram
- Relationship lines encode cardinality (`||--o{` = one-to-many); label every
  relationship with a verb phrase: `CUSTOMER ||--o{ ORDER : places`.
- Attribute lists are optional — include keys and a few defining attributes, not
  every column.

### Beta / newer types
Anything suffixed `-beta` (`architecture-beta`, `usecase-beta`, etc.) and recently
added types (`timeline`, `quadrantChart`, `mindmap` on older renderers) must be
checked against the target renderer before use — see
[06-renderer-compatibility.md](06-renderer-compatibility.md).

## Sources

- Diagram syntax index: <https://mermaid.js.org/intro/syntax-reference.html>
- Sequence diagrams: <https://mermaid.js.org/syntax/sequenceDiagram.html>
- State diagrams: <https://mermaid.js.org/syntax/stateDiagram.html>
- ER diagrams: <https://mermaid.js.org/syntax/entityRelationshipDiagram.html>
