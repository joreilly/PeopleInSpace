package components

import kotlinx.css.LinearDimension
import kotlinx.css.margin
import react.RBuilder
import styled.css
import styled.styledDiv

fun RBuilder.Space(space: LinearDimension) = styledDiv { css { margin(space) } }