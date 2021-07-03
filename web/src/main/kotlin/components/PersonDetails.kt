package components

import com.surrus.common.remote.Assignment
import kotlinx.css.Align
import kotlinx.css.ObjectFit
import kotlinx.css.alignSelf
import kotlinx.css.borderRadius
import kotlinx.css.margin
import kotlinx.css.objectFit
import kotlinx.css.padding
import kotlinx.css.pct
import kotlinx.css.px
import react.RBuilder
import styled.css
import styled.styledDiv
import styled.styledImg

fun RBuilder.PersonDetails(person: Assignment) {
    styledDiv {
        css {
            padding(32.px)
        }
        styledImg(alt = person.name, src = person.personImageUrl) {
            attrs {
                width = "128"
            }
        }

        styledDiv {
            css {
                margin(top = 16.px)
                alignSelf = Align.center
            }
            Typography("h4", person.name)
        }

        styledDiv {
            css {
                margin(top = 8.px)
                alignSelf = Align.center
            }
            Typography("h6", person.craft)
        }

        styledDiv {
            css {
                margin(top = 24.px)
            }
            Typography("body1", person.personBio ?: "")
        }
    }
}
