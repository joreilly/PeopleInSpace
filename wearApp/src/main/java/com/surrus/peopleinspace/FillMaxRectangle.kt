package androidx.wear.compose.samples.shared

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.InspectorValueInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import kotlin.math.sqrt

@Stable
fun Modifier.fillMaxRectangle() = composed {
  val isRound = LocalContext.current.resources.configuration.isScreenRound

  var inset: Dp = 0.dp

  if (isRound) {
    val screenHeightDp = LocalContext.current.resources.configuration.screenHeightDp
    val screenWidthDp = LocalContext.current.resources.configuration.smallestScreenWidthDp
    val maxSquareEdge = (sqrt(((screenHeightDp * screenWidthDp) / 2).toDouble()))
    inset = Dp(((screenHeightDp - maxSquareEdge) / 2).toFloat())
  }
  this.then(RectangleInsetModifier(
    inset = inset,
    inspectorInfo = debugInspectorInfo {
      name = "fillMaxRectangle"
    })
  )
}

private class RectangleInsetModifier(
  val inset: Dp = 0.dp,
  inspectorInfo: InspectorInfo.() -> Unit
) : LayoutModifier, InspectorValueInfo(inspectorInfo) {
  init {
    require(
      (inset.value >= 0f || inset == Dp.Unspecified)
    ) {
      "Inset must be non-negative"
    }
  }

  override fun MeasureScope.measure(
    measurable: Measurable,
    constraints: Constraints
  ): MeasureResult {

    val totalOffsetInPx = inset.roundToPx() * 2

    val placeable = measurable.measure(constraints.offset(-totalOffsetInPx, -totalOffsetInPx))

    val width = constraints.constrainWidth(placeable.width + totalOffsetInPx)
    val height = constraints.constrainHeight(placeable.height + totalOffsetInPx)
    return layout(width, height) {
      placeable.place(inset.roundToPx(), inset.roundToPx())
    }
  }

  override fun hashCode(): Int {
    var result = inset.hashCode()
    result = 31 * result + inset.hashCode()
    return result
  }

  override fun equals(other: Any?): Boolean {
    val otherModifier = other as? RectangleInsetModifier ?: return false
    return inset == otherModifier.inset
  }
}
