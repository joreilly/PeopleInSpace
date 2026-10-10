package dev.johnoreilly.common.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent

@Serializable
public data class AstroResult(val message: String, val number: Int, val people: List<Assignment>)

@Serializable
public data class Assignment(
    val craft: String,
    val name: String,
    var personImageUrl: String? = "",
    var personBio: String? = "",
    var nationality: String = "",
)

@Serializable
public data class IssPosition(val latitude: Double, val longitude: Double)

@Single
public class PeopleInSpaceApi internal constructor(private val client: HttpClient) : KoinComponent {
    public var baseUrl: String = "https://people-in-space-proxy.ew.r.appspot.com"
    internal var baseIssPositionUrl = "https://api.wheretheiss.at"

    public suspend fun fetchPeople(): AstroResult = client.get("$baseUrl/astros.json").body<AstroResult>()
    public suspend fun fetchISSPosition(): IssPosition = client.get("$baseIssPositionUrl/v1/satellites/25544").body<IssPosition>()
}
