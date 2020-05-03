import com.surrus.common.remote.PeopleInSpaceApi
import react.child
import react.createContext
import react.dom.render
import kotlin.browser.document

object AppDependencies {
    val peopleInSpaceApi = PeopleInSpaceApi()
}

val AppDependenciesContext = createContext<AppDependencies>()


fun main() {
    render(document.getElementById("root")) {
        AppDependenciesContext.Provider(AppDependencies) {
            child(functionalComponent = App)
        }
    }
}