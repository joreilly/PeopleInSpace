# Windows and .NET limitations

This document records the blockers encountered while adding the Kotlin/Native NuGet consumer and
the constraints imposed by their current workarounds. It is intended as implementation guidance;
the setup and run commands remain in [`README.md`](README.md).

Verified against `kotlin-native-nuget` 0.3.0, Kotlin 2.4.10, .NET 10. Re-checked on 2026-08-30:
0.3.0 is still the latest plugin release, and the version catalog's `kotlin = "2.4.0"` resolves to
Kotlin Gradle plugin and Kotlin/Native 2.4.10 because the plugin depends on it.

## What kotlin-native-nuget needs to change

Everything below is a workaround for something the generator cannot express yet. This section is the
consolidated list, so the plugin has one place to work from and this document can shrink as items
land. Ordered by how much of this integration they delete.

### 1. `[LibraryImport]` rather than `[DllImport]`

Every native entry point is still `[DllImport]`. Not blocking on WinUI, but it is the trim/AOT-safe
form and is named in ADR-038 alongside the `CSharpProfile` work that would make the output dialect a
generation-time choice.

### 2. Generated bindings as an assembly, not a content file

The bindings ship as `contentFiles/cs/any/Interop.cs`, so exactly one project may compile them and
every other consumer must reference the package for runtime assets only. Getting that wrong produces
duplicate generated types rather than a clear error.

**Costs here:** the `windows/Shared` project boundary is load-bearing and has to be explained to
anyone adding a managed host. A compiled assembly, or a source generator, removes the failure mode.

### 3. Local iteration without version collisions

The local package stays at `0.1.0`, so NuGet cannot tell two successive local builds apart and every
Kotlin change needs `packNuget`, delete `windows/obj/packages`, restore `--force --no-cache`. A
generated snapshot version in `nuget { publish { } }` would remove the whole dance.

### 4. Coexistence with other Kotlin compiler plugins

This one is not in the generator, but it decides whether the plugin can be used in a normal KMP app,
so it belongs on the list.

Linking a `sharedLib` fails when another compiler plugin has generated IR into the same compilation:

```text
e: java.lang.NullPointerException
    at ...KlibModuleOriginKt.getKlibModuleOrigin(KlibModuleOrigin.kt:32)
    at ...cexport.CAdapterCodegen.buildCAdapter(CAdapterCodegen.kt:58)
```

Reproduced on 0.3.0 with the Koin compiler plugin 1.0.2 (`--rerun-tasks` on the shared-lib link
task). Ordinary compilation of the target succeeds; only the C export crashes. Excluding
`io.insert-koin` from the `kotlinCompilerPluginClasspath*` configurations fixes it, which is what
`common/build.gradle.kts` does. Marking the annotated declarations `internal` does **not** help, so
it is not about which declarations get C-exported.

The root cause is in the Kotlin compiler, not in Koin and not in `kotlin-native-nuget`.
`ModuleDescriptor.klibModuleOrigin` asserts a capability with `!!`, but under K2 the descriptor is a
`FirModuleDescriptor`, whose `getCapability` is hard-coded to `return null`. The assertion cannot be
satisfied, so no plugin can avoid it. Koin's only role is building an `IrFile` whose metadata is not
`KonanMetadata`, which routes its declaration onto the branch that reads the module descriptor.
Declarations from the normal Fir2Ir pipeline carry `KonanMetadata` and never reach it, which is why
this is not hit constantly.

