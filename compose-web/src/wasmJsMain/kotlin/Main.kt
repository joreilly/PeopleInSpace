import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow("PeopleInSpace", canvasElementId = "peopleInSpaceCanvas") {

        Column {
            Text(
                text ="PeopleInSpace",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Gray)
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp),
                textAlign = TextAlign.Center,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall)

            PeopleInSpaceScreen()
        }
    }
}

