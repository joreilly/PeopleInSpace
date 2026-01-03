package dev.johnoreilly.common.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent

@Serializable
data class OrbitResult(val sat: Int, val tRef: Long, val orbitData: List<OrbitPoint>)

@Serializable
data class OrbitPoint(val t: Long, val lt: Double, val ln: Double, val h: Float = Float.NaN, val v: Float = Float.NaN, val s: Boolean = true) {
}

@Single
class AstroviewerApi(private val client: HttpClient) : KoinComponent {
    var baseUrl = "https://www.astroviewer.net/iss/ws"

    suspend fun fetchISSFuturePositions() = client.get("$baseUrl/orbit.php?sat=25544").body<OrbitResult>()
}
