<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Theme Companion Changelog

## [Unreleased]

## [0.1.1]

### Added

- Gap Hunter Labs brand icon (`pluginIcon.svg` / `pluginIcon_dark.svg`).

## [0.1.0]

### Added

- "Gap Hunter Monokai" bundled `EditorColorsScheme`: a dark, high-contrast
  scheme built on original colors, applied via the `DEFAULT_*` highlighter
  attribute keys so Java, Kotlin, Python, and JS all pick it up without
  per-language configuration.
- `FileIconProvider` that resolves source/test/resource root icons directly
  from the module root model and the platform's own `AllIcons` set, so
  marked directories always show their distinctive icon instead of silently
  losing it after an update.
- No telemetry, no license prompts, no forced UI changes.

[Unreleased]: https://github.com/kendjm/theme-companion/compare/0.1.1...HEAD
[0.1.1]: https://github.com/kendjm/theme-companion/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/kendjm/theme-companion/commits/0.1.0
