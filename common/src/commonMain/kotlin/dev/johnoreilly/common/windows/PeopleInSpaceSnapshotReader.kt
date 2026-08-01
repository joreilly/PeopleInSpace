package dev.johnoreilly.common.windows

import kotlinx.coroutines.flow.StateFlow

/**
 * Captures an immutable state value for managed consumers that need to read it through primitive
 * accessors. Keeping the captured value stable avoids inconsistent list reads if a flow updates
 * between the count and element calls.
 */
internal class PeopleInSpaceSnapshotReader(
    private val peopleState: StateFlow<PeopleState>,
    private val issState: StateFlow<IssState>,
) {
    private var capturedPeople = peopleState.value
    private var capturedIss = issState.value

    fun capturePeople(): Int {
        capturedPeople = peopleState.value
        return capturedPeople.people.size
    }

    fun capturedPersonName(index: Int) = capturedPeople.people[index].name
    fun capturedPersonCraft(index: Int) = capturedPeople.people[index].craft
    fun capturedPersonNationality(index: Int) = capturedPeople.people[index].nationality
    fun capturedPersonImageUrl(index: Int) = capturedPeople.people[index].personImageUrl
    fun capturedPersonBio(index: Int) = capturedPeople.people[index].personBio
    fun capturedPeopleInitialLoading() = capturedPeople.initialLoading
    fun capturedPeopleRefreshing() = capturedPeople.refreshing
    fun capturedPeopleErrorMessage() = capturedPeople.errorMessage

    fun captureIss() {
        capturedIss = issState.value
    }

    fun capturedIssLatitude() = capturedIss.latitude
    fun capturedIssLongitude() = capturedIss.longitude
    fun capturedIssTimestamp() = capturedIss.timestamp
    fun capturedIssHasPosition() = capturedIss.hasPosition
    fun capturedIssLoading() = capturedIss.loading
    fun capturedIssErrorMessage() = capturedIss.errorMessage
}
