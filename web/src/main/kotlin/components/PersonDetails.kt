package components

import kotlinx.css.Align
import kotlinx.css.ObjectFit
import kotlinx.css.alignSelf
import kotlinx.css.borderRadius
import kotlinx.css.margin
import kotlinx.css.objectFit
import kotlinx.css.padding
import kotlinx.css.pct
import kotlinx.css.px
import model.Person
import react.RBuilder
import styled.css
import styled.styledDiv
import styled.styledImg

fun RBuilder.PersonDetails(person: Person) {
    styledDiv {
        css {
            padding(32.px)
        }
        styledImg(alt = person.assignment.name, src = person.imageUrl) {
            css {
                objectFit = ObjectFit.cover
                borderRadius = 50.pct
            }
            attrs {
                height = "128"
                width = "128"
            }
        }

        styledDiv {
            css {
                margin(top = 16.px)
                alignSelf = Align.center
            }
            Typography("h4", person.assignment.name)
        }

        styledDiv {
            css {
                margin(top = 8.px)
                alignSelf = Align.center
            }
            Typography("h6", person.assignment.craft)
        }

        styledDiv {
            css {
                margin(top = 24.px)
            }
            Typography("body1", person.bio)
        }
    }
}
