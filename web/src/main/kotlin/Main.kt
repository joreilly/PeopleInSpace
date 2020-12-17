import co.touchlab.kermit.Kermit
import com.surrus.common.di.initKoin
import com.surrus.common.repository.PeopleInSpaceRepository
import org.koin.core.KoinComponent
import org.koin.core.get
import react.child
import react.createContext
import react.dom.render

object AppDependencies : KoinComponent {
    val repository: PeopleInSpaceRepository
    val logger: Kermit

    init {
        initKoin()
        repository = get()
        logger = get()
    }
}

val AppDependenciesContext = createContext<AppDependencies>()


fun main() {
    render(kotlinx.browser.document.getElementById("root")) {
        AppDependenciesContext.Provider(AppDependencies) {
            child(App)
        }
    }
}