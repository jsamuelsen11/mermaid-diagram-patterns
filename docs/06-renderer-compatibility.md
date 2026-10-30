# Renderer Compatibility

The same mermaid source renders differently — or not at all — depending on the
platform. A diagram written for mermaid.live can silently degrade on GitHub. The
skill must know (or ask) where the markdown will be viewed and target that renderer.

## Compatibility matrix

| Capability | GitHub | GitLab | VS Code preview¹ | mermaid.live / mermaid-cli |
|---|---|---|---|---|
| Core types (flowchart, sequence, class, state, ER, gantt, pie, journey, gitGraph) | ✅ | ✅ | ✅ | ✅ |
| Frontmatter `title` | ✅ | ✅ | ✅ | ✅ |
| `theme` / `themeVariables` via frontmatter | ⚠️ honored, but fights the platform's auto light/dark switching | ⚠️ same caveat | ✅ | ✅ |
| Automatic light/dark theme switching | ✅ (follows viewer's GitHub theme) | ✅ | ✅ (follows editor theme) | manual |
| `layout: elk` | ❌ falls back to dagre (community-reported) | ❌ not documented | only with ELK plugin/extension support | ✅ (bundled in current versions; CLI needs the elk package on tiny builds) |
| `look: neo` / `handDrawn` | ❌/unknown (version-dependent) | ❌/unknown | version-dependent | ✅ current versions |
| `click` links / callbacks | ❌ stripped (sandboxed iframe) | ⚠️ restricted | ⚠️ | ✅ live editor |
| FontAwesome icons | ❌ renders as text | ❌ | ⚠️ needs CSS | ⚠️ needs CSS |
| Beta types (`architecture-beta`, etc.) | version-dependent, assume ❌ | version-dependent | version-dependent | ✅ |

¹ VS Code needs an extension (e.g., *Markdown Preview Mermaid Support*); built-in
preview does not render mermaid.

## GitHub specifics (the usual lowest common denominator)

- Mermaid renders in README/markdown files, issues, PRs, discussions, and wikis,
  via a sandboxed iframe running Mermaid.js (fenced block with the `mermaid` tag).
- GitHub's docs explicitly warn that **third-party mermaid plugins can cause errors**.
- **Finding the deployed Mermaid version**: a code block containing just `info`
  renders the version — the official way to check what GitHub (or any renderer)
  currently runs:

  ````markdown
  ```mermaid
  info
  ```
  ````

- GitHub switches the diagram theme with the viewer's light/dark preference — this
  is why hardcoding light-page colors breaks (see
  [04-styling-theming.md](04-styling-theming.md), rule 2).
- GitHub lags the latest Mermaid release; never emit syntax newer than the version
  `info` reports without testing.

## Portability strategy

1. **Target the named renderer.** If the user says "for our GitHub README", apply the
   GitHub column strictly: no ELK, no `look`, no `click`, no icons, no pinned theme,
   no beta types.
2. **Unknown target → GitHub-safe profile.** It's the strictest mainstream renderer;
   source that works there works nearly everywhere.
3. **Controlled renderer (docs site, mermaid-cli → SVG) → use the good stuff**: ELK
   for dense graphs, pinned `base` theme + brand `themeVariables`, `look` styles.
4. **Validate against a real parser before delivering** (see below).

## Local validation with mermaid-cli

`@mermaid-js/mermaid-cli` (`mmdc`) renders mermaid to SVG/PNG/PDF and fails loudly on
syntax errors — the only reliable pre-delivery check:

```bash
# one-shot via npx; input can be .mmd or a markdown file (-i README.md transforms
# each mermaid block and reports errors per block)
npx -y @mermaid-js/mermaid-cli -i diagram.mmd -o /tmp/diagram.svg

# validate every diagram in a markdown file without keeping output
npx -y @mermaid-js/mermaid-cli -i doc.md -o /tmp/doc.check.md
```

Exit code ≠ 0 → the diagram will not render. This belongs in the skill's workflow and
is cheap enough to run on every generated diagram. (Requires Node; headless Chromium
is downloaded by puppeteer on first run.)

## Sources

- GitHub official: Creating diagrams (mermaid/geojson/topojson/STL, `info` block,
  plugin warning):
  <https://docs.github.com/en/get-started/writing-on-github/working-with-advanced-formatting/creating-diagrams>
- GitHub ELK non-support (community report):
  <https://github.com/orgs/community/discussions/198799>
- Dark-mode auto-switch breakage: <https://github.com/orgs/community/discussions/35733>
- mermaid-cli: <https://github.com/mermaid-js/mermaid-cli>
- Layout/ELK availability: <https://mermaid.js.org/intro/syntax-reference.html>
