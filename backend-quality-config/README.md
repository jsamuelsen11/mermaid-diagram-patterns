# Backend quality configuration

Drop-in quality control for a Spring Boot, Java 25, Gradle (Kotlin DSL) API backend. One toolchain, pinned by mise,
drives git hooks, CI and local commands so every environment runs the same checks with the same versions.

## Bootstrap a fresh clone

```sh
mise install        # installs Java, Node and every linter at the pinned versions
mise run setup      # npm ci for the Node CLIs, then installs the git hooks
```

From then on hooks run automatically. Useful commands:

| Command                | What it does                                                      |
| ---------------------- | ----------------------------------------------------------------- |
| `mise run lint`        | Every pre-commit check against all tracked files (what CI runs)   |
| `mise run format`      | Apply every formatter to the whole tree                           |
| `mise run check`       | Full Gradle verification: Error Prone, NullAway, Checkstyle, tests |
| `mise run test`        | Tests only                                                        |
| `mise run secrets`     | gitleaks over the full history                                    |
| `mise run hooks:install` | Reinstall hooks after editing lefthook.yml                      |
| `mise tasks`           | List everything above                                             |

## What runs when

### pre-commit (staged files only, groups run in parallel, fixes are re-staged)

| Files                                  | Format                              | Lint / validate                                      |
| -------------------------------------- | ----------------------------------- | ---------------------------------------------------- |
| `*.java`                               | Spotless + palantir-java-format     | javac `-Werror`, Error Prone, NullAway, Checkstyle   |
| `*.gradle.kts`                         | Spotless + ktfmt                    |                                                      |
| `*.md`                                 | rumdl                               | rumdl                                                |
| `*.yml`, `*.yaml`                      | Prettier                            | yamllint                                             |
| `.github/workflows/*`                  |                                     | actionlint + JSON schema                             |
| `.github/dependabot.yml`               |                                     | JSON schema                                          |
| `compose*.yaml`, `docker-compose*.yaml` |                                    | Compose spec schema                                  |
| `openapi*`, `swagger*`, `**/api/**`    |                                     | Redocly (recommended ruleset, tightened)             |
| `*.json`, `*.json5`, `*.jsonc`         | Prettier                            |                                                      |
| `renovate.json`                        |                                     | Renovate schema                                      |
| `*.xml`, `*.jmx`, `*.xsd`, `*.xsl`, `*.wsdl` | Prettier (XML plugin)         | xmllint well-formedness                              |
| `*.properties`                         | Prettier (properties plugin)        |                                                      |
| `*.toml`                               | taplo                               | taplo                                                |
| `Dockerfile*`, `Containerfile`         |                                     | hadolint                                             |
| `*.sh`, `*.bash`                       | shfmt                               | shellcheck                                           |
| everything                             |                                     | editorconfig-checker, typos, conflict markers, files over 1 MiB |

### commit-msg

commitlint with the Conventional Commits preset. Merge, revert and dependency-bot commits are exempt.

### pre-push

`./gradlew test` and a gitleaks scan of every commit not yet on a remote.

## How gitignore is honoured

- Hooks only ever receive staged files, which by definition are not ignored.
- `lefthook run pre-commit --all-files` (used by `mise run lint` and CI) takes its file list from `git ls-files`.
- Tools that can be run standalone are configured to respect `.gitignore` too: Prettier does by default, rumdl via
  `respect-gitignore`, yamllint via `ignore-from-file`, editorconfig-checker by checking only git-tracked files.

## File map

