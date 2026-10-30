# Requirements for the Mermaid Diagram Skill

Distills the research (docs 01–06, plus the palette/templates/examples in 08–10) into
requirements and a proposed workflow for a Claude skill that creates/edits Mermaid
diagrams in markdown.

**This repository is documentation only.** The skill is NOT built here. A future
build session should need nothing beyond this repo's docs plus generic
skill-authoring knowledge (e.g., the `skill-creator` skill): docs 01–06 are the
knowledge base, 08–10 are ready-made reference content, and this document is the spec.

## Problem statement

Generated mermaid diagrams typically fail in five ways:

1. **Syntax errors** — diagram doesn't render at all (unquoted labels, `end`,
   version-specific syntax).
2. **Dark-mode breakage** — hardcoded fills with floating text color.
3. **Poor layout** — wrong direction, tangled edges, oversized scope.
4. **Wrong diagram type** — flowcharts used for interactions, lifecycles, data models.
5. **Unmaintainable source** — single-letter IDs, per-node styles, no structure.

The skill must make each failure structurally impossible or caught before delivery.

## Functional requirements

### FR1 — Diagram type selection
Before writing syntax, classify the content against the decision table in
[02-diagram-selection.md](02-diagram-selection.md). Time-ordered interactions →
sequence; lifecycle → state; data relationships → ER; flowchart as deliberate
fallback, never default.

### FR2 — Renderer targeting
Determine the target renderer (ask only if not inferable from context — a README or
repo doc implies GitHub). Apply the corresponding capability profile from
[06-renderer-compatibility.md](06-renderer-compatibility.md). Unknown → GitHub-safe
profile: no ELK, no `look`, no `click`, no icons, no pinned theme, no beta types.

### FR3 — Defensive syntax (always on)
- Every label double-quoted (nodes, edges, subgraph titles).
- Semantic node IDs; never single letters.
- No bare `end`; no node IDs starting `o`/`x` adjacent to links.
- `flowchart` not `graph`; `stateDiagram-v2` not `stateDiagram`.
- Frontmatter `title`; `accTitle`/`accDescr` on non-trivial diagrams.
- Full pitfall list: [05-syntax-pitfalls.md](05-syntax-pitfalls.md).

### FR4 — Layout quality
- Deliberate direction choice (LR for ≤8-node chains, TB for branchy/deep).
- Happy path declared first as an unbroken chain; edges grouped by source.
- Subgraphs for grouping (IDs + quoted titles, ≤2 levels).
- Crossing-reduction checklist from [03-layout-patterns.md](03-layout-patterns.md)
  applied before delivery.
- Scope cap: >15 nodes → split into multiple diagrams, each with one idea.

### FR5 — Theme-safe styling
- Default: no custom colors.
- When styling: semantic `classDef`s (≤4), full `fill`/`stroke`/`color` triples,
  fills exclusively from the WCAG-verified palette in
  [08-safe-palette.md](08-safe-palette.md); styling grouped at the bottom of the
  source.
- Semantic edge styles (`-.->`, `==>`) over `linkStyle` indices.
- Rules in [04-styling-theming.md](04-styling-theming.md).

### FR6 — Validation before delivery
Every generated/edited diagram must be parsed with mermaid-cli before being presented:

```bash
npx -y @mermaid-js/mermaid-cli -i <file> -o <scratch-output>
```

Non-zero exit → fix and re-validate; never deliver an unvalidated diagram. If `mmdc`
is unavailable (no network/node), state explicitly that the diagram is unvalidated.

### FR7 — Self-review pass
After validation, review the source against the consolidated checklist in
[11-review-checklist.md](11-review-checklist.md) (scope/type, syntax, layout,
styling, portability, maintainability — each item cites its source doc).

### FR8 — Editing existing diagrams
When modifying a user's diagram: preserve their ID conventions and styling vocabulary;
apply defensive fixes (quoting) only to lines touched, unless the diagram is broken —
then fix minimally and say what was broken.

## Non-functional requirements

- **NFR1**: The skill degrades gracefully — if the renderer is unknown and the user
  can't be asked, it produces GitHub-safe output and notes the assumption.
- **NFR2**: Generated source is maintainable by humans: ordered, indented, commented
  only where non-obvious.
- **NFR3**: The skill never invents syntax. Anything not covered by docs 01–06 gets
  verified against official docs (Context7 `/mermaid-js/mermaid` or mermaid.js.org)
  before use.

## Proposed skill workflow

```
1. CLASSIFY   content → diagram type (FR1); target renderer → capability profile (FR2)
2. SCOPE      list entities/relations; >15 nodes → split plan
3. DRAFT      start from the matching template (09-templates.md); defensive syntax
              (FR3), layout rules (FR4), happy path first
4. STYLE      only if it adds meaning; verified-palette classDefs (FR5, 08)
5. VALIDATE   mmdc parse/render (FR6); on error → fix → re-validate
6. REVIEW     checklist pass (FR7); layout bad → crossing-reduction loop (FR4)
7. DELIVER    diagram + one-line note of renderer assumptions made
```

## Proposed skill structure (for the build session)

```
mermaid-diagrams/
  SKILL.md              # workflow + the always-on rules (FR1–FR8 condensed)
  references/
    diagram-types.md    # from 02 — selection table + per-type notes
    layout.md           # from 03
    styling.md          # from 04 + verified palette from 08
    pitfalls.md         # from 05
    renderers.md        # from 06 — capability profiles
    templates.md        # from 09 — validated starter skeletons
    examples.md         # from 10 — before/after pairs (few-shot material)
  scripts/
    validate.sh         # wraps mmdc; input file → pass/fail + error excerpt
```

Design notes for SKILL.md:

- Keep the always-on rules (quoting, dark-mode triple, validation) in the main file
  since they apply to every invocation; push per-type syntax detail to references
  loaded on demand (progressive disclosure — SKILL.md should stay small).
- The description/trigger should fire on: creating or editing mermaid diagrams,
  "add a diagram to this doc/README", architecture/flow/sequence diagram requests in
  markdown contexts, and fixing broken/ugly mermaid.
- The validation script is the only executable dependency (Node + npx) and must fail
  soft: if `mmdc` can't run, deliver with an explicit "unvalidated" warning (NFR1).
- The worked examples (10) compress the rules better than restating them — prefer
  embedding 1–2 before/after pairs in SKILL.md over more rule text.

## Resolved during research

- **Safe palette**: verified by WCAG contrast computation — six fills, see
  [08-safe-palette.md](08-safe-palette.md). Original candidates `#059669` and
  `#d97706` failed white-text contrast and were replaced with `#047857`/`#b45309`.
- **Validation tooling**: `npx -y @mermaid-js/mermaid-cli -i <md-or-mmd> -o <out>`
  confirmed working on this machine (Node v25 via nvm/mise); it validates every
  mermaid block in a markdown file and exits non-zero per broken block.
- **Starter templates**: written and mmdc-validated in
  [09-templates.md](09-templates.md).

## Open questions (resolve during skill build)

1. Should the skill render a PNG/SVG preview for the user when working locally
   (mmdc output), or is parse-validation enough?
2. Verify current GitHub Mermaid version with an `info` block in a scratch repo
   (requires pushing to GitHub — can't be done from docs alone) to set the syntax
   baseline (edge IDs? `@{shape}` metadata? timeline/quadrant?).
3. GitLab/Confluence/Obsidian capability columns are thin — research if those targets
   matter to actual usage.
