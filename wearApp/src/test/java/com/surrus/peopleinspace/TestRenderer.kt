package com.surrus.peopleinspace

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.wear.tiles.DeviceParametersBuilders
import com.surrus.peopleinspace.previews.PreviewAstronauts
import com.surrus.peopleinspace.tile.PeopleInSpaceTile
import com.surrus.peopleinspace.tile.PeopleInSpaceTileRenderer
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.math.roundToInt
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
class TestRenderer {
    private val context = getApplicationContext<Context>()

    val renderer = PeopleInSpaceTileRenderer(context)

    @Test
    fun testRender() {
        val tile = renderer.renderTile(
            PeopleInSpaceTile.Astronauts(PreviewAstronauts),
            buildDeviceParameters(context.resources)
        )

        assertNotNull(tile)
    }
}

private fun buildDeviceParameters(resources: Resources): DeviceParametersBuilders.DeviceParameters {
    val displayMetrics: DisplayMetrics = resources.displayMetrics
    val isScreenRound: Boolean = true
    return DeviceParametersBuilders.DeviceParameters.Builder()
        .setScreenWidthDp((displayMetrics.widthPixels / displayMetrics.density).roundToInt())
        .setScreenHeightDp((displayMetrics.heightPixels / displayMetrics.density).roundToInt())
        .setScreenDensity(displayMetrics.density).setScreenShape(
            if (isScreenRound) DeviceParametersBuilders.SCREEN_SHAPE_ROUND
            else DeviceParametersBuilders.SCREEN_SHAPE_RECT
        ).setDevicePlatform(DeviceParametersBuilders.DEVICE_PLATFORM_WEAR_OS).build()
}
