# GitHub Actions Workflows

This directory contains CI/CD workflows for the PeopleInSpace project.

## Workflows

### Platform-Specific Workflows

#### `android.yml` - Android CI
- **Trigger**: Pull requests
- **Jobs**:
  - Build: Builds Android app and runs unit tests
  - AndroidTest: Runs instrumentation tests on Android emulator (API 26)
- **Runner**: ubuntu-24.04

#### `ios.yml` - iOS CI
- **Trigger**: Pull requests
- **Jobs**: Builds iOS app using Xcode
- **Runner**: macos-14
- **Concurrency**: Cancels previous runs from same PR

#### `web.yml` - Web CI
- **Trigger**: Pull requests
- **Jobs**: Builds WebAssembly app
- **Runner**: ubuntu-24.04
- **Note**: Has commented-out deployment to GitHub Pages

#### `wearos.yml` - Wear OS CI
- **Trigger**: Pull requests
- **Jobs**: Builds Wear OS app
- **Runner**: ubuntu-24.04

#### `maestro.yml` - E2E Tests
- **Trigger**: Pull requests
- **Jobs**: Runs end-to-end tests using Maestro
- **Runner**: ubuntu-24.04

### Multiplatform Test Workflows

#### `compose-ui-tests.yml` - Compose UI Tests (Recommended)
- **Trigger**: Pull requests and pushes to main/master
- **Purpose**: Runs Compose Multiplatform UI tests from `common/src/commonTest`
- **Platform**: JVM (fastest and most reliable for CI)
- **Features**:
  - Runs all UI tests matching `*Ui*` pattern
  - Uploads test results as artifacts (7 day retention)
  - Publishes detailed test report with pass/fail information
  - Uses Gradle caching for faster builds
  - Cancels in-progress runs on new commits

**Test Files Covered**:
- `ComposeMultiplatformUiTests.kt` - Basic UI component tests
- `ISSPositionUiTests.kt` - ISS position display tests
- `ViewModelUiTests.kt` - ViewModel integration tests
- `TestTagExampleTests.kt` - Test tag usage examples

#### `multiplatform-tests.yml` - Full Platform Coverage
- **Trigger**: Pull requests and pushes to main/master
- **Purpose**: Runs common module tests on all supported platforms
- **Jobs**:
  1. **jvm-tests**: Runs tests on JVM (Linux)
  2. **android-tests**: Runs Android unit tests (Robolectric)
  3. **ios-tests**: Runs iOS simulator tests (macOS)
  4. **wasm-tests**: Runs WebAssembly tests
  5. **test-summary**: Aggregates results from all platforms

**Note**: This workflow is more comprehensive but takes longer to complete due to multiple platform builds.

## Choosing Between Workflows

### Use `compose-ui-tests.yml` when:
- ✅ You want fast feedback on UI tests (2-3 minutes)
- ✅ You're primarily testing UI logic that's platform-agnostic
- ✅ You want detailed test reports in PR checks
- ✅ You need quick iteration during development

### Use `multiplatform-tests.yml` when:
- ✅ You need to verify tests pass on all platforms
- ✅ You're testing platform-specific behavior
- ✅ You're preparing for a release
- ✅ You have time for longer CI runs (10-15 minutes)

## Local Testing

Before pushing, run tests locally to catch issues early:

```bash
# Run UI tests on JVM (fastest)
./gradlew :common:jvmTest --tests "*Ui*"

# Run all common tests on JVM
./gradlew :common:jvmTest

# Run Android tests
./gradlew :common:testDebugUnitTest

# Run iOS tests (macOS only)
./gradlew :common:iosSimulatorArm64Test

# Run WebAssembly tests
./gradlew :common:wasmJsTest

# Run all platform tests
./gradlew :common:test
```

## Test Results

Test results are uploaded as artifacts and accessible in the GitHub Actions UI:
- **Retention**: 7 days for UI test results
- **Format**: HTML reports and JUnit XML
- **Location**: Actions tab → Workflow run → Artifacts section

## Troubleshooting

### Tests Failing on CI but Passing Locally

1. Check for timing issues - CI might be slower:
   ```kotlin
   testDispatcher.scheduler.advanceUntilIdle()
   waitForIdle()
   ```

2. Verify test isolation - tests should not depend on order

3. Check platform-specific behavior

### Gradle Build Issues

- Ensure `kotlinUpgradeYarnLock` runs for WebAssembly builds
- Check Java version (requires JDK 17)
- Verify Gradle wrapper is up to date

### iOS Test Failures

- iOS tests require macOS runners (more expensive)
- Consider running iOS tests only on main branch or release tags

## Workflow Best Practices

1. **Fast Feedback**: `compose-ui-tests.yml` runs quickly (~3 min)
2. **Comprehensive Coverage**: `multiplatform-tests.yml` verifies all platforms
3. **Artifact Retention**: Test results kept for 7 days for debugging
4. **Concurrency Control**: Only latest run per PR/branch executes
5. **Clear Naming**: Test artifacts clearly labeled by platform

## Adding New Tests

When adding new UI tests to `common/src/commonTest`:

1. Follow naming convention: `*UiTests.kt`
2. Tests will automatically run in CI (matched by `*Ui*` pattern)
3. Use `@Test` annotation from `kotlin.test`
4. Use `runComposeUiTest` for compose UI tests
5. Add test tags for better test stability

Example:
```kotlin
@Test
fun myNewUiTest() = runComposeUiTest {
    setContent { MyComposable() }
    onNodeWithTag("myElement").assertIsDisplayed()
}
```

## Status Checks

Required status checks for PRs:
- Compose UI Tests (JVM) - from `compose-ui-tests.yml`
- Platform builds (Android, iOS, Web, Wear OS) - from platform workflows

Optional/informational checks:
- Full multiplatform test suite - from `multiplatform-tests.yml`

## Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Gradle Actions](https://github.com/gradle/actions)
- [Compose Multiplatform Testing](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html)
