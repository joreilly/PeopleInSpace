package dev.johnoreilly.common.windows

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PeopleInSpaceModelsTest {
    @Test
    fun defaultPeopleStateRepresentsAnUnfinishedLoad() {
        val state = PeopleState()

        assertTrue(state.initialLoading)
        assertFalse(state.refreshing)
        assertTrue(state.people.isEmpty())
        assertNull(state.errorMessage)
    }

    @Test
    fun defaultIssStateDoesNotClaimToHaveAPosition() {
        val state = IssState()

        assertTrue(state.loading)
        assertFalse(state.hasPosition)
        assertNull(state.errorMessage)
    }
}
