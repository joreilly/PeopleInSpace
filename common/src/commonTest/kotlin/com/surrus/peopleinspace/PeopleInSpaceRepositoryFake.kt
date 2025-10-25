package dev.johnoreilly.peopleinspace

import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.common.remote.IssPosition
import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class PeopleInSpaceRepositoryFake: PeopleInSpaceRepositoryInterface {
    val peopleList = listOf(
        Assignment("ISS", "Chris Cassidy", "https://example.com/cassidy.jpg", "American astronaut", "USA"),
        Assignment("ISS", "Anatoly Ivanishin", "https://example.com/ivanishin.jpg", "Russian cosmonaut", "Russia"),
        Assignment("ISS", "Ivan Vagner", "https://example.com/vagner.jpg", "Russian cosmonaut", "Russia")
    )

    val issPosition = IssPosition(53.2743394, -9.0514163)

    override fun fetchPeopleAsFlow(): Flow<List<Assignment>> {
        return flowOf(peopleList)
    }

    override fun pollISSPosition(): Flow<IssPosition> {
        return flowOf(issPosition)
    }

    override suspend fun fetchAndStorePeople() {
        // No-op for fake
    }
}
