# Windows client: known constraints

Verified against `kotlin-native-nuget` 0.4.0, Kotlin 2.4.10, .NET 10 on 2026-09-02. Setup and run
commands are in [`README.md`](README.md).

## What the Windows client shares

`PeopleInSpaceClient` (`common/src/mingwX64Main`) owns its HTTP client (WinHttp), SQLDelight
driver, repository and coroutine scope, and exposes two `StateFlow`s plus `refresh()` and
`close()`. The state it emits is the same sealed `PersonListUiState` / `IssPositionUiState` that
the Compose clients' ViewModels use (`personListUiState()` / `issPositionUiState()` in
`common/src/commonMain/.../viewmodel/UiStateFlows.kt`), and the list items are the repository's
own `Assignment` and `IssPosition`. The generated C# renders each sealed class as an abstract class
with its subtypes nested inside it, so the ViewModel pattern-matches on `Loading` / `Error` /
`Success`.

The Windows client does not use Koin: the AndroidX ViewModels live in the `nonWindows` source set
and Koin's compiler plugin is excluded from the MinGW compilations (see below), so the client
constructs its dependencies directly.

## kotlin-native-nuget

- **Bare cross-namespace names on sealed subclasses**
  ([#50](https://github.com/xxfast/kotlin-native-nuget/issues/50)) — 0.4.0 fixed
  [#41](https://github.com/xxfast/kotlin-native-nuget/issues/41) for top-level classes, but a
  property getter on a sealed subclass still names a type from another exported package without
  its namespace (`PersonListUiState.Success.Result`, `IssPositionUiState.Success.Position` refer
  to `Assignment` / `IssPosition` from the `Remote` namespace).
  `windows/WinUiApp/GeneratedBindingShims.cs` aliases the two names globally so the generated
  file compiles unedited.
- **Host-specific package** — the package contains only `runtimes/win-x64/native/peopleinspace.dll`
  and can only be packed on Windows; the MinGW link tasks are disabled elsewhere.
- **Snapshot versions** — `publish { snapshot = true }` gives every `packNuget` a fresh
  `0.1.0-snapshot.<timestamp>` identity and pins it in `build/PeopleInSpace.KotlinVersions.props`,
  which `windows/Directory.Build.props` imports. The .NET solution therefore cannot restore until
  the package has been packed once, and old snapshots accumulate under `common/build/nuget`
  (cleared by Gradle `clean`) and `windows/obj/packages` (delete it by hand).

## Kotlin/Native and MinGW

- **Compiler plugins on MinGW** — the Compose compiler refuses to run without the Compose runtime
  on the classpath, which has no MinGW artifacts, so it is excluded from the MinGW compilations
  (nothing there is composable). Koin's plugin is excluded too: Kotlin/Native's C adapter
  generation crashes on IR the Koin plugin generates ([KT-62984](https://youtrack.jetbrains.com/issue/KT-62984); fixed by
  [JetBrains/kotlin#7431](https://github.com/JetBrains/kotlin/pull/7431), not yet in a release).
  Both exclusions live on the MinGW `kotlinCompilerPluginClasspath*` configurations in
  `common/build.gradle.kts`; every other target keeps both plugins. The Koin one can go once the
  project builds on a Kotlin that contains the fix.
- **Static SQLite** — SQLDelight's MinGW driver needs `libsqlite3.a` at link time, staged at
  `common/build/mingw-sqlite/`. MSYS2 builds it with stack protection, and the Kotlin/Native MinGW
  toolchain (gcc 9.2) does not link `libssp` on its own, so the `link*MingwX64` tasks copy the
  toolchain's `libssp.a` alongside and link `-lssp`. Re-check when Kotlin/Native updates its bundled
  MinGW.

## Deployment

- `win-x64` only; unpackaged and framework-dependent (Windows App SDK 1.8 runtime required).
- No package identity, so packaged-only APIs such as `Windows.Storage.ApplicationData` throw
  `0x80073D54`; `LocalAppDataStore` uses `System.IO` under `%LOCALAPPDATA%` instead.
- CI packs the native library, restores and builds the app. There are no automated UI tests;
  launching the app is the smoke test for the generated Flow bindings.
