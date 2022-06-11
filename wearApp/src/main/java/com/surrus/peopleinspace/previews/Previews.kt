package com.surrus.peopleinspace.previews

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.surrus.common.remote.Assignment

val NeilArmstrong = Assignment(
    craft = "Apollo 11",
    name = "Neil Armstrong",
    personImageUrl = "https://www.biography.com/.image/ar_1:1%2Cc_fill%2Ccs_srgb%2Cfl_progressive%2Cq_auto:good%2Cw_1200/MTc5OTk0MjgyMzk5MTE0MzYy/gettyimages-150832381.jpg"
)

val BuzzAldrin = Assignment(
    craft = "Apollo 11",
    name = "Buzz Aldrin",
    personImageUrl = "https://nypost.com/wp-content/uploads/sites/2/2018/06/buzz-aldrin.jpg?quality=80&strip=all"
)

val PreviewAstronauts = listOf(
    NeilArmstrong,
    BuzzAldrin,
    NeilArmstrong,
    BuzzAldrin,
    NeilArmstrong,
    BuzzAldrin
)

class AstronautSelections: PreviewParameterProvider<List<Assignment>> {
    override val values: Sequence<List<Assignment>>
        get() = (0..5).asSequence().map { PreviewAstronauts.take(it) }
}

@Preview(
    backgroundColor = 0xff000000, showBackground = true, widthDp = 100, heightDp = 100
)
annotation class IconSizePreview