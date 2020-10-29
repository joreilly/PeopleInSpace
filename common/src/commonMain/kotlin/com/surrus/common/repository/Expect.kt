package com.surrus.common.repository

import co.touchlab.kermit.Logger
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase

expect fun createDb() : PeopleInSpaceDatabase?

expect fun getLogger(): Logger