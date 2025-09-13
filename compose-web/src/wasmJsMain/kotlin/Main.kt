import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.CanvasBasedWindow
import coil3.compose.AsyncImage
import dev.johnoreilly.common.di.initKoin
import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.common.repository.PeopleInSpaceRepository

// Define colors for the application
private val primaryColor = Color(0xFF1E88E5) // Blue
private val secondaryColor = Color(0xFF26A69A) // Teal
private val backgroundColor = Color(0xFFF5F5F5) // Light Gray
private val cardColor = Color.White
private val selectedItemColor = Color(0xFFE3F2FD) // Light Blue

private val koin = initKoin(enableNetworkLogs = true).koin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {

    val peopleInSpaceRepository = koin.get<PeopleInSpaceRepository>()

    CanvasBasedWindow("PeopleInSpace", canvasElementId = "peopleInSpaceCanvas") {

        val people by peopleInSpaceRepository.fetchPeopleAsFlow().collectAsState(emptyList())
        var selectedPerson by remember { mutableStateOf<Assignment?>(null) }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = backgroundColor
        ) {
            Column(Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(primaryColor)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "People In Space",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Content
                Row(Modifier.fillMaxSize().padding(16.dp)) {
                    // Left panel with list of people
                    Card(
                        modifier = Modifier.width(280.dp).fillMaxHeight(),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(primaryColor.copy(alpha = 0.8f))
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Astronauts",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            PersonList(people, selectedPerson) {
                                selectedPerson = it
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Right panel with person details
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(Modifier.fillMaxSize()) {
                            selectedPerson?.let {
                                PersonDetailsView(it)
                            } ?: run {
                                // Show a message when no person is selected
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Select an astronaut to view details",
                                        style = TextStyle(
                                            color = Color.Gray,
                                            fontSize = 18.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PersonList(
    people: List<Assignment>,
    selectedPerson: Assignment?,
    personSelected: (person: Assignment) -> Unit
) {
    if (people.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(people) { person ->
                PersonView(person, selectedPerson, personSelected)
                
                // Add divider between items (except after the last item)
                if (person != people.last()) {
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.LightGray,
                        thickness = 0.5.dp
                    )
                }
            }
        }
    } else {
        // Show loading state
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Loading astronauts...",
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            )
        }
    }
}


@Composable
fun PersonView(
    person: Assignment,
    selectedPerson: Assignment?,
    personSelected: (person: Assignment) -> Unit
) {
    val isSelected = person.name == selectedPerson?.name
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) selectedItemColor else Color.Transparent)
            .clickable(onClick = { personSelected(person) })
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Person image or placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isSelected) primaryColor else secondaryColor)
                .border(1.dp, Color.LightGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            person.personImageUrl?.let {
                AsyncImage(
                    model = it,
                    contentDescription = person.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                )
            } ?: Text(
                text = person.name.first().toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                person.name,
                style = TextStyle(
                    fontSize = if (isSelected) 18.sp else 16.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = person.craft,
                    style = TextStyle(
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )
                )
            }

            if (person.nationality.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = person.nationality,
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

@Composable
fun PersonDetailsView(person: Assignment) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item(person) {
            // Header with name and craft
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    person.name,
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        textAlign = TextAlign.Center
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "Currently aboard: ${person.craft}",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = secondaryColor,
                        textAlign = TextAlign.Center
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Profile image
            val personImageUrl = person.personImageUrl
            personImageUrl?.let {
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(CircleShape)
                        .border(4.dp, primaryColor.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        modifier = Modifier.size(240.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        model = personImageUrl,
                        contentDescription = person.name
                    )
                }
            } ?: Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.2f))
                    .border(4.dp, primaryColor.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = person.name.first().toString(),
                    style = TextStyle(
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // Nationality section (if available)
            if (person.nationality.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF9F9F9)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Nationality",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Divider(color = Color.LightGray)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            person.nationality,
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.DarkGray
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bio section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF9F9F9)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Biography",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Divider(color = Color.LightGray)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val bio = person.personBio ?: "No biography available for ${person.name}."
                    Text(
                        bio,
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            color = Color.DarkGray
                        )
                    )
                }
            }
        }
    }
}
