# Theme Companion

IntelliJ-family plugin. A dark, high-contrast editor color scheme plus a
folder-icon provider that keeps source/test/resource root icons showing up,
always.

## Why it exists

Born from real evidence in JetBrains Marketplace reviews of a paid Monokai
theme plugin (2.47M downloads, 85%+ of reviews at 3 stars or below), not
assumptions:

- Users who already paid still got purchase/license popups pushing them to
  the lifetime tier — "Totally unacceptable," in their own words.
- Directories marked as source or test roots stopped showing their icon
  after an update — "Folders no longer have icons."
- A UI redesign ("Island Theme") was forced on everyone with no setting to
  turn it off, drawing repeated complaints asking for an opt-out.
- The Light variant of the theme was only available on some JetBrains IDEs
  and not others — inconsistent across the product line.
- The free alternatives that exist have been unmaintained since 2019.

## Why built this way

- **A bundled `EditorColorsScheme`, not a full Theme/Laf.** The complaints
  are about editor syntax coloring and file icons, not the IDE's window
  chrome. A `bundledColorScheme` entry in `plugin.xml` pointing at a plain
  scheme XML file is the smallest, most maintainable way to ship a color
  scheme — no custom scheme-loading code needed.
- **Colors set on the `DEFAULT_*` attribute keys** (`DefaultLanguageHighlighterColors`),
  not per-language keys. Java, Kotlin, Python, and JS highlighters all fall
  back to these defaults for keywords/strings/numbers/comments/etc., so one
  set of overrides covers all of them without per-language duplication.
- **An original palette, not a copy of the competitor's.** The classic
  "monokai" aesthetic (dark warm-gray background, vivid pink/green/blue/
  yellow/violet accents) is a decades-old, widely-reused style, not a
  trademarked exact palette — but the specific hex values here are our own,
  chosen to be clearly distinct from both the original Monokai and Monokai
  Pro's palettes.
- **`FileIconProvider` resolves icons only from `AllIcons`**, a hard
  platform dependency that is always present, instead of from this plugin's
  own bundled icon resources. That is the direct fix for "folder icons
  disappeared after an update": there is no custom icon asset here that
  could fail to load. The provider matches a directory against the real
  module root model (`ModuleRootManager` → `ContentEntry` → `SourceFolder`)
  and returns `AllIcons.Modules.SourceRoot` / `TestRoot` / `ResourcesRoot` /
  `TestResourcesRoot` accordingly; if it isn't a marked root, or anything
  goes wrong resolving it, it returns `null` and lets the platform's own
  default apply — it never partially breaks the icon.
- **No network access, no telemetry, no popups, no forced onboarding.**
  Turn the theme on the same way as any built-in one: Settings > Appearance
  & Behavior > Appearance.

## Usage

Settings > Appearance & Behavior > Appearance > Theme, or Editor > Color
Scheme, and pick "Gap Hunter Monokai." Source/test/resource root icons work
automatically wherever a module marks a directory as one — no configuration
needed.

## Enterprise / Team Licensing

Need enterprise features, custom color schemes, or team licensing?
Contact us at **gaphunterlabs@gmail.com**.

## Development

```
./gradlew test           # unit tests
./gradlew buildPlugin    # generates build/distributions/*.zip
./gradlew verifyPlugin   # checks compatibility against real IDEs
```

## License

Apache-2.0. See `LICENSE`.
