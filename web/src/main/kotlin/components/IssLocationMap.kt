package components

import com.surrus.common.remote.IssPosition
import components.pigeonmaps.Map
import components.pigeonmaps.Marker
import react.RBuilder

fun RBuilder.IssLocationMap(issPosition: IssPosition) {
    val locationCoordinates = arrayOf(issPosition.latitude, issPosition.longitude)
    Map {
        attrs {
            height = 300
            defaultZoom = 4
            defaultCenter = locationCoordinates
        }

        Marker {
            attrs {
                width = 50
                anchor = locationCoordinates
            }
        }
    }
}