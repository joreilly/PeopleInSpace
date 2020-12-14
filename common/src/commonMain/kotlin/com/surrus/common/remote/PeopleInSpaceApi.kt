package com.surrus.common.remote

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import org.koin.core.KoinComponent

@Serializable
data class AstroResult(val message: String, val number: Int, val people: List<Assignment>)

@Serializable
data class Assignment(val craft: String, val name: String)

@Serializable
data class IssPosition(val latitude: Double, val longitude: Double)

@Serializable
data class IssResponse(val message: String, val iss_position: IssPosition, val timestamp: Long)


class PeopleInSpaceApi(
    private val client: HttpClient,
    private val baseUrl: String = "http://api.open-notify.org",
) : KoinComponent {
    suspend fun fetchPeople() = client.get<AstroResult>("$baseUrl/astros.json")
    suspend fun fetchISSPosition() = client.get<IssResponse>("$baseUrl/iss-now.json")
}
