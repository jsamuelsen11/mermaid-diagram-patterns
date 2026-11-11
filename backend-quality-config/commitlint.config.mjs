// commitlint: Conventional Commits. https://www.conventionalcommits.org
export default {
  extends: ["@commitlint/config-conventional"],
  rules: {
    "header-max-length": [2, "always", 100],
    "body-max-line-length": [2, "always", 120],
    "subject-case": [2, "never", ["sentence-case", "start-case", "pascal-case", "upper-case"]],
    "type-enum": [
      2,
      "always",
      ["build", "chore", "ci", "docs", "feat", "fix", "perf", "refactor", "revert", "style", "test"],
    ],
  },
  // Dependency-update bots and merge commits are exempt.
  ignores: [(message) => /^(Merge |Revert |chore\(deps\)|fix\(deps\))/.test(message)],
};