This is [KT-62984](https://youtrack.jetbrains.com/issue/KT-62984), open since October 2023. It was
filed against the Compose plugin, where it fired only for `public`/`protected` `@Composable`
functions; Koin reaches the same crash site with no Compose involved, so it is not annotation-specific.
A community PR that filtered `@Composable` out of C export was closed unmerged and would not have
helped here.

**Fixed upstream, not yet released.** [JetBrains/kotlin#7431](https://github.com/JetBrains/kotlin/pull/7431)
("Do not assert klib origin capability on K2 module descriptors") was merged into `master` on
2026-08-25. It splits the accessor into a nullable `klibModuleOriginOrNull`, keeps the strict one
failing with a named error, and moves the callers with a null contract onto the nullable form. As of
2026-08-30 the fix is not in any released Kotlin: 2.4.10 still crashes, the 2.4.20-RC2 changelog does
not list it, and the YouTrack issue still shows State Backlog. It ships with the next release cut from
`master` unless it is backported to 2.4.x. The exclusion stays until this project builds with a
Kotlin that contains it; at that point, drop the `kotlinCompilerPluginClasspath*` exclusion in
`common/build.gradle.kts` and re-run `packNuget` to confirm.

A standalone reproducer and a write-up for that issue live in `kt-62984-repro` (sibling checkout),
reduced to three Koin annotations and no custom compiler plugin.

### Previously on this list: AOT-safe generics and callbacks

The generated `KotlinStateFlow<T>.Value` and Flow collection go through `Activator.CreateInstance`
and `Marshal.GetFunctionPointerForDelegate`, neither of which works under Mono AOT-only mode. This
project no longer targets an AOT-only host, so `KotlinPeopleInSpaceSource` collects the exported
flows directly and the scalar-accessor workaround has been removed. Anyone adding an AOT-only host
(Mac Catalyst, NativeAOT) will hit both again; upstream tracks them as ADR-038 and ADR-041.

## Supported targets and deployment

- WinUI 3 supports only `win-x64`. Other Windows architectures are not configured.
- WinUI is unpackaged, framework-dependent, and not self-contained. A target machine must have the
  matching Windows App SDK 1.8 runtime installed.
- Because the WinUI process has no package identity, packaged-only Windows Runtime APIs such as
  `Windows.Storage.ApplicationData` throw `0x80073D54` and take the app down at startup as a stowed
  exception (`0xC000027B`). The WinUI project must use unpackaged-safe alternatives (`System.IO`
  under `%LOCALAPPDATA%`), and `windows/Shared` must not assume package identity either.
- MSIX packaging, installers, Store distribution, self-contained deployment, and a graphical ISS
  map are outside the current scope.
- The package and the WinUI app can only be built on Windows.

## The NuGet surface is deliberately narrow

`PeopleInSpaceClient` lives in `mingwX64Main`. It owns its HTTP client (WinHttp), SQLDelight driver,
repository and coroutine scope, and exposes two `StateFlow`s plus `refresh()` and `close()`.

The models it emits (`Person`, `PeopleState`, `IssState`) and the `PeopleInSpaceClientController`
that produces them sit in `commonMain` under `dev.johnoreilly.common.windows`, which is also the
package the NuGet plugin exports. They are in `commonMain` so their state transitions can be tested
on the JVM against a fake repository, and they exist because the repository's own surface
(`StateFlow<Throwable?>`, the `Assignment` wire DTO, Koin construction) is not something the
generator can hand to C# usefully.

Generating bindings for the broad shared package surface caused Kotlin/Native C adapter generation
failures. New exports should be added deliberately.

## MinGW requires a static SQLite archive

SQLDelight's MinGW `NativeSqliteDriver` requires SQLite at native link time. The Windows build must
stage the MSYS2 `mingw-w64-x86_64-sqlite3` archive at:

```text
common/build/mingw-sqlite/libsqlite3.a
```

Without that archive, linking `peopleinspace.dll` fails. This setup is specific to MinGW x64. The
archive is linked into `peopleinspace.dll`, avoiding a separate SQLite runtime DLL in the app.

Current MSYS2 builds SQLite with stack protection and fortified string functions, so the archive
references `__stack_chk_fail`, `__stack_chk_guard`, and `__mem*_chk`. Modern mingw-w64 provides those
in its CRT, but the Kotlin/Native MinGW toolchain still ships gcc 9.2 and does not link `libssp` on
its own, and the MSYS2 `mingw-w64-x86_64-crt` `libssp.a` is an empty stub. The `link*MingwX64` tasks
therefore copy `libssp.a` out of the toolchain under `~/.konan/dependencies` and link it with
`-lssp`. This is coupled to the toolchain layout; a Kotlin/Native release that updates its bundled
MinGW should be re-checked here.

`PeopleInSpaceClient(storageDirectory)` also expects a non-blank, existing, writable directory. It
uses that directory as SQLDelight's `basePath`; it does not create an arbitrary caller-provided
directory.

## Koin and Kotlin ViewModel boundary

The annotation/compiler-plugin Koin setup is back, but it cannot run on the target that links a
`sharedLib` for the NuGet package. The cause and its evidence are item 4 of
"What kotlin-native-nuget needs to change" above; the compiler fix is merged upstream but not yet in
a released Kotlin, so this section stays in force for now.

`common/build.gradle.kts` therefore excludes `io.insert-koin` from the
`kotlinCompilerPluginClasspath*` configurations of `MingwX64`. That target does not use Koin, so
nothing is lost there, and every other target keeps annotation-driven DI with its compile-time graph
check.

Two consequences worth knowing:

- Anything the exported native client needs must be constructed directly, never resolved from Koin.
  `PeopleInSpaceClient` already owns its dependencies, which is what makes the exclusion safe.
- The graph is only compile-verified on the targets the plugin runs on. `KoinGraphTest` (jvmTest)
  resolves it at runtime so a broken annotation wiring cannot pass unnoticed.

The compiler plugin is pinned to `1.0.2`. `1.1.0` rejects the `expect`/`actual` `@Module NativeModule`
pattern, reporting `KOIN-D001 Missing dependency` for `HttpClientEngine` and
`PeopleInSpaceDatabaseWrapper`: its graph verifier does not see providers declared on an `expect`
class through to the `actual`. Upgrading needs that resolved or the platform modules restructured.
`1.1.0` is still the latest release as of 2026-08-30.

AndroidX lifecycle ViewModels remain in the `nonWindows` source set and are intentionally excluded
from MinGW. The exported `PeopleInSpaceClient` does not start Koin or expose AndroidX types.

Consequently, the .NET client does not consume the existing Kotlin UI ViewModels. WinUI uses the
UI-independent C# `PeopleInSpaceViewModel` from `windows/Shared`; the UI project contains only
platform-specific presentation and lifecycle code.

## Generated bindings must be compiled once

`windows/Shared` is the sole project that compiles the NuGet package's generated `Interop.cs`.
`WinUiApp` references the same package only for its native runtime assets. Allowing an application
project to compile the package content files would create duplicate generated types and interop
declarations.

Any additional managed host should keep this project boundary: reference `windows/Shared` for the
managed API and include only the package's runtime/native assets itself.

Shipping the bindings as an assembly instead would remove this rule entirely; item 2 of "What kotlin-native-nuget needs to change" above.

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

Generated NuGet packages, native binaries, restore caches, and staged SQLite archives are build
products. They are neither committed nor published to an external feed.

A generated snapshot version would remove steps 2 and 3; item 3 of "What kotlin-native-nuget needs to change" above.

## Verification gaps

The Windows workflow packs the native package, inspects the expected native asset, restores from a
clean repository-local cache, builds the solution, and runs unit tests. The C# tests use a fake
source and intentionally avoid live network calls, so the generated Flow collection in
`KotlinPeopleInSpaceSource` is only exercised by launching the app.

CI does not currently launch the UI or verify image/network behavior end-to-end. Those remain manual
smoke tests. There are no automated WinUI UI tests.
