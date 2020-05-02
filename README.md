# PeopleInSpace

Minimal **Kotlin Multiplatform** project using Jetpack Compose and SwiftUI.  Currently running on
* Android
* iOS
* watchOS
* macOS
* Web

It makes use of basic API (http://open-notify.org/Open-Notify-API/People-In-Space/) to show list of people currently in
space (inspired by https://kousenit.org/2019/12/19/a-few-astronomical-examples-in-kotlin/)!  The list is shown on Android
using **Jetpack Compose** and on iOS using **SwiftUI**

Related posts:
* [Minimal Kotlin Multiplatform project using Compose and SwiftUI](https://johnoreilly.dev/posts/minimal-kotlin-platform-compose-swiftui/)
* [Adding some Storage (to) Space](https://johnoreilly.dev/posts/adding-sqldelight-to-peopleinspace/)
* [Kotlin Multiplatform running on macOS](https://johnoreilly.dev/posts/kotlinmultiplatform-macos/)
* [PeopleInSpace hits the web with Kotlin/JS and React](https://johnoreilly.dev/posts/peopleinspace-kotlinjs/)


**Note**: You need to use Android Studio v4.1 (currently on Canary 8).  Have tested on XCode v11.3


**Update Jan 14th 2020**: This now also includes WatchOS version thanks to [Neal Sanche](https://github.com/nealsanche)

The following is pretty much all the code used (along with gradle files/resources etc).  I did say it was *minimal*!!

**Update Jan 25th 2020**: Have added SQLDelight support for locally persisting data (across all the platforms).
I haven't updated code below yet as I think it still has value in demonstrating what a minimum Kotlin
Multiplatform project would be. 

**Update April 15th 2020**: Added macOS support

**Update May 2nd 2020**: Added basic Kotlin/JS support

### iOS SwiftUI Code

```swift
struct ContentView: View {
    @ObservedObject var peopleInSpaceViewModel = PeopleInSpaceViewModel(repository: PeopleInSpaceRepository())

    var body: some View {
        NavigationView {
            List(peopleInSpaceViewModel.people, id: \.name) { person in
                PersonView(person: person)
            }
            .navigationBarTitle(Text("PeopleInSpace"), displayMode: .large)
            .onAppear(perform: {
                self.peopleInSpaceViewModel.fetch()
            })
        }
    }
}

struct PersonView : View {
    var person: Assignment

    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(person.name).font(.headline)
                Text(person.craft).font(.subheadline)
            }
        }
    }
```


### WatchOS SwiftUI Code

```swift
struct ContentView: View {
    @ObservedObject var peopleInSpaceViewModel = PeopleInSpaceViewModel(repository: PeopleInSpaceRepository())
    
    var body: some View {
        VStack {
            List(peopleInSpaceViewModel.people, id: \.name) { person in
                PersonView(person: person)
            }
            .onAppear(perform: {
                self.peopleInSpaceViewModel.fetch()
            })
        }
    }
}

struct PersonView : View {
    var person: Assignment
    
    var body: some View {
        NavigationLink(person.name, destination: Text(person.craft).font(.subheadline))
    }
}
```



### iOS Swift ViewModel

```swift
class PeopleInSpaceViewModel: ObservableObject {
    @Published var people = [Assignment]()

    private let repository: PeopleInSpaceRepository
    init(repository: PeopleInSpaceRepository) {
        self.repository = repository
    }
    
    func fetch() {
        repository.fetchPeople(success: { data in
            self.people = data
        })
    }
}
```



### Android Jetpack Compose code

```kotlin
class MainActivity : AppCompatActivity() {
    private val peopleInSpaceViewModel: PeopleInSpaceViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val peopleState = peopleInSpaceViewModel.peopleInSpace.observeAsState()
            mainLayout(peopleState)
        }
    }
}

@Composable
fun mainLayout(peopleState: State<List<Assignment>?>) {
    MaterialTheme {
        Column {
            TopAppBar(
                title = {
                    Text("People In Space")
                }
            )
            AdapterList(data = peopleState.value!!) { person ->
                Row(person)
            }

        }
    }
}


@Composable
fun Row(person: Assignment) {
    Text(
        text = "${person.name} (${person.craft})",
        modifier = Modifier.padding(16.dp)
    )
}
```

### Android Kotlin ViewModel

```kotlin
class PeopleInSpaceViewModel(peopleInSpaceRepository: PeopleInSpaceRepository) : ViewModel() {
    val peopleInSpace = MutableLiveData<List<Assignment>>(emptyList())

    init {
        viewModelScope.launch {
            val people = peopleInSpaceRepository.fetchPeople()
            peopleInSpace.value = people
        }
    }
}
```

### Kotlin/JS client

```kotlin
val App = functionalComponent<RProps> { _ ->
    val scope = MainScope()
    val api = PeopleInSpaceApi()

    val (people, setPeople) = useState(emptyList<Assignment>())

    useEffect(dependencies = listOf()) {
        scope.launch {
            setPeople(api.fetchPeople().people)
        }
    }

    h1 {
        +"People In Space"
    }
    ul {
        people.forEach { item ->
            li {
                +"${item.name} (${item.craft})"
            }
        }
    }
}
```

### Shared Kotlin Repository

```kotlin
class PeopleInSpaceRepository {
    private val peopleInSpaceApi = PeopleInSpaceApi()

    suspend fun fetchPeople() : List<Assignment> {
        val result = peopleInSpaceApi.fetchPeople()
        return result.people
    }


    fun fetchPeople(success: (List<Assignment>) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            success(fetchPeople())
        }
    }
}
```


### Shared Kotlin API Client Code (using **Ktor** and **Kotlinx Serialization** library)

```kotlin
@Serializable
data class AstroResult(val message: String, val number: Int, val people: List<Assignment>)

@Serializable
data class Assignment(val craft: String, val name: String)

class PeopleInSpaceApi {
    private val baseUrl = "http://api.open-notify.org"

    private val client by lazy {
        HttpClient() {
            install(JsonFeature) {
                serializer = KotlinxSerializer(Json(JsonConfiguration(strictMode = false)))
            }
        }
    }

    suspend fun fetchPeople() = client.get<AstroResult>("$baseUrl/astros.json")
}
```




### Languages, libraries and tools used

* [Kotlin](https://kotlinlang.org/)
* [Kotlin Corooutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)
* [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization)
* [Ktor client library](https://github.com/ktorio/ktor)
* [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/index.html)
* [Koin](https://github.com/InsertKoinIO/koin)
* [Jetpack Compose](https://developer.android.com/jetpack/compose)
* [SwiftUI](https://developer.apple.com/documentation/swiftui)