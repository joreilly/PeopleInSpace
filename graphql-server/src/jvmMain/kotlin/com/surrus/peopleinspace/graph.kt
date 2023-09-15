package com.surrus.peopleinspace

import com.expediagroup.graphql.server.operations.Query
import com.surrus.common.remote.PeopleInSpaceApi
import com.surrus.common.remote.Assignment
import org.springframework.stereotype.Component

data class People(val people: List<Assignment>)

@Component
class RootQuery : Query {
  private var  peopleInSpaceApi: PeopleInSpaceApi = koin.get()

  suspend fun allPeople(): People = People(peopleInSpaceApi.fetchPeople().people)
}
