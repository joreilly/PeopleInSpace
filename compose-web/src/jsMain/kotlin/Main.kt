import androidx.compose.runtime.*
import com.chihsuanwu.maps.compose.web.*
import com.chihsuanwu.maps.compose.web.drawing.Marker
import com.chihsuanwu.maps.compose.web.drawing.rememberMarkerState
import com.surrus.common.di.initKoin
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.IssPosition
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.InternalCoroutinesApi
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable

private val koin = initKoin(enableNetworkLogs = true).koin

@InternalCoroutinesApi
fun main() {
    val repo = koin.get<PeopleInSpaceRepositoryInterface>()

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
            H1(attrs = { classes(TextStyles.titleText) }) {
                Text("People In Space")
            }
            H2 {
                Text("ISS Position: latitude = ${issPosition.latitude}, longitude = ${issPosition.longitude}")
            }

            Div(attrs = {
                style {
                    display(DisplayStyle.Flex)
                    flexDirection(FlexDirection.Row)
                }
            }) {
                Div {
                    people.forEach { person ->
                        Div(
                            attrs = {
                                style {
                                    display(DisplayStyle.Flex)
                                    alignItems(AlignItems.Center)
                                }
                            }
                        ) {

                            val imageUrl = person.personImageUrl ?: ""
                            Img(
                                src = imageUrl,
                                attrs = {
                                    style {
                                        width(48.px)
                                        property("padding-right", 16.px)
                                    }
                                }
                            )

                            Span(attrs = { classes(TextStyles.personText) }) {
                                Text("${person.name} (${person.craft})")
                            }
                        }
                    }
                }

                MapContainer(issPosition)
            }
        }
    }
}

@Composable
private fun MapContainer(issPosition: IssPosition) {
    var apiKey: String by remember { mutableStateOf("") }
    var setApiKeyClicked by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(
            center = LatLng(
                issPosition.latitude,
                issPosition.longitude
            ),
            zoom = 4.0
        )
    }

    val markerState = rememberMarkerState(
        position = LatLng(
            issPosition.latitude,
            issPosition.longitude
        )
    )

    LaunchedEffect(issPosition) {
        val position = LatLng(
            issPosition.latitude,
            issPosition.longitude
        )
        markerState.position = position
        cameraPositionState.position = cameraPositionState.position.copy(center = position)
    }

    Div(attrs = {
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            flex(1)
            marginLeft(20.px)
        }
    }) {
        Div(
            attrs = {
                style {
                    margin(10.px)
                }
            }
        ) {
            Input(
                type = InputType.Text,
                attrs = {
                    attr("placeholder", "Enter your API key here")
                    value(apiKey)
                    onInput { event ->
                        apiKey = event.value
                    }
                    style {
                        display(DisplayStyle.Inline)
                        padding(4.px)
                    }
                }
            )

            Button(
                attrs = {
                    style {
                        margin(10.px)
                    }
                    onClick {
                        setApiKeyClicked = true
                    }
                }
            ) {
                Text("Set API Key")
            }
        }

        if (setApiKeyClicked) {
            GoogleMap(
                apiKey = apiKey,
                cameraPositionState = cameraPositionState,
                attrs = {
                    style {
                        width(100.percent)
                        flex(1)
                    }
                }
            ) {
                Marker(
                    state = markerState,
                )
            }
        }
    }
}

object TextStyles : StyleSheet() {

    val titleText by style {
        color(rgb(23,24, 28))
        fontSize(50.px)
        property("font-size", 50.px)
        property("letter-spacing", (-1.5).px)
        property("font-weight", 900)
        property("line-height", 58.px)

        property(
            "font-family",
            "Gotham SSm A,Gotham SSm B,system-ui,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Oxygen,Ubuntu,Cantarell,Droid Sans,Helvetica Neue,Arial,sans-serif"
        )
    }

    val personText by style {
        color(rgb(23,24, 28))
        fontSize(24.px)
        property("font-size", 28.px)
        property("letter-spacing", "normal")
        property("font-weight", 300)
        property("line-height", 40.px)

        property(
            "font-family",
            "Gotham SSm A,Gotham SSm B,system-ui,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Oxygen,Ubuntu,Cantarell,Droid Sans,Helvetica Neue,Arial,sans-serif"
        )
    }
}
