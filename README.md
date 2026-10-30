# Mermaid Diagram Patterns — Research & Requirements

Research foundation for a Claude skill that produces high-quality Mermaid diagrams in
markdown documents. The goal: diagrams that are correct on first render, legible in both
light and dark mode, well-laid-out, and portable across renderers (GitHub, GitLab,
VS Code, mermaid-cli) — without the user needing to fix them up.

**This repository is documentation only** — the skill itself will be built in a later
session, using these docs as the complete knowledge base and
[docs/07-skill-requirements.md](docs/07-skill-requirements.md) as the spec.

All guidance is sourced from the official Mermaid documentation (mermaid.js.org,
Mermaid v11.x), GitHub's official docs, and verified renderer behavior. Each document
lists its sources.

## Documents

| Document | Covers |
|---|---|
| [docs/01-principles.md](docs/01-principles.md) | Core quality principles: scope, labels, semantics, when a diagram earns its place |
| [docs/02-diagram-selection.md](docs/02-diagram-selection.md) | Choosing the right diagram type for the content |
| [docs/03-layout-patterns.md](docs/03-layout-patterns.md) | Direction, declaration order, subgraphs, edge management, ELK |
| [docs/04-styling-theming.md](docs/04-styling-theming.md) | Frontmatter config, themes, themeVariables, classDef, dark-mode safety |
| [docs/05-syntax-pitfalls.md](docs/05-syntax-pitfalls.md) | Reserved words, escaping, quoting, things that break the parser |
| [docs/06-renderer-compatibility.md](docs/06-renderer-compatibility.md) | What works where: GitHub, GitLab, VS Code, mermaid.live, mermaid-cli |
| [docs/07-skill-requirements.md](docs/07-skill-requirements.md) | Requirements, workflow, and build spec for the Claude skill |
| [docs/08-safe-palette.md](docs/08-safe-palette.md) | WCAG-verified light/dark-safe color palette (with rejected candidates) |
| [docs/09-templates.md](docs/09-templates.md) | Validated starter templates per diagram type |
| [docs/10-worked-examples.md](docs/10-worked-examples.md) | Before/after pairs: typical generated-diagram failures → fixed |
| [docs/11-review-checklist.md](docs/11-review-checklist.md) | Consolidated pre-delivery checklist (the FR7 review pass) |

## The short version

1. **Scope**: one idea per diagram, roughly 5–15 nodes. Split anything bigger.
2. **Type**: pick the diagram type that matches the content (sequence for interactions
   over time, state for lifecycles, ER for data models — not flowcharts for everything).
3. **IDs vs labels**: semantic node IDs (`authSvc`), human-readable quoted labels
   (`authSvc["Auth Service"]`). Always quote labels.
4. **Layout**: choose `LR` or `TB` deliberately; declare nodes in reading order
   (declaration order drives layout); group with subgraphs, max two levels deep.
5. **Styling**: style through frontmatter config + `classDef` semantic classes.
   Never hardcode a fill without also setting `color` and `stroke` — this is the #1
   cause of unreadable diagrams in dark mode.
6. **Portability**: assume the lowest common denominator (GitHub's renderer) unless
   told otherwise — no ELK, no `click`, no beta diagram types.
7. **Validate**: parse/render with `mermaid-cli` (`mmdc`) before delivering. A diagram
   that doesn't compile is worse than no diagram.
