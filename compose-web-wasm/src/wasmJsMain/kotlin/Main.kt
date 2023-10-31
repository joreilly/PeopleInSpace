import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import androidx.compose.material3.Text

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow("PeopleInSpace", canvasElementId = "peopleInSpaceCanvas") {
        PeopleInSpaceScreen()
    }
}

