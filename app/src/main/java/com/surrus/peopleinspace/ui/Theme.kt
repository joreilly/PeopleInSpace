package com.surrus.peopleinspace.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable


private val DarkColorPalette = darkColors(
        primary = maroon200,
        primaryVariant = maroon700,
        secondary = teal200
)

private val LightColorPalette = lightColors(
        primary = maroon500,
        primaryVariant = maroon700,
        secondary = teal200
)

@Composable
fun PeopleInSpaceTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}