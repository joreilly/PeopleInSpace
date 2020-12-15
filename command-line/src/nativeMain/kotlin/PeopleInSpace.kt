import com.surrus.common.di.initKoin
import com.surrus.common.remote.PeopleInSpaceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking(context = Dispatchers.Unconfined) {
        Platform.isMemoryLeakCheckerActive = false
        val koin = initKoin().koin
        val peopleInSpaceApi = koin.get<PeopleInSpaceApi>()
        println(loadAstronauts(peopleInSpaceApi).joinToString("\n"))
    }
}

private suspend fun loadAstronauts(peopleInSpaceApi: PeopleInSpaceApi): List<String> {
    println("Loading astronauts from space...")
    return peopleInSpaceApi
        .fetchPeople()
        .people
        .map { it.name }
        .sorted()
}
