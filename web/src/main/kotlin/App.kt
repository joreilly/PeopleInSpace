import com.surrus.common.remote.Assignment
import com.surrus.common.remote.IssPosition
import react.*
import react.dom.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect


@InternalCoroutinesApi
val App = functionalComponent<RProps> {
    val appDependencies = useContext(AppDependenciesContext)
    val repository = appDependencies.repository

    val (people, setPeople) = useState(emptyList<Assignment>())
    val (issPosition, setIssPosition) = useState(IssPosition(0.0,0.0))

    useEffectWithCleanup(dependencies = listOf()) {
        val mainScope = MainScope()

        mainScope.launch {
            setPeople(repository.fetchPeople())

            repository.pollISSPosition().collect {
                setIssPosition(it)
            }
        }
        return@useEffectWithCleanup { mainScope.cancel() }
    }

    h1 {
        +"People In Space"
    }
    h2 {
        +"$issPosition"
    }
    ul {
        people.forEach { item ->
            li {
                +"${item.name} (${item.craft})"
            }
        }
    }

}
