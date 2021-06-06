package model

import com.surrus.common.remote.Assignment

data class Person(
    val assignment: Assignment,
    val bio: String,
    val imageUrl: String
)
