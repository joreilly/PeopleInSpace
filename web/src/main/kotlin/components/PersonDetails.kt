package components

import kotlinx.css.Align
import kotlinx.css.alignSelf
import kotlinx.css.margin
import kotlinx.css.padding
import kotlinx.css.px
import model.Person
import react.RBuilder
import react.dom.img
import styled.css
import styled.styledDiv

fun RBuilder.PersonDetails(person: Person) {
    styledDiv {
        css {
            padding(32.px)
        }

        img(alt = person.assignment.name, src = person.imageUrl) {
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
