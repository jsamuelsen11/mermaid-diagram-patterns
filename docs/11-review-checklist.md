# Pre-Delivery Review Checklist

The consolidated checklist behind FR7 in [07-skill-requirements.md](07-skill-requirements.md).
Run it against every diagram after mmdc validation passes. Each item cites its source
doc. This is intended to be embedded (condensed) in the skill's SKILL.md.

## Scope & type

- [ ] One idea, one abstraction level per diagram (01 §2)
- [ ] ≤15 nodes / ≤7 sequence lifelines / ≤8 ER entities — else split (01 §2, 03 §9)
- [ ] Diagram type matches content shape: time-ordered → sequence, lifecycle → state,
      cardinality → ER; flowchart is a deliberate choice, not a default (02)
- [ ] The diagram earns its place — it shows branching/cycles/layers/relationships
      that prose can't (01 §1)

## Syntax & robustness

- [ ] Every label double-quoted: nodes, edges, subgraph titles (05 §3)
- [ ] No bare `end`; no `o`/`x`-leading IDs adjacent to links (05 §1–2)
- [ ] `flowchart` not `graph`; `stateDiagram-v2` not `stateDiagram` (05 §7)
- [ ] Semantic node IDs, no single letters (01 §3)
- [ ] Frontmatter `title` + `accTitle`/`accDescr` present (01 §6)
- [ ] mmdc validation passed — or output explicitly marked unvalidated (07 FR6)

## Layout

- [ ] Direction chosen deliberately: LR for short chains, TB for branchy/deep (03 §1)
- [ ] Happy path declared first as an unbroken chain (03 §2)
- [ ] Subgraphs have IDs + quoted titles; nesting ≤2 levels (03 §6)
- [ ] No gratuitous edge crossings — reduction checklist applied if tangled (03 §7)
- [ ] Edge labels only where they add meaning (01 §4)

## Styling

- [ ] No custom colors unless color carries meaning (04 §2, 01 §7)
- [ ] Every classDef has the full `fill`/`stroke`/`color` triple (04 §2)
- [ ] Fills only from the verified palette (08)
- [ ] ≤4 classDefs, named like a legend (`errorPath`, not `redBox`) (04 §5)
- [ ] No `linkStyle` by index; semantic edge types instead (04 §6)
- [ ] No theme pinned when the target auto-switches light/dark (04 §3)
- [ ] Meaning never carried by color alone (01 §7, 08)

## Portability

- [ ] Target renderer known or GitHub-safe profile applied (06, 07 FR2)
- [ ] GitHub-safe profile: no ELK, no `look`, no `click`, no FontAwesome, no beta
      types, no pinned theme (06)
- [ ] Renderer assumptions stated in one line alongside the delivered diagram (07)

## Maintainability

- [ ] Declarations in reading order; styling grouped at the bottom (01 §8)
- [ ] Comments only where non-obvious; no `{}` inside `%%` comments (01 §8, 05 §5)
- [ ] When editing someone else's diagram: their conventions preserved, fixes
      minimal and explained (07 FR8)
