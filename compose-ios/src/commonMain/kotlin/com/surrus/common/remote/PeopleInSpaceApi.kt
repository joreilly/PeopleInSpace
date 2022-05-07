package com.surrus.common.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent

@Serializable
data class AstroResult(val message: String, val number: Int, val people: List<Assignment>)

@Serializable
data class Assignment(val craft: String, val name: String, var personImageUrl: String? = "", var personBio: String? = "")

@Serializable
data class IssPosition(val latitude: Double, val longitude: Double)

@Serializable
data class IssResponse(val message: String, val iss_position: IssPosition, val timestamp: Long)

class PeopleInSpaceApi(
    private val client: HttpClient,
    var baseUrl: String = "https://people-in-space-proxy.ew.r.appspot.com",
) : KoinComponent {
    suspend fun fetchPeople() = client.get("$baseUrl/astros.json").body<AstroResult>()
    suspend fun fetchISSPosition() = client.get("$baseUrl/iss-now.json").body<IssResponse>()
}
