# Windows and .NET limitations

This document records the blockers encountered while adding the Kotlin/Native NuGet consumers and
the constraints imposed by their current workarounds. It is intended as implementation guidance;
the setup and run commands remain in [`README.md`](README.md).

Verified against `kotlin-native-nuget` 0.3.0, Kotlin 2.4.10, .NET 10.

## What kotlin-native-nuget needs to change

Everything below is a workaround for something the generator cannot express yet. This section is the
consolidated list, so the plugin has one place to work from and this document can shrink as items
land. Ordered by how much of this integration they delete.

### 1. Reflection-free materialisation of generated objects

`NugetMarshal.FromHandle<T>` ends in `Activator.CreateInstance(typeof(T), BindingFlags.NonPublic |
..., new object[] { handle })`, and `KotlinStateFlow<T>.Value` routes through it. Mono cannot do that
in AOT-only mode, so a plain `.Value` read of an object-typed state crashes on Mac Catalyst, not only
Flow collection.

**Costs here:** the entire scalar-accessor surface. 16 exported `capturedXxx()` functions on
`PeopleInSpaceClient`, `PeopleInSpaceSnapshotReader`, and the C# `PersonInfo`/`PeopleSnapshot`/
`IssSnapshot` projections exist only for this. Tracked upstream as an open Future Improvement
(ADR-038), blocked on a reflection-free generic dispatch path.

### 2. AOT-safe callbacks into managed code

Flow collection, the async/suspend path, and the interface bridges all obtain their native callback
pointers from `Marshal.GetFunctionPointerForDelegate` (`CirFlowRenderer.kt:65-67`,
`CirClassRenderer.kt:664/722/750`, `CirBridgeRenderer.kt:129-130`), which needs runtime JIT.
ADR-041 already identifies `[UnmanagedCallersOnly]` + `delegate* unmanaged<>` as the AOT-compatible
alternative and chose against it for the reverse direction.

**Costs here:** the 250 ms polling adapter in `KotlinPeopleInSpaceSource` instead of collecting the
exported `StateFlow`, with the lag, dropped intermediate states, and lost ordering that implies. Also
`RefreshAsync`, which schedules via `requestRefresh()` and cannot await the exported `suspend
refresh()` or cancel it.

Items 1 and 2 together are the whole Mac Catalyst section of this document.

### 3. `[LibraryImport]` rather than `[DllImport]`

Every native entry point is still `[DllImport]`. Not blocking, but it is the trim/AOT-safe form and
is named in ADR-038 alongside the `CSharpProfile` work that would make the output dialect a
generation-time choice.

### 4. Generated bindings as an assembly, not a content file

The bindings ship as `contentFiles/cs/any/Interop.cs`, so exactly one project may compile them and
every other consumer must reference the package for runtime assets only. Getting that wrong produces
duplicate generated types rather than a clear error.

**Costs here:** the `windows/Shared` project boundary is load-bearing and has to be explained to
anyone adding a managed host. A compiled assembly, or a source generator, removes the failure mode.

### 5. A package that carries more than the packing host's RID

`PeopleInSpace.Kotlin` contains only the native library for the host that packed it, so a Windows
package must be built on Windows and a macOS one on macOS. There is no supported way to combine
per-host outputs into one publishable package.

**Costs here:** the package is not publishable as-is, and CI verifies each host separately.

### 6. Local iteration without version collisions

The local package stays at `0.1.0`, so NuGet cannot tell two successive local builds apart and every
Kotlin change needs `packNuget`, delete `windows/obj/packages`, restore `--force --no-cache`. A
generated snapshot version in `nuget { publish { } }` would remove the whole dance.

### 7. Coexistence with other Kotlin compiler plugins

This one is not in the generator, but it decides whether the plugin can be used in a normal KMP app,
so it belongs on the list.

Linking a `sharedLib` fails when another compiler plugin has generated IR into the same compilation:

```text
e: java.lang.NullPointerException
    at ...KlibModuleOriginKt.getKlibModuleOrigin(KlibModuleOrigin.kt:32)
    at ...cexport.CAdapterCodegen.buildCAdapter(CAdapterCodegen.kt:58)
```

