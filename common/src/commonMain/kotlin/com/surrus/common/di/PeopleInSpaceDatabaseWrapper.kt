package com.surrus.common.di

import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import org.koin.core.annotation.Single

@Single
class PeopleInSpaceDatabaseWrapper(val instance: PeopleInSpaceDatabase?)
