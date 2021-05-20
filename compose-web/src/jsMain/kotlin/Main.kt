import androidx.compose.runtime.*
import androidx.compose.web.css.*
import androidx.compose.web.renderComposable
import androidx.compose.web.elements.*
import com.surrus.common.di.initKoin
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.IssPosition
import com.surrus.common.repository.PeopleInSpaceRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.common.foundation.layout.Row


private val koin = initKoin(enableNetworkLogs = true).koin

@InternalCoroutinesApi
fun main() {
    val repo = koin.get<PeopleInSpaceRepository>()

    renderComposable(rootElementId = "root") {
        var people by remember { mutableStateOf(emptyList<Assignment>()) }

        LaunchedEffect(true) {
            people = repo.fetchPeople()
        }

        val issPosition by produceState(initialValue = IssPosition(0.0, 0.0), repo) {
            repo.pollISSPosition().collect { value = it }
        }


        Div(style = { padding(16.px) }) {
            Column {
                H1(attrs = { classes(TextStyles.titleText) }) {
                    Text("People In Space")
                }
                H2 {
                    Text("ISS Position: latitude = ${issPosition.latitude}, longitude = ${issPosition.longitude}")
                }

                people.forEach { person ->
                    Row {
                        val imageUrl = repo.getPersonImage(person.name)
                        Img(src = imageUrl, style = {
                            width(48.px)
                            property("padding-right", value(16.px))
                        })

                        Text("${person.name} (${person.craft})")
                    }
                }
            }
        }
    }
}

object TextStyles : StyleSheet() {

    val titleText by style {
        color("#27282c")
        fontSize(50.px)
        property("font-size", AppCSSVariables.wtHeroFontSize.value(50.px))
        property("letter-spacing", value((-1.5).px))
        property("font-weight", value(900))
        property("line-height", value(58.px))
        property("line-height", AppCSSVariables.wtHeroLineHeight.value(64.px))

        media(maxWidth(640.px)) {
            self style {
                AppCSSVariables.wtHeroFontSize(42.px)
                AppCSSVariables.wtHeroLineHeight(48.px)
            }
        }

        property(
                "font-family",
                value("Gotham SSm A,Gotham SSm B,system-ui,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Oxygen,Ubuntu,Cantarell,Droid Sans,Helvetica Neue,Arial,sans-serif")
        )
    }

    val personText by style {
        color("#27282c")
        fontSize(28.px)
        property("font-size", AppCSSVariables.wtSubtitle2FontSize.value(28.px))
        property("letter-spacing", value("normal"))
        property("font-weight", value(300))
        property("line-height", value(40.px))
        property("line-height", AppCSSVariables.wtSubtitle2LineHeight.value(40.px))

        media(maxWidth(640.px)) {
            self style {
                AppCSSVariables.wtSubtitle2FontSize(24.px)
                AppCSSVariables.wtSubtitle2LineHeight(32.px)
            }
        }

        property(
                "font-family",
                value("Gotham SSm A,Gotham SSm B,system-ui,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Oxygen,Ubuntu,Cantarell,Droid Sans,Helvetica Neue,Arial,sans-serif")
        )
    }
}

object AppCSSVariables : CSSVariables {
    val wtColorGreyLight by variable<Color>()
    val wtColorGreyDark by variable<Color>()

    val wtOffsetTopUnit by variable<CSSSizeValue>()
    val wtHorizontalLayoutGutter by variable<CSSSizeValue>()
    val wtFlowUnit by variable<CSSSizeValue>()

    val wtHeroFontSize by variable<CSSSizeValue>()
    val wtHeroLineHeight by variable<CSSSizeValue>()
    val wtSubtitle2FontSize by variable<CSSSizeValue>()
    val wtSubtitle2LineHeight by variable<CSSSizeValue>()
    val wtH2FontSize by variable<CSSSizeValue>()
    val wtH2LineHeight by variable<CSSSizeValue>()
    val wtH3FontSize by variable<CSSSizeValue>()
    val wtH3LineHeight by variable<CSSSizeValue>()

    val wtColCount by variable<Int>()
}
