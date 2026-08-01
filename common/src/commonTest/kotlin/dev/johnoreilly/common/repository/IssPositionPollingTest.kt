package dev.johnoreilly.common.repository

import dev.johnoreilly.common.remote.IssPosition
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class IssPositionPollingTest {
    @Test
    fun retriesAfterFailureAndEmitsTheNextSuccessfulPosition() = runTest {
        var attempts = 0
        val errors = mutableListOf<Throwable?>()
        val loading = mutableListOf<Boolean>()
        val positions = mutableListOf<IssPosition>()

        val pollingJob = launch {
            issPositionPollingFlow(
                pollIntervalMillis = 1,
                fetchPosition = {
                    attempts++
                    if (attempts == 1) error("temporary failure")
                    IssPosition(latitude = 42.0, longitude = -71.0, timestamp = 99)
                },
                onLoadingChanged = { loading += it },
                onError = { errors += it },
            ).take(1).toList(positions)
        }
        advanceUntilIdle()

        pollingJob.join()

        assertEquals(2, attempts)
        assertEquals(listOf(IssPosition(42.0, -71.0, 99)), positions)
        assertTrue(errors.any { it is IllegalStateException })
        assertEquals(listOf(true, false, true, false), loading)
    }
}
