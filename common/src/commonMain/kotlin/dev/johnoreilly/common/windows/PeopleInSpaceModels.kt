package dev.johnoreilly.common.windows

import kotlin.time.Instant

/** A person currently assigned to a spacecraft. */
data class Person(
    val name: String,
    val craft: String,
    val personImageUrl: String?,
    val personBio: String?,
    val nationality: String,
)

/**
 * Windows-friendly projection of the people repository.
 *
 * Errors are strings rather than [Throwable]s so the type remains convenient to
 * consume from the kotlin-native-nuget generated API.
 */
data class PeopleState(
    val people: List<Person> = emptyList(),
    /** True until the repository's first people synchronisation finishes. */
    val initialLoading: Boolean = true,
    /** True only for a later synchronisation, so cached people remain usable. */
    val refreshing: Boolean = false,
    val errorMessage: String? = null,
)

/** Windows-friendly projection of ISS polling state. */
data class IssState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Instant = Instant.fromEpochSeconds(0),
    val hasPosition: Boolean = false,
    val loading: Boolean = true,
    val errorMessage: String? = null,
)
