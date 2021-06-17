import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.renderComposable
import org.jetbrains.compose.web.dom.*
import com.surrus.common.di.initKoin
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.IssPosition
import com.surrus.common.repository.PeopleInSpaceRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.common.foundation.layout.Row
import org.jetbrains.compose.common.ui.Alignment
import org.jetbrains.compose.common.ui.Modifier


private val koin = initKoin(enableNetworkLogs = true).koin

@InternalCoroutinesApi
fun main() {
    val repo = koin.get<PeopleInSpaceRepository>()

    renderComposable(rootElementId = "root") {
        Style(TextStyles)

        var people by remember { mutableStateOf(emptyList<Assignment>()) }

        LaunchedEffect(true) {
            people = repo.fetchPeople()
        }

        val issPosition by produceState(initialValue = IssPosition(0.0, 0.0), repo) {
            repo.pollISSPosition().collect { value = it }
        }


        Div(attrs = { style { padding(16.px) } }) {
            Column {
                H1(attrs = { classes(TextStyles.titleText) }) {
                    Text("People In Space")
                }
                H2 {
                    Text("ISS Position: latitude = ${issPosition.latitude}, longitude = ${issPosition.longitude}")
                }

                people.forEach { person ->
                    Div(attrs = { style {
                        display(DisplayStyle.Flex)
                        alignItems(AlignItems.Center)
                    }}) {

                        val imageUrl = repo.getPersonImage(person.name)
                        Img(src = imageUrl, attrs = { style {
                            width(48.px)
                            property("padding-right", 16.px)
                        }})

                        Span(attrs = { classes(TextStyles.personText) }) {
                            Text("${person.name} (${person.craft})")
                        }
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
        property("font-size", 50.px)
        property("letter-spacing", (-1.5).px)
        property("font-weight", 900)
        property("line-height", 58.px)

        media(maxWidth(640.px)) {
            self style {
                42.px
                48.px
            }
        }

        property("font-family",
                "Gotham SSm A,Gotham SSm B,system-ui,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Oxygen,Ubuntu,Cantarell,Droid Sans,Helvetica Neue,Arial,sans-serif")
    }

    val personText by style {
        color("#27282c")
        fontSize(24.px)
        property("font-size", 28.px)
        property("letter-spacing", "normal")
        property("font-weight", 300)
        property("line-height", 40.px)

        media(maxWidth(640.px)) {
            self style {
                24.px
                32.px
            }
        }

        property("font-family",
                "Gotham SSm A,Gotham SSm B,system-ui,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Oxygen,Ubuntu,Cantarell,Droid Sans,Helvetica Neue,Arial,sans-serif")
    }
}

