package dev.johnoreilly.common.windows

import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PeopleInSpaceSnapshotReaderTest {
    @Test
    fun peopleCaptureRemainsConsistentUntilNextCapture() {
        val peopleState = MutableStateFlow(
            PeopleState(
                people = listOf(Person("Alice", "ISS", null, null, "Irish")),
                initialLoading = false,
            ),
        )
        val reader = PeopleInSpaceSnapshotReader(peopleState, MutableStateFlow(IssState()))

        assertEquals(1, reader.capturePeople())
        peopleState.value = PeopleState(
            people = listOf(
                Person("Bob", "Tiangong", null, null, "Chinese"),
                Person("Chris", "ISS", null, null, "American"),
            ),
            refreshing = true,
        )

        assertEquals("Alice", reader.capturedPersonName(0))
        assertFalse(reader.capturedPeopleRefreshing())
        assertEquals(2, reader.capturePeople())
        assertEquals("Bob", reader.capturedPersonName(0))
        assertTrue(reader.capturedPeopleRefreshing())
    }

    @Test
    fun issValuesChangeOnlyWhenCaptured() {
        val issState = MutableStateFlow(IssState(latitude = 1.0, longitude = 2.0, hasPosition = true))
        val reader = PeopleInSpaceSnapshotReader(MutableStateFlow(PeopleState()), issState)

        reader.captureIss()
        issState.value = IssState(latitude = 3.0, longitude = 4.0, hasPosition = true)

        assertEquals(1.0, reader.capturedIssLatitude())
        reader.captureIss()
        assertEquals(3.0, reader.capturedIssLatitude())
    }
}
