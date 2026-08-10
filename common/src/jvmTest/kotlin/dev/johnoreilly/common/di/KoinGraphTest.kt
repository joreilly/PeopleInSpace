package dev.johnoreilly.common.di

import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import io.ktor.client.HttpClient
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * The Koin compiler plugin verifies the graph at compile time, but only for the compilations it is
 * applied to. This resolves the graph for real so a broken annotation wiring cannot pass unnoticed.
 */
class KoinGraphTest {

    @AfterTest
    fun tearDown() = stopKoin()

    @Test
    fun resolvesTheAnnotatedApplicationGraph() {
        val koin = initKoin().koin

        assertNotNull(koin.get<PeopleInSpaceRepositoryInterface>())
        assertNotNull(koin.get<HttpClient>())
        assertNotNull(koin.get<PeopleInSpaceDatabaseWrapper>())
    }
}
