# WinUI 3 client

`windows/WinUiApp` consumes the Kotlin Multiplatform data layer from a locally generated
`PeopleInSpace.Kotlin` NuGet package. `PeopleInSpaceViewModel` collects the exported Kotlin
`StateFlow`s through the generated bindings and awaits the exported `suspend fun refresh()`; the
state it receives is the same sealed `PersonListUiState` / `IssPositionUiState` the Compose clients
use, carrying the shared `Assignment` and `IssPosition` models. See
[`LIMITATIONS.md`](LIMITATIONS.md) for the current constraints.

## Prerequisites

- JDK 17
- .NET 10 SDK
- Windows 10 SDK 10.0.19041.0 or newer (Visual Studio is optional)
- Windows App SDK 1.8 runtime matching the version used by `WinUiApp`
- MSYS2 with the `mingw-w64-x86_64-sqlite3` package

### Stage static SQLite for Kotlin/Native

The MinGW SQLDelight driver requires SQLite while linking `peopleinspace.dll`. Install it from an
MSYS2 shell, then copy the archive into place from PowerShell:

```bash
pacman -S --needed mingw-w64-x86_64-sqlite3
```

```powershell
New-Item -ItemType Directory -Force common\build\mingw-sqlite | Out-Null
Copy-Item C:\msys64\mingw64\lib\libsqlite3.a common\build\mingw-sqlite\libsqlite3.a
```

## Pack, restore, build

Run from the repository root. `packNuget` builds the native library, generates the C# bindings and
writes `PeopleInSpace.Kotlin.0.1.0-snapshot.<timestamp>.nupkg` under `common/build/nuget`, which
`windows/NuGet.config` adds as a local feed. Every pack mints a new snapshot version and pins it in
`build/PeopleInSpace.KotlinVersions.props`, which `windows/Directory.Build.props` imports, so a
plain restore always picks up the latest pack:

```powershell
.\gradlew.bat :common:packNuget
dotnet restore windows\PeopleInSpace.Windows.sln
dotnet build windows\PeopleInSpace.Windows.sln --configuration Release -p:Platform=x64 --no-restore
```

## Run

The app is unpackaged and framework-dependent. Register the Windows App SDK runtime from the
restored package once, then launch:

```powershell
Get-ChildItem windows\obj\packages\microsoft.windowsappsdk.runtime\*\tools\MSIX\win10-x64\*.msix |
    ForEach-Object { Add-AppxPackage $_.FullName }
dotnet run --project windows\WinUiApp\PeopleInSpace.Windows.WinUiApp.csproj --configuration Debug -p:Platform=x64
```

(`0x80073D06` from `Add-AppxPackage` means a newer runtime is already registered; that is fine.)
Window state and the SQLDelight database live under `%LOCALAPPDATA%\PeopleInSpace`.

## Troubleshooting

- **`PeopleInSpace.Kotlin` cannot be found or has an empty version** — run `:common:packNuget`
  first (it writes `build/PeopleInSpace.KotlinVersions.props`) and restore from the repository
  root so `windows/NuGet.config` is discovered.
- **Kotlin changes are not visible** — repack and restore; each pack is a new snapshot version.
- **Linking cannot find SQLite** — confirm `common/build/mingw-sqlite/libsqlite3.a` came from the
  MSYS2 MinGW x64 package.
- **Undefined `__stack_chk_fail` / `__memcpy_chk`** — the link task could not stage `libssp.a`
  from `%USERPROFILE%\.konan\dependencies\msys2-mingw-w64-x86_64-*` (or set `KONAN_DATA_DIR`).
- **Exit `0xC000027B` at startup** — a packaged-only Windows Runtime API was called from the
  unpackaged process; keep `System.IO`-based alternatives.