Reproduced on 0.3.0 with the Koin compiler plugin 1.0.2 (`:common:linkReleaseSharedMacosArm64`,
`--rerun-tasks`). Ordinary compilation of those targets succeeds; only the C export crashes.
Excluding `io.insert-koin` from the two `kotlinCompilerPluginClasspath*` configurations fixes it,
which is what `common/build.gradle.kts` does. Marking the annotated declarations `internal` does
**not** help, so it is not about which declarations get C-exported.

The root cause is in the Kotlin compiler, not in Koin and not in `kotlin-native-nuget`.
`ModuleDescriptor.klibModuleOrigin` asserts a capability with `!!`, but under K2 the descriptor is a
`FirModuleDescriptor`, whose `getCapability` is hard-coded to `return null`. The assertion cannot be
satisfied, so no plugin can avoid it. Koin's only role is building an `IrFile` whose metadata is not
`KonanMetadata`, which routes its declaration onto the branch that reads the module descriptor.
Declarations from the normal Fir2Ir pipeline carry `KonanMetadata` and never reach it, which is why
this is not hit constantly.

This is [KT-62984](https://youtrack.jetbrains.com/issue/KT-62984), open since October 2023 and still
unresolved (Severity Minor, State Backlog). It was filed against the Compose plugin, where it fired
only for `public`/`protected` `@Composable` functions; Koin reaches the same crash site with no
Compose involved, so it is not annotation-specific. A community PR that filtered `@Composable` out of
C export was closed unmerged and would not have helped here.

A standalone reproducer and a write-up for that issue live in `kt-62984-repro` (sibling checkout),
reduced to three Koin annotations and no custom compiler plugin.

Note this also contradicts the upstream ROADMAP's expect/actual item, which records "Koin is not the
cause ... packs green through the full `packNuget` (Verified by execution)". That conclusion was
reached against the KSP-time duplicate-plan crash that ADR-074 fixed. The failure here is a different
one, at link time, and it is still live on 0.3.0. `expect`/`actual` is not required to trigger it.

## Supported targets and deployment

- WinUI 3 and MAUI on Windows currently support only `win-x64`.
- Mac support is the MAUI Mac Catalyst host on Apple silicon (`maccatalyst-arm64`). Intel Macs,
  other Windows architectures, and a standalone AppKit host are not configured.
- WinUI is unpackaged, framework-dependent, and not self-contained. A target machine must have the
  matching Windows App SDK 1.8 runtime installed.
- MSIX packaging, installers, Store distribution, self-contained deployment, and a graphical ISS
  map are outside the current scope.
- WinUI cannot be built or launched on macOS. The MAUI Catalyst host exists partly to exercise the
  managed adapter and macOS native package locally, but it is not a substitute for a Windows smoke
  test.

## Native packages are host-specific

`PeopleInSpace.Kotlin` contains a native library for the host on which it is packed:

- Windows produces `runtimes/win-x64/native/peopleinspace.dll`.
- Apple silicon macOS produces `runtimes/osx-arm64/native/libpeopleinspace.dylib`.

The package is not a universal cross-platform binary. MinGW link tasks are disabled on non-Windows
hosts, so a Windows package and its DLL must be produced on Windows. The Catalyst host currently
links the `osx-arm64` dylib explicitly through `NativeReference`; there is no separate Kotlin
Mac Catalyst artifact.

Both hosts do share one `PeopleInSpaceClient`. It lives in the `nativeClientMain` source set, which
`mingwX64Main` and `macosArm64Main` both depend on (`macosArm64` keeps its place under `apple` too;
this is a second parent, not a replacement). The only platform seam is
`createPlatformHttpClientEngine`, actualised as WinHttp and Darwin. Anything added to the exported
client therefore reaches both hosts at once and must compile for both.

The NuGet binding scope must also remain narrow. Generating bindings for the broad shared package
surface caused Kotlin/Native C adapter generation failures on macOS. The package is therefore
restricted to `dev.johnoreilly.common.windows`. New exports should be added deliberately and tested
on both native hosts.

## MinGW requires a static SQLite archive

SQLDelight's MinGW `NativeSqliteDriver` requires SQLite at native link time. The Windows build must
stage the MSYS2 `mingw-w64-x86_64-sqlite3` archive at:

```text
common/build/mingw-sqlite/libsqlite3.a
```

Without that archive, linking `peopleinspace.dll` fails. This setup is specific to MinGW x64. The
archive is linked into `peopleinspace.dll`, avoiding a separate SQLite runtime DLL in the app.

`PeopleInSpaceClient(storageDirectory)` also expects a non-blank, existing, writable directory. It
uses that directory as SQLDelight's `basePath`; it does not create an arbitrary caller-provided
directory.

## Koin and Kotlin ViewModel boundary

The annotation/compiler-plugin Koin setup is back, but it cannot run on the two targets that link a
`sharedLib` for the NuGet package. The cause and its evidence are item 7 of
"What kotlin-native-nuget needs to change" above.

`common/build.gradle.kts` therefore excludes `io.insert-koin` from the
`kotlinCompilerPluginClasspath*` configurations of `MingwX64` and `MacosArm64`. Neither target uses
Koin, so nothing is lost there, and every other target keeps annotation-driven DI with its
compile-time graph check.

Two consequences worth knowing:

- Anything the exported native client needs must be constructed directly, never resolved from Koin.
  `PeopleInSpaceClient` already owns its dependencies, which is what makes the exclusion safe.
- The graph is only compile-verified on the targets the plugin runs on. `KoinGraphTest` (jvmTest)
  resolves it at runtime so a broken annotation wiring cannot pass unnoticed.

The compiler plugin is pinned to `1.0.2`. `1.1.0` rejects the `expect`/`actual` `@Module NativeModule`
pattern, reporting `KOIN-D001 Missing dependency` for `HttpClientEngine` and
`PeopleInSpaceDatabaseWrapper`: its graph verifier does not see providers declared on an `expect`
class through to the `actual`. Upgrading needs that resolved or the platform modules restructured.

AndroidX lifecycle ViewModels remain in the `nonWindows` source set and are intentionally excluded
from MinGW. The exported Windows/macOS `PeopleInSpaceClient` does not start Koin or expose AndroidX
types; it owns its HTTP client, SQLDelight driver, repository, coroutine scope, and shutdown
lifecycle directly.

Consequently, the .NET clients do not consume the existing Kotlin UI ViewModels. WinUI and MAUI
instead share the UI-independent C# `PeopleInSpaceViewModel` from `windows/Shared`; their UI projects
contain only platform-specific presentation and lifecycle code.

## Mac Catalyst AOT and generated Flow bindings

Mac Catalyst runs managed code in AOT-only mode. Two generated interop paths from
`kotlin-native-nuget` are not AOT-safe in this environment:

1. Generic Flow collection creates reverse-P/Invoke delegates that require runtime JIT compilation.
   This failed with an "Attempting to JIT compile method ... while running in aot-only mode" error.
2. Reading generated Kotlin object wrappers such as `PeopleState` and `Person` uses reflective
   wrapper construction. Mono crashed while performing the resulting generic type checks.

Direct use of the generated generic Flow collectors or object-wrapper projection therefore remains
unsupported in the Catalyst host.

Re-checked against the 0.3.0 output, where both causes are unchanged. The mechanics and what would
fix them are items 1 and 2 of
"What kotlin-native-nuget needs to change" above.

Upstream tracks this as an open Future Improvement, blocked on a reflection-free generics dispatch
path (ADR-038). The workaround below stays until that ships.

The current workaround captures immutable state inside Kotlin and exposes scalar accessors.
`KotlinPeopleInSpaceSource` polls those accessors every 250 ms and emits a managed snapshot only when
it changes. "Scalar" here includes stdlib types the generator maps to a single wire value, not only
primitives and strings: `IssState.timestamp` is a `kotlin.time.Instant` and crosses as a
`DateTimeOffset` over one `Int64`, so it needs no handle, no reflection, and no managed conversion.
This makes the tested adapter AOT-safe, but has these tradeoffs:

- Managed state can lag Kotlin state by up to approximately 250 ms.
- Intermediate states between polls can be missed.
- The adapter does not preserve Flow backpressure or event ordering semantics.
- Each newly projected field requires another exported scalar accessor.
- The C# `PersonInfo`, `PeopleSnapshot`, and `IssSnapshot` records are necessary managed projections;
  they are not independent domain models or sources of truth.
- Captures are stateful per native client. The current adapter starts one people watcher and one ISS
  watcher. Multiple independent watchers of the same stream must not interleave a capture with its
  accessor reads; a general multi-subscriber API would need synchronization or per-capture handles.

The generated StateFlow API remains exported for other consumers, but the Catalyst managed adapter
must not switch back to it until the generator provides an AOT-safe implementation and a Catalyst
integration test covers it.

## Refresh is scheduled across the native boundary

The managed adapter calls Kotlin's `requestRefresh()` instead of awaiting the exported suspend
function. This avoids another managed-to-native asynchronous callback path.

`RefreshAsync` therefore completes after the refresh has been scheduled, not after the network and
database work has completed. Later people-state snapshots communicate refreshing, success, or
failure. Cancellation after scheduling cannot cancel the Kotlin refresh operation.

Awaiting the exported suspend function needs item 2 of "What kotlin-native-nuget needs to change" above.

## Generated bindings must be compiled once

`windows/Shared` is the sole project that compiles the NuGet package's generated `Interop.cs`.
`WinUiApp` and `MauiApp` reference the same package only for its native runtime assets. Allowing an
application project to compile the package content files would create duplicate generated types and
interop declarations.

Any additional managed host should keep this project boundary: reference `windows/Shared` for the
managed API and include only the package's runtime/native assets itself.

Shipping the bindings as an assembly instead would remove this rule entirely; item 4 of "What kotlin-native-nuget needs to change" above.

## Fixed local NuGet versions can become stale

The local package deliberately remains at version `0.1.0`. NuGet cannot tell two locally rebuilt
packages with that version apart. After changing Kotlin or its exported API, the reliable sequence
is:

1. Run `:common:packNuget`.
2. Delete `windows/obj/packages`.
3. Restore with `--force --no-cache`.
4. Rebuild the managed host.

The repository-local `RestorePackagesPath` prevents this workflow from deleting or mutating the
user's global NuGet cache. Restore must also discover `windows/NuGet.config`, which supplies
`common/build/nuget` as the local feed.

On Mac Catalyst, invoking only MSBuild's `Run` target can launch an existing app bundle without
rebuilding it. Build explicitly and then open the resulting `.app` to avoid testing stale native or
managed code.

Generated NuGet packages, native binaries, restore caches, and staged SQLite archives are build
products. They are neither committed nor published to an external feed.

A generated snapshot version would remove steps 2 and 3; item 6 of "What kotlin-native-nuget needs to change" above.

## MAUI Catalyst shutdown workaround

With MAUI 10.0.20, Catalyst can deliver a trait-collection callback after the window service scope
has been disposed. That produced an `ObjectDisposedException` for `IServiceProvider` during normal
window shutdown.

The app synchronously disconnects its page and shell handlers in `Window.Destroying` before awaiting
view-model disposal. This is a single-window teardown workaround: disconnected pages must not be
reused. It should be re-evaluated after upgrading MAUI, because newer framework code may contain its
own teardown guard.

## Verification gaps

The Windows and macOS workflows pack the native package, inspect the expected native asset, restore
from a clean repository-local cache, build the relevant host, and run unit tests. The C# tests use a
fake source and intentionally avoid live network calls.

CI does not currently launch either UI, verify image/network behavior end-to-end, or reproduce the
Catalyst AOT and shutdown paths. Those remain manual smoke tests. There are no automated WinUI or
MAUI UI tests.

