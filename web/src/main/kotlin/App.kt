import com.surrus.common.remote.Assignment
import com.surrus.common.remote.IssPosition
import components.*
import components.materialui.AppBar
import components.materialui.Card
import components.materialui.Grid
import components.materialui.Toolbar
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.css.margin
import kotlinx.css.padding
import kotlinx.css.px
import react.*
import react.dom.*
import styled.css


@InternalCoroutinesApi
val App = functionalComponent<RProps> {
    val appDependencies = useContext(AppDependenciesContext)
    val repository = appDependencies.repository

    val (people, setPeople) = useState(emptyList<Assignment>())
    val (issPosition, setIssPosition) = useState(IssPosition(0.0, 0.0))
    val (selectedPerson, setSelectedPerson) = useState<Assignment?>(null)

    useEffectWithCleanup(dependencies = listOf()) {
        val mainScope = MainScope()

        mainScope.launch {
            val people = repository.fetchPeople()
            setPeople(people)
            setSelectedPerson(people.first())

            repository.pollISSPosition().collect {
                setIssPosition(it)
            }
        }
        return@useEffectWithCleanup { mainScope.cancel() }
    }
    Fragment {
        AppBar {
            css {
                margin(0.px)
            }
            Toolbar {
                Typography("h6", "People In Space")
            }
        }

        Toolbar {
            // Empty toolbar to avoid below content to be overlapped by AppBar
        }

        Grid {
            attrs {
                container = true
                spacing = 4
                justify = "flex-start"
                alignItems = "stretch"
            }

            Grid {
                attrs {
                    item = true
                    md = 4
                    xs = 12
                }
                PeopleList(
                    people = people,
                    selectedPerson = selectedPerson,
                    onSelect = {
                        setSelectedPerson(it)
                    }
                )
            }
            Grid {
                attrs {
                    item = true
                    md = 8
                    xs = 12
                }

                selectedPerson?.let { person ->
                    Card {
                        css {
                            padding(16.px)
                        }

                        PersonDetails(person)
                    }
                }
            }
        }

        if (issPosition.latitude != 0.0 && issPosition.longitude != 0.0) {
            Space(16.px)
            Typography("subtitle1", buildString {
                append("ISS Position: ")
                append("Latitude = ${issPosition.latitude}, ")
                append("Longitude = ${issPosition.longitude}")
            })
            Space(4.px)
            IssLocationMap(issPosition)
        }
    }
}