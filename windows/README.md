# WinUI 3 and .NET MAUI clients

The .NET clients consume the Kotlin Multiplatform data layer from a locally generated
`PeopleInSpace.Kotlin` NuGet package. They share managed state and behavior while keeping their UI
platform-specific:

| Project | Responsibility |
|---|---|
| `Shared` | Compiles the generated Kotlin bindings and projects Kotlin state into UI-independent C# view models |
| `Shared.Tests` | Tests the C# state projection, commands, dispatching, cancellation, and disposal with fake data sources |
| `WinUiApp` | WinUI 3 UI for Windows |
| `MauiApp` | .NET MAUI UI for Windows and Mac Catalyst |

`Shared` is the only project that compiles the package's generated `Interop.cs`. The application
projects reference the package only for its native runtime assets, avoiding duplicate generated
types. Both applications use the same `PeopleInSpaceViewModel`; only navigation, pages, controls,
and UI-thread dispatchers live in the UI projects.

## Supported targets

- WinUI 3: `win-x64`
- .NET MAUI on Windows: `win-x64`
- .NET MAUI on Mac Catalyst: Apple silicon (`maccatalyst-arm64`)

Other Windows architectures, Intel Macs, MSIX packaging, installers, and self-contained deployment
are not configured.

## Local Kotlin NuGet package

Run all commands from the repository root. The Gradle task below builds the native library,
generates the C# bindings, and writes `PeopleInSpace.Kotlin.0.1.0.nupkg` under
`common/build/nuget`:

```text
./gradlew :common:packNuget
```

On Windows, use `gradlew.bat` instead:

```powershell
.\gradlew.bat :common:packNuget
```

The package contains the generated bindings and the native library for the current host:

- Windows: `runtimes/win-x64/native/peopleinspace.dll`
- macOS: `runtimes/osx-arm64/native/libpeopleinspace.dylib`

`windows/NuGet.config` adds `common/build/nuget` as a local package source. Because the sample keeps
the package version at `0.1.0`, clear `windows/obj/packages` before restoring after rebuilding the
package. This prevents NuGet from reusing an older package with the same version.

## Windows prerequisites

- JDK 17
- .NET 10 SDK
- Visual Studio 2022 with the Windows application development workload
- Windows 10 SDK 10.0.19041.0 or newer
- Windows App SDK 1.8 runtime matching the version used by `WinUiApp`
- MSYS2 with the `mingw-w64-x86_64-sqlite3` package
- The MAUI Windows workload when building `MauiApp`

Install the MAUI workload from PowerShell if it is not already available:

```powershell
dotnet workload install maui-windows
```

### Provide static SQLite for Kotlin/Native

The MinGW SQLDelight driver requires SQLite while linking `peopleinspace.dll`. Install the package
from an MSYS2 shell:

```bash
pacman -S --needed mingw-w64-x86_64-sqlite3
```

Then stage its static archive from PowerShell. Linking this archive keeps SQLite inside
`peopleinspace.dll`, so the applications do not need a separate SQLite runtime DLL:

```powershell
New-Item -ItemType Directory -Force common\build\mingw-sqlite | Out-Null
Copy-Item C:\msys64\mingw64\lib\libsqlite3.a common\build\mingw-sqlite\libsqlite3.a
```

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
runtime before launching it:

```powershell
dotnet run --project windows\WinUiApp\PeopleInSpace.Windows.WinUiApp.csproj --configuration Debug -p:Platform=x64
```

### Run .NET MAUI on Windows

```powershell
dotnet build windows\MauiApp\PeopleInSpace.Windows.MauiApp.csproj -t:Run -f net10.0-windows10.0.19041.0 -p:Platform=x64
```

Both applications store the SQLDelight database under the current user's local application data
directory.

## Mac Catalyst prerequisites

- Apple silicon Mac
- JDK 17
- .NET 10 SDK
- A current Xcode installation selected with `xcode-select`
- .NET MAUI Mac Catalyst workload

Install the workload if needed:

```bash
dotnet workload install maui-maccatalyst
```

### Pack, restore, test, and run

```bash
./gradlew :common:packNuget
rm -rf windows/obj/packages
dotnet restore windows/Shared.Tests/PeopleInSpace.Windows.Shared.Tests.csproj --force --no-cache
dotnet test windows/Shared.Tests/PeopleInSpace.Windows.Shared.Tests.csproj --configuration Release --no-restore
dotnet restore windows/MauiApp/PeopleInSpace.Windows.MauiApp.csproj -p:TargetFramework=net10.0-maccatalyst --force --no-cache
dotnet build windows/MauiApp/PeopleInSpace.Windows.MauiApp.csproj --configuration Debug --framework net10.0-maccatalyst --no-restore
open -n "windows/MauiApp/bin/Debug/net10.0-maccatalyst/maccatalyst-arm64/People in Space.app"
```

Build the application explicitly before opening it. Invoking only MSBuild's `Run` target can launch
an existing app bundle without first rebuilding it.

Mac Catalyst is an AOT-only environment. The managed adapter reads the exported Kotlin
`StateFlow.Value` properties and projects snapshots on a short polling interval rather than using
the generic callback bindings generated by `kotlin-native-nuget` 0.2.0, which would require runtime
JIT compilation.

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

### WinUI starts only on a development machine

The application is unpackaged and framework-dependent. The destination machine must have the
matching Windows App SDK 1.8 runtime installed; the sample does not bundle that runtime.

## Continuous integration

The Windows workflow creates a fresh NuGet package, verifies `peopleinspace.dll`, clears the
repository-local NuGet cache, restores without cache, builds the solution, runs `Shared.Tests`, and
uploads the package and WinUI output. The macOS workflow performs the corresponding native-library
check and builds the Mac Catalyst application.
