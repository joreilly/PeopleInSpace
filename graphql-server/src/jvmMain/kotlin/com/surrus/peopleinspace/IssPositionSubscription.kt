package com.surrus.peopleinspace

import com.expediagroup.graphql.server.operations.Subscription
import com.surrus.common.remote.IssPosition
import com.surrus.common.remote.PeopleInSpaceApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asPublisher
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component



@Component
class IssPositionSubscription : Subscription {
    private val logger: Logger = LoggerFactory.getLogger(IssPositionSubscription::class.java)
    private var  peopleInSpaceApi: PeopleInSpaceApi = koin.get()


    fun issPosition(): Publisher<IssPosition> {
        return flow {
            while (true) {
                val position = peopleInSpaceApi.fetchISSPosition().iss_position
                logger.info("ISS position = $position")
                emit(position)
                delay(POLL_INTERVAL)
            }
        }.asPublisher()
    }

    companion object {
        private const val POLL_INTERVAL = 10000L
    }

}