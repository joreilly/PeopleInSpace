import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import coil3.compose.AsyncImage
import dev.johnoreilly.common.di.initKoin
import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.common.repository.PeopleInSpaceRepository

// Define custom colors for the app
private val SpaceBlue = Color(0xFF1E88E5)
private val SpaceDarkBlue = Color(0xFF0D47A1)
private val SpaceLightBlue = Color(0xFFBBDEFB)
private val SpaceGray = Color(0xFF607D8B)
private val SpaceLightGray = Color(0xFFECEFF1)

// Custom theme for the app
@Composable
fun PeopleInSpaceTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = lightColors(
            primary = SpaceBlue,
            primaryVariant = SpaceDarkBlue,
            secondary = SpaceGray,
            background = Color.White,
            surface = Color.White
        ),
        typography = Typography(
            h4 = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                letterSpacing = 0.25.sp
            ),
            h6 = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = 0.15.sp
            ),
            body1 = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                letterSpacing = 0.5.sp
            ),
            body2 = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                letterSpacing = 0.25.sp
            )
        ),
        content = content
    )
}

private val koin = initKoin(enableNetworkLogs = true).koin

fun main() = application {
    val windowState = rememberWindowState(width = 1000.dp, height = 700.dp)

    var selectedPerson by remember { mutableStateOf<Assignment?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val peopleInSpaceRepository = koin.get<PeopleInSpaceRepository>()
    val people by peopleInSpaceRepository.fetchPeopleAsFlow().collectAsState(emptyList())
    
    // Update loading state when people list changes
    LaunchedEffect(people) {
        isLoading = people.isEmpty()
    }

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "People In Space"
    ) {
        PeopleInSpaceTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // App header
                    Surface(
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.fillMaxWidth().height(60.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "People In Space",
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            
                            Spacer(Modifier.weight(1f))
                        }
                    }
                    
                    // Main content
                    Row(Modifier.fillMaxSize()) {
                        // People list panel
                        Card(
                            modifier = Modifier.width(280.dp).fillMaxHeight().padding(8.dp),
                            elevation = 4.dp
                        ) {
                            Box(Modifier.fillMaxSize()) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(40.dp).align(Alignment.Center)
                                    )
                                } else {
                                    PersonList(people, selectedPerson) {
                                        selectedPerson = it
                                    }
                                }
                            }
                        }

                        // Details panel
                        Box(
                            Modifier.weight(1f).fillMaxHeight().padding(8.dp)
                        ) {
                            if (selectedPerson != null) {
                                PersonDetailsView(selectedPerson!!)
                            } else {
                                // Show placeholder when no person is selected
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Select a person to view details",
                                        style = MaterialTheme.typography.h6.copy(color = SpaceGray)
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

    // workaround for compose desktop but if LazyColumn is empty
    if (people.isNotEmpty()) {
        LazyColumn {
            items(people) { person ->
                PersonView(person, selectedPerson, personSelected)
            }
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
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = { personSelected(person) }),
        color = if (isSelected) SpaceLightBlue else MaterialTheme.colors.surface,
        elevation = if (isSelected) 4.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar placeholder
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = if (isSelected) SpaceBlue else SpaceGray
            ) {
                Box(contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = person.personImageUrl,
                        contentDescription = person.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )

                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = person.name,
                    style = if (isSelected) 
                        MaterialTheme.typography.h6.copy(color = SpaceDarkBlue) 
                    else 
                        MaterialTheme.typography.body1,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = "Spacecraft: ${person.craft}",
                    style = MaterialTheme.typography.body2.copy(
                        color = if (isSelected) SpaceDarkBlue.copy(alpha = 0.7f) else SpaceGray
                    )
                )

                val nationality = person.nationality
                if (nationality.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Nationality: $nationality",
                        style = MaterialTheme.typography.body2.copy(
                            color = if (isSelected) SpaceDarkBlue.copy(alpha = 0.7f) else SpaceGray
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun PersonDetailsView(person: Assignment) {
    Card(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item(person) {
                // Header with name and spacecraft
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        person.name,
                        style = MaterialTheme.typography.h4.copy(
                            color = SpaceDarkBlue,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Surface(
                        color = SpaceLightBlue,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            "Aboard ${person.craft}",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = TextStyle(
                                color = SpaceDarkBlue,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        )
                    }

                    val nationality = person.nationality
                    if (nationality.isNotBlank()) {
                        Surface(
                            color = SpaceLightBlue,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                "Nationality: $nationality",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                style = TextStyle(
                                    color = SpaceDarkBlue,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Astronaut image with border
                val personImageUrl = person.personImageUrl
                if (!personImageUrl.isNullOrEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(280.dp),
                        elevation = 4.dp
                    ) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                            model = personImageUrl,
                            contentDescription = person.name
                        )
                    }
                } else {
                    // Placeholder if no image
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(280.dp),
                        color = SpaceLightGray
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                person.name.first().toString(),
                                style = TextStyle(
                                    color = SpaceBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 80.sp
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Biography section
                val bio = person.personBio ?: "No biography available for this astronaut."
                if (bio.isNotEmpty()) {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Biography",
                                style = TextStyle(
                                    color = SpaceDarkBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                bio,
                                style = MaterialTheme.typography.body1.copy(
                                    lineHeight = 24.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
