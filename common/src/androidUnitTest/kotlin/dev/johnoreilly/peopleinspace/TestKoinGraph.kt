package dev.johnoreilly.peopleinspace

import dev.johnoreilly.common.di.commonModule
import dev.johnoreilly.common.repository.platformModule
import dev.johnoreilly.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify
import kotlin.test.Test


@OptIn(KoinExperimentalAPI::class)
class TestKoinGraph {

    @Test
    fun checkKoinModules() {
        val modules = module {
            includes(commonModule(false), platformModule())
        }

        modules.verify(
            extraTypes = listOf(HttpClientEngine::class, HttpClientConfig::class, PeopleInSpaceDatabase::class)
        )
    }
}
