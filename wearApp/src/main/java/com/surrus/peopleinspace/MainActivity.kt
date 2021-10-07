package com.surrus.peopleinspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.wear.compose.material.MaterialTheme
import coil.ImageLoader
import coil.compose.LocalImageLoader
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val peopleInSpaceRepository: PeopleInSpaceRepositoryInterface by inject()

    private val imageLoader: ImageLoader by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                MaterialTheme {
                    PersonList(peopleInSpaceRepository, personSelected = {})
                }
            }
        }
    }
}
