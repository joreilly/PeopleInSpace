# Windows client: known constraints

Verified against `kotlin-native-nuget` 0.3.0, Kotlin 2.4.10, .NET 10 on 2026-08-30. Setup and run
commands are in [`README.md`](README.md).

## What the Windows client shares

`PeopleInSpaceClient` (`common/src/mingwX64Main`) owns its HTTP client (WinHttp), SQLDelight
driver, repository and coroutine scope, and exposes two `StateFlow`s plus `refresh()` and
`close()`. The state it emits is projected from the same `PersonListUiState` /
`IssPositionUiState` that the Compose clients' ViewModels use (`personListUiState()` /
`issPositionUiState()` in `common/src/commonMain/.../viewmodel/UiStateFlows.kt`), and the list
items are the repository's own `Assignment` and `IssPosition`.

The Windows client does not use Koin: the AndroidX ViewModels live in the `nonWindows` source set
and Koin's compiler plugin is excluded from the MinGW compilations (see below), so the client
constructs its dependencies directly.

## Workarounds for kotlin-native-nuget 0.3.0

Each of these is tracked upstream and can be removed when the fix ships.

- **Sealed UI state cannot cross the boundary** — nested classes hit three separate generator
  bugs: [#38](https://github.com/xxfast/kotlin-native-nuget/issues/38) (nullable properties
  exported as non-null), [#39](https://github.com/xxfast/kotlin-native-nuget/issues/39) (list
  getters on sealed subclasses don't compile) and
  [#40](https://github.com/xxfast/kotlin-native-nuget/issues/40) (`FromHandle<T>` cannot
  materialise an abstract base). `ExportedState.kt` flattens the sealed state into top-level
  `PeopleState` / `IssState` envelopes for the export only.
- **Two exported packages** — `dev.johnoreilly.common.remote` is exported for `Assignment` and
  `IssPosition`, which triggers [#41](https://github.com/xxfast/kotlin-native-nuget/issues/41)
  (bare cross-namespace type names) and
  [#42](https://github.com/xxfast/kotlin-native-nuget/issues/42) (dangling `IKoinComponent`
  supertype on the Api classes in that package). `windows/WinUiApp/GeneratedBindingShims.cs`
  resolves both without editing the generated file.
- **Fixed package version** — the local package stays at `0.1.0`, so after any Kotlin change:
  `packNuget`, delete `windows/obj/packages`, restore `--force --no-cache`. The repository-local
  `RestorePackagesPath` keeps this away from the global NuGet cache.
- **Host-specific package** — the package contains only `runtimes/win-x64/native/peopleinspace.dll`
  and can only be packed on Windows; the MinGW link tasks are disabled elsewhere.

## Kotlin/Native and MinGW

- **Koin compiler plugin on MinGW** — Kotlin/Native's C adapter generation crashes on IR the Koin
  plugin generates ([KT-62984](https://youtrack.jetbrains.com/issue/KT-62984); fixed by
  [JetBrains/kotlin#7431](https://github.com/JetBrains/kotlin/pull/7431), not yet in a release).
  `common/build.gradle.kts` excludes `io.insert-koin` from the MinGW
  `kotlinCompilerPluginClasspath*` configurations. Every other target keeps annotation-driven DI.
  Drop the exclusion once the project builds on a Kotlin that contains the fix.
- **Static SQLite** — SQLDelight's MinGW driver needs `libsqlite3.a` at link time, staged at
  `common/build/mingw-sqlite/`. MSYS2 builds it with stack protection, and the Kotlin/Native MinGW
  toolchain (gcc 9.2) does not link `libssp` on its own, so the `link*MingwX64` tasks copy the
  toolchain's `libssp.a` alongside and link `-lssp`. Re-check when Kotlin/Native updates its bundled
  MinGW.

## Deployment

- `win-x64` only; unpackaged and framework-dependent (Windows App SDK 1.8 runtime required).
- No package identity, so packaged-only APIs such as `Windows.Storage.ApplicationData` throw
  `0x80073D54`; `LocalAppDataStore` uses `System.IO` under `%LOCALAPPDATA%` instead.
- CI packs the native library, restores from a clean cache and builds the app. There are no
  automated UI tests; launching the app is the smoke test for the generated Flow bindings.