| File                                   | Purpose                                                                 |
| -------------------------------------- | ----------------------------------------------------------------------- |
| `mise.toml`                            | Tool pins, PATH for `node_modules/.bin`, tasks, `postinstall` hook       |
| `lefthook.yml`                         | Hook definitions                                                        |
| `.lefthookrc`                          | Puts mise tools on PATH for IDE and GUI git clients                     |
| `gradlew`, `gradle/wrapper/*`          | Gradle 9.9.1 wrapper                                                    |
| `.editorconfig`                        | Indentation, line endings, charset per file type (plus IntelliJ keys)   |
| `.gitattributes`                       | LF normalisation, binary markers                                        |
| `.gitignore`                           | Gradle, JVM, IDE, OS, secrets                                           |
| `package.json`, `package-lock.json`    | Prettier + plugins, Redocly, commitlint                                 |
| `.prettierrc.yaml`, `.prettierignore`  | Prettier (YAML, JSON, JSON5, XML, JMX, properties)                      |
| `.rumdl.toml`                          | Markdown rules                                                          |
| `.yamllint.yaml`                       | YAML lint rules                                                         |
| `.hadolint.yaml`                       | Dockerfile rules                                                        |
| `.gitleaks.toml`                       | Secret scanning allowlist                                               |
| `_typos.toml`                          | Spell-check exclusions and project vocabulary                           |
| `.taplo.toml`                          | TOML formatting                                                         |
| `.shellcheckrc`, `.shfmt`              | Shell lint and format                                                   |
| `.editorconfig-checker.json`           | Backstop enforcement of `.editorconfig`                                 |
| `commitlint.config.mjs`                | Commit message rules                                                    |
| `redocly.yaml`                         | OpenAPI rules                                                           |
| `build.gradle.kts`                     | Spotless, Checkstyle, Error Prone + NullAway, test config, versions task |
| `settings.gradle.kts`, `gradle.properties`, `gradle/libs.versions.toml` | Catalog and Gradle switches             |
| `config/checkstyle/*.xml`              | Checkstyle rules and suppressions                                       |
| `.github/workflows/quality.yml`        | CI parity: hooks on all files, formatter drift check, gitleaks, `gradle check` |
| `.github/actionlint.yaml`              | actionlint runner labels                                                |
| `renovate.json`                        | Keeps every pin above current, auto-merges minor and patch tooling bumps |

## Design decisions

- **Spotless formats only staged Java files.** The hook passes `-PspotlessTargets=a.java,b.java`; `build.gradle.kts`
  narrows the Spotless target when the property is present. Without it, Spotless covers the whole tree.
- **Checkstyle does no formatting.** palantir-java-format owns layout; Checkstyle covers naming, imports, design and
  bug-prone constructs that a formatter cannot.
- **NullAway runs in JSpecify mode** and is off for test sources. Spring Framework 7 ships JSpecify annotations, so
  framework return types are already nullness-aware. Set `NullAway:AnnotatedPackages` to your real root package.
- **Markdown belongs to rumdl, not Prettier.** Two formatters on one format fight; rumdl formats and lints.
- **JMX files are formatted.** JMeter may rewrite them on save; if that causes churn, move `*.jmx` from the `xml` job
  into `.prettierignore` and keep only the xmllint check.
- **Python CLIs come through uv.** yamllint and check-jsonschema are installed by mise's pipx backend using uv, so no
  system Python is required.
- **editorconfig-checker is installed from GitHub releases.** The mise registry entry for 4.0.2 points at a renamed asset.
- **Actions are pinned by commit SHA.** Renovate's `helpers:pinGitHubActionDigests` keeps the SHAs current.
- **`.lefthookrc` makes hooks work from IDEs.** The hook wrapper sources it (via `rc: '"$PWD/.lefthookrc"'`) before
  calling lefthook, so tools resolve even when git runs with a PATH that was never mise-activated. The `$PWD` form
  is deliberate: a bare filename is looked up on PATH by POSIX sh, and lefthook strips a leading `./`.
- **Checkstyle suppresses `HideUtilityClassConstructor` for `*Application.java`.** Spring Boot entry points only
  hold `main()`, which Checkstyle would otherwise flag as a utility class.

## Adapting

- **Multi-module:** move the quality block from `build.gradle.kts` into a convention plugin in `build-logic/` and
  apply it in every subproject. The hooks already call root-level tasks, so they need no change.
- **Group/package:** change `group` in `build.gradle.kts`; NullAway's annotated package follows it.
- **OpenAPI location:** adjust the `openapi` job glob in `lefthook.yml` if specs live somewhere else.
- **Skip a hook once:** `LEFTHOOK=0 git commit` or `LEFTHOOK_EXCLUDE=typos,editorconfig git commit`.

## Next steps worth considering

- Gradle dependency verification (`./gradlew --write-verification-metadata sha256`) to lock artifact checksums.
- OWASP Dependency-Check or Snyk in CI for CVE scanning of Java dependencies.
- Testcontainers-based integration tests under a separate `integrationTest` source set, run in CI only.
