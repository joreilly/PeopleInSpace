# Compose Multiplatform UI Tests

This directory contains Compose Multiplatform UI tests that can run across multiple platforms (Android, iOS, Desktop, Web).

## Overview

The tests use the **Compose Multiplatform UI Testing framework** which provides a platform-agnostic way to test Compose UI components. Unlike platform-specific tests (e.g., Android's `createComposeRule()`), these tests use `runComposeUiTest` which works across all supported platforms.

## Test Files

### 1. `ComposeMultiplatformUiTests.kt`
Basic UI component tests demonstrating:
- Testing simple composables (`CoordinateDisplay`)
- Verifying text content is displayed
- Testing with different input values
- Basic assertions (`assertIsDisplayed`, `assertExists`)

### 2. `ISSPositionUiTests.kt`
Tests for ISS position display components:
- Testing with realistic data from fake repository
- Testing edge cases (zero coordinates, negative values, extremes)
- Demonstrating data-driven testing patterns

### 3. `ViewModelUiTests.kt`
Advanced tests showing state-based UI testing:
- Testing UI components with StateFlow (the pattern used by ViewModels)
- Managing coroutine test dispatchers
- Testing state transitions (Loading → Success → Error)
- Verifying UI reacts to state changes
- Note: Uses StateFlow directly instead of actual ViewModels (which use Koin DI)

### 4. `TestTagExampleTests.kt`
Best practices for using test tags:
- Finding elements by test tag
- Testing element hierarchies
- Combining test tags with text matching
- Constants for test tag names

### 5. `PeopleInSpaceRepositoryFake.kt`
Test double providing consistent test data for UI tests.

## Key Differences from Platform-Specific Tests

### Android Tests (app module)
```kotlin
class MyTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun myTest() {
        composeTestRule.setContent { /* ... */ }
        composeTestRule.onNodeWithText("Hello").assertIsDisplayed()
    }
}
```

### Multiplatform Tests (common module)
```kotlin
@OptIn(ExperimentalTestApi::class)
class MyTest {
    @Test
    fun myTest() = runComposeUiTest {
        setContent { /* ... */ }
        onNodeWithText("Hello").assertIsDisplayed()
    }
}
```

**Note**: The `@OptIn(ExperimentalTestApi::class)` annotation is required because the Compose Multiplatform UI testing API is currently experimental. Add this annotation to your test classes to suppress experimental API warnings.

## Running the Tests

### Run all common tests
```bash
./gradlew :common:test
```

### Run tests for specific platform
```bash
# Android
./gradlew :common:testDebugUnitTest

# iOS (requires macOS)
./gradlew :common:iosSimulatorArm64Test

# JVM/Desktop
./gradlew :common:jvmTest

# WebAssembly
./gradlew :common:wasmJsTest
```

### Run tests in IDE
- IntelliJ IDEA/Android Studio: Right-click on test file or test method and select "Run"
- Tests will run on the JVM by default when run from IDE
- To run on specific platform, use Gradle commands

## Test Structure

All tests follow this pattern:

```kotlin
@OptIn(ExperimentalTestApi::class)
class MyUiTests {
    @Test
    fun testName() = runComposeUiTest {
        // 1. Setup (if needed)
        val viewModel = MyViewModel(fakeRepository)

        // 2. Set content
        setContent {
            MaterialTheme {
                MyComposable(viewModel)
            }
        }

        // 3. Advance time (for async operations)
        testDispatcher.scheduler.advanceUntilIdle()
        waitForIdle()

        // 4. Assert
        onNodeWithText("Expected Text").assertIsDisplayed()
    }
}
```

### Important: Experimental API

The Compose Multiplatform UI testing framework is currently experimental. You must add the `@OptIn(ExperimentalTestApi::class)` annotation to your test classes to use `runComposeUiTest` and suppress experimental API warnings.

## Common Test Assertions

### Existence
```kotlin
onNodeWithTag("myTag").assertExists()
onNodeWithTag("myTag").assertDoesNotExist()
```

### Visibility
```kotlin
onNodeWithText("Hello").assertIsDisplayed()
onNodeWithText("Hidden").assertIsNotDisplayed()
```

### Text Content
```kotlin
onNodeWithTag("title").assertTextEquals("Hello, World!")
onNodeWithText("Hello", substring = true).assertExists()
```

### Hierarchy
```kotlin
onNodeWithTag("container")
    .onChildren()
    .assertCountEquals(5)

onNodeWithTag("list")
    .onChildAt(0)
    .assertTextContains("First Item")
```

### Interactions
```kotlin
onNodeWithTag("button").performClick()
onNodeWithTag("textField").performTextInput("Hello")
onNodeWithTag("scrollable").performScrollTo()
```

## Best Practices

### 1. Use Test Tags
Always add test tags to key UI elements:
```kotlin
LazyColumn(
    modifier = Modifier.testTag("PersonList")
) {
    items(people) { person ->
        PersonView(person, modifier = Modifier.testTag("person_${person.id}"))
    }
}
```

### 2. Define Test Tag Constants
```kotlin
object TestTags {
    const val PERSON_LIST = "PersonList"
    const val ISS_MAP = "ISSMap"
    const val REFRESH_BUTTON = "RefreshButton"
}
```

### 3. Use Fake Repositories
Create test doubles that provide consistent, predictable data:
```kotlin
class PeopleInSpaceRepositoryFake : PeopleInSpaceRepositoryInterface {
    val peopleList = listOf(/* test data */)
    override fun fetchPeopleAsFlow() = flowOf(peopleList)
}
```

### 4. Test State Changes
Verify UI responds to state updates:
```kotlin
@Test
fun testLoadingState() = runComposeUiTest {
    setContent { MyScreen(uiState = UiState.Loading) }
    onNodeWithTag("loadingIndicator").assertExists()
}

@Test
fun testSuccessState() = runComposeUiTest {
    setContent { MyScreen(uiState = UiState.Success(data)) }
    onNodeWithTag("content").assertExists()
}
```

### 5. Test User Interactions
```kotlin
@Test
fun testButtonClick() = runComposeUiTest {
    var clicked = false
    setContent {
        Button(onClick = { clicked = true }) {
            Text("Click Me")
        }
    }

    onNodeWithText("Click Me").performClick()
    // Assert on state change
}
```

## Testing State-Based UI Components

The ViewModels in this project use Koin dependency injection and don't accept constructor parameters. Therefore, UI tests focus on testing components with StateFlow directly:

1. **Setup test dispatcher**:
```kotlin
private val testDispatcher = StandardTestDispatcher()

@BeforeTest
fun setup() {
    Dispatchers.setMain(testDispatcher)
}

@AfterTest
fun tearDown() {
    Dispatchers.resetMain()
}
```

2. **Create mock state flows**:
```kotlin
val uiStateFlow = MutableStateFlow(PersonListUiState.Success(fakeData))
```

3. **Test state transitions**:
```kotlin
// Start with loading
val stateFlow = MutableStateFlow(UiState.Loading)
setContent { MyComposable(stateFlow) }
onNodeWithText("Loading...").assertIsDisplayed()

// Transition to success
stateFlow.value = UiState.Success(data)
waitForIdle()
onNodeWithText("Data").assertIsDisplayed()
```

4. **Advance time for coroutines**:
```kotlin
testDispatcher.scheduler.advanceUntilIdle()
waitForIdle()
```

This approach tests the UI layer independently of ViewModel implementation details.

## Platform-Specific Considerations

### Android
- Tests run on JVM by default (Robolectric)
- Can run on emulator/device with `testDebugUnitTest`

### iOS
- Requires macOS to run
- Uses iOS Simulator

### Desktop (JVM)
- Runs natively on JVM
- Fastest platform for local testing

### Web (WASM)
- Requires WebAssembly setup
- May have limitations with certain APIs

## Limitations

### Current Limitations of Multiplatform UI Testing:

1. **Platform-specific components**: `expect/actual` composables (like `ISSMapView`) may need platform-specific tests or mock implementations

2. **Some APIs**: Certain platform-specific APIs may not be available in common tests

3. **Screenshots**: Screenshot testing requires platform-specific implementations

### Workarounds:

1. **Mock expect functions**: Create test implementations of expect functions
2. **Test interfaces**: Test against interfaces rather than implementations
3. **Separate platform tests**: Keep platform-specific UI tests in platform modules

## Examples from Project

### Testing CoordinateDisplay
```kotlin
@OptIn(ExperimentalTestApi::class)
class CoordinateDisplayTests {
    @Test
    fun testCoordinateDisplay() = runComposeUiTest {
        setContent {
            CoordinateDisplay(label = "Latitude", value = "53.27")
        }
        onNodeWithText("Latitude").assertIsDisplayed()
        onNodeWithText("53.27").assertIsDisplayed()
    }
}
```

### Testing with StateFlow (ViewModel Pattern)
```kotlin
@OptIn(ExperimentalTestApi::class)
class StateBasedUiTests {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun testWithStateFlow() = runComposeUiTest {
        // Create mock state flow
        val positionFlow = MutableStateFlow(IssPosition(53.27, -9.05))

        setContent {
            // Composable that accepts StateFlow
            ISSPositionContent(positionFlow)
        }

        testDispatcher.scheduler.advanceUntilIdle()
        waitForIdle()

        onNodeWithText("53.27").assertIsDisplayed()
    }

    @Test
    fun testStateTransition() = runComposeUiTest {
        val stateFlow = MutableStateFlow(UiState.Loading)
        setContent { MyComposable(stateFlow) }

        onNodeWithText("Loading...").assertExists()

        stateFlow.value = UiState.Success(data)
        waitForIdle()

        onNodeWithText("Success").assertExists()
    }
}
```

**Why StateFlow instead of actual ViewModels?**
The ViewModels in this project use Koin for dependency injection and don't accept constructor parameters. Testing with StateFlow allows us to test the UI layer independently without setting up complex Koin test modules.

## Resources

- [Compose Multiplatform Testing Docs](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html)
- [Compose Testing Cheatsheet](https://developer.android.com/jetpack/compose/testing-cheatsheet)
- [Testing State in Compose](https://developer.android.com/jetpack/compose/testing#test-state)

## Contributing

When adding new UI tests:
1. Add `@OptIn(ExperimentalTestApi::class)` to your test class
2. Follow the existing naming conventions (e.g., `*UiTests.kt`)
3. Add test tags to new composables
4. Create test composables for complex scenarios
5. Document any platform-specific limitations
6. Keep tests fast and focused
