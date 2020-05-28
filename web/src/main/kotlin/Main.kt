import com.surrus.common.di.initKoin
import com.surrus.common.repository.PeopleInSpaceRepository
import react.child
import react.createContext
import react.dom.render
import kotlin.browser.document

object AppDependencies {
    val repository = PeopleInSpaceRepository()
}

val AppDependenciesContext = createContext<AppDependencies>()


fun main() {
    initKoin()
    render(document.getElementById("root")) {
        AppDependenciesContext.Provider(AppDependencies) {
            child(functionalComponent = App)
        }
    }
}