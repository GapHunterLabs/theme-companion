# Known Issues

Resolved design decisions or resolved bugs backed by measured evidence,
kept here so future work on this plugin doesn't have to re-derive it.

## `bundledColorScheme` path included the `.xml` extension — scheme never actually loaded (found and fixed, 2026-07-28)

`plugin.xml` registered the bundled color scheme as:

```xml
<bundledColorScheme path="/schemes/GapHunterMonokai.xml"/>
```

The `path` attribute of `com.intellij.bundledColorScheme` must **not**
include the `.xml` extension — the platform appends it automatically
when resolving the file (confirmed against JetBrains's own Color Scheme
Management documentation: `path="colors/MyScheme"` resolves to
`colors/MyScheme.xml` on disk). With the extension included, the
platform would have tried to resolve
`.../GapHunterMonokai.xml.xml` — a file that doesn't exist.

**No exception was ever thrown for this** — same silent-failure shape as
other gotchas already documented across this workspace (K2 mode
incompatibility, `RenameProcessor` override-only violations): the
extension point simply doesn't register the scheme, no error surfaces
anywhere in `idea.log`.

**Fix:**

```xml
<bundledColorScheme path="/schemes/GapHunterMonokai"/>
```

**Verified with a real `runIde` pass, not just documentation:** Settings
> Editor > Color Scheme shows "Gap Hunter Monokai" as a selectable,
applied scheme after the fix.

**Lesson:** never assume a `path`/`file` attribute on a platform
extension point mirrors the exact filename on disk — some extension
points (like this one) strip the extension internally and re-append it,
and that convention isn't necessarily uniform across extension points.
Confirm against the platform's own documentation for each one rather
than copying the pattern from an unrelated extension point.
