package components

import react.RBuilder

fun RBuilder.Typography(variant: String, text: String) = components.materialui.Typography {
    attrs.variant = variant
    +text
}