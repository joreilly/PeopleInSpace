# WinUI 3 client

The Windows client consumes the Kotlin Multiplatform data layer from a locally generated
`PeopleInSpace.Kotlin` NuGet package:

| Project | Responsibility |
|---|---|
| `Shared` | Compiles the generated Kotlin bindings and projects Kotlin state into UI-independent C# view models |
| `Shared.Tests` | Tests the C# state projection, commands, dispatching, cancellation, and disposal with fake data sources |
| `WinUiApp` | WinUI 3 UI for Windows |

`Shared` is the only project that compiles the package's generated `Interop.cs`. `WinUiApp`
references the package only for its native runtime assets, avoiding duplicate generated types.

`KotlinPeopleInSpaceSource` collects the exported Kotlin `StateFlow`s directly through the generated
bindings and awaits the exported `suspend fun refresh()`. The C# `PersonInfo`, `PeopleSnapshot`, and
`IssSnapshot` records exist so `PeopleInSpaceViewModel` can be tested with a fake source and never
holds a native handle.

## Supported targets

- WinUI 3: `win-x64`

Other Windows architectures, MSIX packaging, installers, and self-contained deployment are not
configured.

## Local Kotlin NuGet package

Run all commands from the repository root. The Gradle task below builds the native library,
generates the C# bindings, and writes `PeopleInSpace.Kotlin.0.1.0.nupkg` under
`common/build/nuget`:

```powershell
.\gradlew.bat :common:packNuget
```

The package contains the generated bindings and `runtimes/win-x64/native/peopleinspace.dll`. It can
only be packed on Windows: the MinGW link tasks are disabled on other hosts.

`windows/NuGet.config` adds `common/build/nuget` as a local package source. Because the sample keeps
the package version at `0.1.0`, clear `windows/obj/packages` before restoring after rebuilding the
package. This prevents NuGet from reusing an older package with the same version.

## Prerequisites

- JDK 17
- .NET 10 SDK
- Windows 10 SDK 10.0.19041.0 or newer (Visual Studio is optional; the .NET SDK builds the solution
  on its own)
- Windows App SDK 1.8 runtime matching the version used by `WinUiApp`
- MSYS2 with the `mingw-w64-x86_64-sqlite3` package

### Provide static SQLite for Kotlin/Native

The MinGW SQLDelight driver requires SQLite while linking `peopleinspace.dll`. Install the package
from an MSYS2 shell:

```bash
pacman -S --needed mingw-w64-x86_64-sqlite3
```

Then stage its static archive from PowerShell. Linking this archive keeps SQLite inside
`peopleinspace.dll`, so the application does not need a separate SQLite runtime DLL:

```powershell
New-Item -ItemType Directory -Force common\build\mingw-sqlite | Out-Null
Copy-Item C:\msys64\mingw64\lib\libsqlite3.a common\build\mingw-sqlite\libsqlite3.a
```

MSYS2 compiles SQLite with stack protection, which the Kotlin/Native MinGW toolchain does not link
automatically. The `link*MingwX64` tasks copy the toolchain's own `libssp.a` into
`common/build/mingw-sqlite` and link it with `-lssp`, so no extra staging is needed.

### Pack, restore, build, and test

```powershell
.\gradlew.bat :common:packNuget
Remove-Item -Recurse -Force windows\obj\packages -ErrorAction SilentlyContinue
dotnet restore windows\PeopleInSpace.Windows.sln --force --no-cache
dotnet build windows\PeopleInSpace.Windows.sln --configuration Release -p:Platform=x64 --no-restore
dotnet test windows\Shared.Tests\PeopleInSpace.Windows.Shared.Tests.csproj --configuration Release -p:Platform=x64 --no-build
```

The generated-binding warnings are treated as errors along with the rest of the managed solution.

### Run WinUI 3

The WinUI application is unpackaged and framework-dependent. Install the matching Windows App SDK
runtime before launching it. The restored `Microsoft.WindowsAppSDK.Runtime` package carries the
runtime MSIX files, which can be registered for the current user without an installer:

```powershell
Get-ChildItem windows\obj\packages\microsoft.windowsappsdk.runtime\*\tools\MSIX\win10-x64\*.msix |
    ForEach-Object { Add-AppxPackage $_.FullName }
```

Packages that are already registered at a newer version are rejected with `0x80073D06`; that is
harmless.

Then launch the application:

```powershell
dotnet run --project windows\WinUiApp\PeopleInSpace.Windows.WinUiApp.csproj --configuration Debug -p:Platform=x64
```

Because the application has no package identity, it cannot use `Windows.Storage.ApplicationData`.
`LocalAppDataStore` keeps window state and the SQLDelight database under
`%LOCALAPPDATA%\PeopleInSpace`.

## Troubleshooting

### `PeopleInSpace.Kotlin` cannot be found

Run `:common:packNuget` first and confirm that the `.nupkg` exists in `common/build/nuget`. Restore
from the repository root so `windows/NuGet.config` is discovered.

### Changes to Kotlin are not visible in the app

Repack the NuGet package, remove `windows/obj/packages`, and restore with `--force --no-cache`.
The local package deliberately retains version `0.1.0`, so ordinary NuGet caching cannot distinguish
two successive local builds.

### MinGW linking cannot find SQLite

Confirm that `common/build/mingw-sqlite/libsqlite3.a` exists and came from the MSYS2 MinGW x64
package, then rerun `:common:packNuget`.

### MinGW linking reports undefined `__stack_chk_fail` or `__memcpy_chk`

The link task could not stage `libssp.a` from the Kotlin/Native toolchain. Confirm that
`%USERPROFILE%\.konan\dependencies` contains a `msys2-mingw-w64-x86_64-*` directory (or set
`KONAN_DATA_DIR` to the directory that does), then rerun `:common:packNuget`.

### WinUI exits at startup with `0xC000027B`

The stowed exception usually wraps `0x80073D54` (the process has no package identity), raised by a
packaged-only Windows Runtime API such as `ApplicationData.Current`. Keep unpackaged-safe
alternatives such as `Environment.GetFolderPath` in the WinUI project.

### WinUI starts only on a development machine

The application is unpackaged and framework-dependent. The destination machine must have the
matching Windows App SDK 1.8 runtime installed; the sample does not bundle that runtime.

## Continuous integration

The Windows workflow creates a fresh NuGet package, verifies `peopleinspace.dll`, clears the
repository-local NuGet cache, restores without cache, builds the solution, runs `Shared.Tests`, and
uploads the package and WinUI output.
