# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PeopleInSpace is a Kotlin Multiplatform (KMP) project that displays people currently in space using data from The Space Devs API. It targets Android, iOS (SwiftUI), Desktop, Web (Wasm), Wear OS, plus a Ktor backend and MCP server.

## Build Commands

```bash
# Android
./gradlew :app:assembleDebug
./gradlew :app:test

# Desktop
./gradlew :compose-desktop:run

# Web (Wasm)
./gradlew :compose-web:wasmBrowserDevelopmentRun

# Backend (Ktor)
./gradlew :backend:run
./gradlew :backend:shadowJar

# MCP Server
./gradlew :mcp-server:shadowJar

# Common module tests
./gradlew :common:testDebugUnitTest
```

iOS is built via Xcode from `PeopleInSpaceSwiftUI/`.

## Module Structure

| Module | Purpose |
|--------|---------|
| `common` | Shared KMP code: API clients, repository, ViewModels, shared Compose UI |
| `app` | Android (Jetpack Compose, Material 3, Glance AppWidget) |
| `wearApp` | Wear OS (Compose for Wear, Horologist, Tiles) |
| `compose-desktop` | Desktop app (Compose for Desktop) |
| `compose-web` | Web app (Compose for Web, Kotlin/Wasm) |
| `backend` | Ktor server with Swagger UI at `/docs` |
| `mcp-server` | MCP server exposing `get-people-in-space` tool |
| `PeopleInSpaceSwiftUI/` | Native iOS app (Xcode project, consumes KMP framework via SKIE) |

## Architecture

**Shared code** lives in `common/src/commonMain/kotlin/dev/johnoreilly/common/`:
- `di/` — Koin DI with KSP annotations (`@Single`, `@Module`, `@KoinApplication`). Platform-specific modules use `expect`/`actual` (`NativeModule`, `ViewModelModule`).
- `remote/` — Ktor HTTP clients (`PeopleInSpaceApi`, `AstroviewerApi`). Base URL: `https://people-in-space-proxy.ew.r.appspot.com`. Platform-specific Ktor engines (Android, Darwin, Java, browser).
- `repository/` — `PeopleInSpaceRepository` is the single source of truth, caches to SQLDelight.
- `viewmodel/` — `PersonListViewModel`, `ISSPositionViewModel` using `StateFlow`.
- `ui/` — Shared Compose UI components (`ISSMapView`, `ISSPositionContent`).

**SQLDelight** schema at `common/src/commonMain/sqldelight/` with a single `People` table (name, craft, personImageUrl, personBio, nationality). Platform-specific drivers in each source set.

**State management**: MVVM with `androidx.lifecycle.viewmodel` (multiplatform), reactive via `StateFlow` and `Flow`.

## Key Libraries & Versions

Versions managed in `gradle/libs.versions.toml`:
- Kotlin 2.3.0, Compose Multiplatform 1.10.0, Ktor 3.4.0, SQLDelight 2.2.1
- Koin 4.1.1 (with Koin Annotations 2.3.1 + KSP), SKIE 0.10.9
- Coil 3.3.0 (image loading), Kermit 2.0.8 (logging), OsmDroid 6.1.20 (maps)

## Platform-Specific Notes

- **Android** uses Navigation Compose for routing, Coil for images, Material 3 Adaptive layouts
- **iOS** consumes the KMP framework via SKIE for native Swift/Flow interop
- **Backend** runs on Netty, port from `PORT` env var (default 8080), includes Swagger/OpenAPI
- **MCP Server** supports `--stdio` (for Claude Desktop) and `--sse-server` (default port 3001) modes
