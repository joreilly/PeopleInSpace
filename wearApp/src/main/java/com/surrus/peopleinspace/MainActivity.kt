package com.surrus.peopleinspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.wear.compose.material.MaterialTheme
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val peopleInSpaceRepository: PeopleInSpaceRepositoryInterface by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                PersonList(peopleInSpaceRepository, personSelected = {})
            }
        }
    }
}
