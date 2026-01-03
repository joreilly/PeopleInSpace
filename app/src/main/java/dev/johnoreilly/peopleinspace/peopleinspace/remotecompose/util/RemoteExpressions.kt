package dev.johnoreilly.peopleinspace.peopleinspace.remotecompose.util

import android.annotation.SuppressLint
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.lerp
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.ui.geometry.Offset

/**
 * Linearly interpolates a value along the ISS orbit path based on the current time.
 *
 * This function builds a chain of conditional expressions (`select`) in the Remote Compose DSL to
 * determine which segment of the path the ISS is currently in, and then performs linear
 * interpolation (lerp) between the start and end of that segment.
 *
 * @param mapResult The result containing the drawn map bitmap and the list of path segments
 * ```
 *                  with their respective timestamps and canvas offsets.
 * @param currentTime
 * ```
 * A [RemoteFloat] representing the current time elapsed since the start of the path.
 * @param value A lambda that extracts the target [RemoteFloat] (e.g., x or y coordinate) from a
 * path point.
 * @return A [RemoteFloat] expression that evaluates to the interpolated value at [currentTime].
 */
@SuppressLint("RestrictedApi")
fun evaluateTime(
        mapResult: MapResult,
        currentTime: RemoteFloat,
        value: (RemoteFloat, Offset) -> RemoteFloat
): RemoteFloat {
    data class TimePoint(val time: RemoteFloat, val value: RemoteFloat)

    val firstTime = mapResult.pathSegments.first().first

    // Create a list of points (time since start, value at that time).
    // Note: We currently take only the first 6 points for simplicity/performance in the DSL.
    val points: List<TimePoint> =
            mapResult
                    .pathSegments
                    .map {
                        val timeAtPoint = (it.first - firstTime).toInt().rf
                        TimePoint(timeAtPoint, value(timeAtPoint, it.second))
                    }
                    .take(6)

    if (points.isEmpty()) return 0f.rf
    if (points.size == 1) return points[0].value

    var result: RemoteFloat = points.last().value

    // Build a nested select structure: if (time < p1.time) lerp(...) else if (time < p2.time) ...
    // This allows the Remote Compose engine to evaluate the correct position at any given frame.
    for (i in points.size - 2 downTo 0) {
        val p0 = points[i]
        val p1 = points[i + 1]
        val interpolation = lerp(p0.value, p1.value, (currentTime - p0.time) / (p1.time - p0.time))
        result = currentTime.lt(p1.time).select(interpolation, result)
    }

    return currentTime.lt(points[0].time).select(points[0].value, result)
}
