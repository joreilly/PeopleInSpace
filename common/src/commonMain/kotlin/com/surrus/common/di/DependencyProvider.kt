package com.surrus.common.di

import com.surrus.common.repository.createDb

interface IDatabaseDependencyProvider

fun createDbClient(
    dependencyProvider: IDatabaseDependencyProvider = object : IDatabaseDependencyProvider {}
) = createDb(dependencyProvider)