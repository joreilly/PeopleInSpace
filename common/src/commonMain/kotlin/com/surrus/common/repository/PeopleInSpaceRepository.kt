package com.surrus.common.repository

import co.touchlab.kermit.Kermit
import com.surrus.common.model.personBios
import com.surrus.common.model.personImages
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.IssPosition
import com.surrus.common.remote.PeopleInSpaceApi
import io.realm.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.CoroutineContext


class AssignmentDb : RealmObject {
    var name: String = ""
    var craft: String = ""
}


@ExperimentalCoroutinesApi
class PeopleInSpaceRepository : KoinComponent  {
    private val peopleInSpaceApi: PeopleInSpaceApi by inject()
    private val logger: Kermit by inject()

    private val coroutineScope: CoroutineScope = MainScope()

    val configuration = RealmConfiguration(schema = setOf(AssignmentDb::class))
    val realm = Realm.open(configuration)


    var peopleJob: Job? = null

    init {
        coroutineScope.launch {
            fetchAndStorePeople()
        }
    }

    fun fetchPeopleAsFlow(): Flow<List<Assignment>> {
        return callbackFlow {
            realm.objects<AssignmentDb>().observe {
                val allPeople = it.toList().map {
                    Assignment(name = it.name, craft = it.craft)
                }
                offer(allPeople)
            }
            awaitClose {
            }
        }
    }

    private suspend fun fetchAndStorePeople()  {
        logger.d { "fetchAndStorePeople" }
        val result = peopleInSpaceApi.fetchPeople()

        // this is very basic implementation for now that removes all existing rows
        // in db and then inserts results from api request
        realm.beginTransaction()
        realm.objects<AssignmentDb>().delete()

        result.people.forEach {
            realm.create<AssignmentDb>().apply {
                name = it.name
                craft = it.craft
            }
        }
        realm.commitTransaction()
    }

    // Used by web client atm
    suspend fun fetchPeople() = peopleInSpaceApi.fetchPeople().people

    fun getPersonBio(personName: String): String {
        return personBios[personName] ?: ""
    }

    fun getPersonImage(personName: String): String {
        return personImages[personName] ?: ""
    }

    // called from Kotlin/Native clients
    fun startObservingPeopleUpdates(success: (List<Assignment>) -> Unit) {
        logger.d { "startObservingPeopleUpdates" }
        peopleJob = coroutineScope.launch {
            fetchPeopleAsFlow().collect {
                success(it)
            }
        }
    }

    fun stopObservingPeopleUpdates() {
        logger.d { "stopObservingPeopleUpdates, peopleJob = $peopleJob" }
        peopleJob?.cancel()
    }


    fun pollISSPosition(): Flow<IssPosition> = flow {
        while (true) {
            val position = peopleInSpaceApi.fetchISSPosition().iss_position
            emit(position)
            logger.d("PeopleInSpaceRepository") { position.toString() }
            delay(POLL_INTERVAL)
        }
    }



    val iosScope: CoroutineScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = SupervisorJob() + Dispatchers.Main
    }

    fun iosPollISSPosition() = KotlinNativeFlowWrapper<IssPosition>(pollISSPosition())


    companion object {
        private const val POLL_INTERVAL = 10000L
    }
}


class KotlinNativeFlowWrapper<T>(private val flow: Flow<T>) {
    fun subscribe(
        scope: CoroutineScope,
        onEach: (item: T) -> Unit,
        onComplete: () -> Unit,
        onThrow: (error: Throwable) -> Unit
    ) = flow
        .onEach { onEach(it) }
        .catch { onThrow(it) }
        .onCompletion { onComplete() }
        .launchIn(scope)
}

