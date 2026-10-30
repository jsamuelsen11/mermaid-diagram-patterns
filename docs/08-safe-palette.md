# Verified Dark/Light-Safe Color Palette

Resolves the palette question from [07-skill-requirements.md](07-skill-requirements.md)
with computed WCAG 2.x contrast ratios instead of eyeballing.

## Acceptance criteria

A fill color is safe for use in a `classDef` when **all three** hold:

1. **White text on the fill ≥ 4.5:1** (WCAG AA for normal text) — node labels stay
   readable.
2. **Fill vs. light page background (`#ffffff`) ≥ 3:1** (WCAG 1.4.11 non-text
   contrast) — the node shape is visible on GitHub light mode.
3. **Fill vs. dark page background (`#0d1117`, GitHub dark canvas) ≥ 3:1** — the node
   shape is visible on GitHub dark mode.

This window is narrow: a color dark enough for white text but light enough to stand
off `#0d1117` sits in a mid-tone band. That's why both light pastels *and* very dark
saturated colors fail.

## The verified palette

All six pass all three criteria. Use white text (`color:#fff`) with every one.

| Role (suggested) | Fill | White text | vs light bg | vs dark bg |
|---|---|---:|---:|---:|
| Primary / process | `#2563eb` (blue) | 5.17 | 5.17 | 3.66 |
| Secondary / alt path | `#7c3aed` (violet) | 5.70 | 5.70 | 3.32 |
| Success / happy path | `#047857` (green) | 5.48 | 5.48 | 3.45 |
| Warning / pending | `#b45309` (amber) | 5.02 | 5.02 | 3.77 |
| Error / failure path | `#dc2626` (red) | 4.83 | 4.83 | 3.92 |
| Neutral / external system | `#64748b` (slate) | 4.76 | 4.76 | 3.98 |

Strokes: use a darker shade of the same hue (e.g., `fill:#2563eb,stroke:#1e40af`);
stroke contrast is non-critical because the fill itself already clears 3:1 against
both page backgrounds.

```
classDef primary   fill:#2563eb,stroke:#1e40af,color:#fff
classDef secondary fill:#7c3aed,stroke:#5b21b6,color:#fff
classDef success   fill:#047857,stroke:#065f46,color:#fff
classDef warning   fill:#b45309,stroke:#92400e,color:#fff
classDef errorPath fill:#dc2626,stroke:#991b1b,color:#fff
classDef neutral   fill:#64748b,stroke:#475569,color:#fff
```

## Rejected candidates (and why — keep for the skill's "don't do this" list)

| Fill | Fails | Ratio |
|---|---|---:|
| `#059669` green | white text < 4.5 | 3.77 |
| `#d97706` amber | white text < 4.5 | 3.19 |
| `#1d4ed8` blue | invisible edge-case on dark bg (< 3) | 2.82 |
| `#6d28d9` violet | vs dark bg < 3 | 2.66 |
| `#92400e` amber | vs dark bg < 3 | 2.67 |
| `#b91c1c` red | vs dark bg < 3 | 2.92 |
| `#475569` slate | vs dark bg < 3 | 2.50 |

The pattern: Tailwind's `-600` row is roughly the sweet spot (with green/amber needing
`-700`); `-500` is too light for white text, `-700`/`-800` too dark for dark mode.

## Scope and caveats

- Ratios computed with the WCAG 2.x relative-luminance formula
  (script: a small Node implementation; reproduce with any WCAG contrast checker).
- Background assumptions: GitHub light `#ffffff`, GitHub dark `#0d1117`. Other
  platforms' dark canvases (VS Code `#1e1e1e`, GitLab `#18171d`) are lighter or
  comparable, so the ≥3:1 margin holds or improves.
- This palette is for **node fills via classDef**. It does not make `lineColor`,
  `edgeLabelBackground`, or `clusterBkg` overrides safe — those sit directly on the
  page background and should stay theme-controlled (see
  [04-styling-theming.md](04-styling-theming.md), rule 4).
- Color-blind note: blue/violet and green/amber pairs can be hard to distinguish for
  some viewers — which is why the palette assigns *roles* and the principles doc
  requires pairing color with shape/edge-style/label, never color alone.
