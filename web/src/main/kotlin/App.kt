import com.surrus.common.remote.Assignment
import com.surrus.common.remote.PeopleInSpaceApi
import react.*
import react.dom.*
import kotlinx.coroutines.*


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
