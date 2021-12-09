import com.surrus.common.di.initKoin
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import react.child
import react.createContext
import react.dom.render

object AppDependencies : KoinComponent {
    val repository: PeopleInSpaceRepositoryInterface

    init {
        initKoin()
        repository = get()
    }
}

val AppDependenciesContext = createContext<AppDependencies>()

@InternalCoroutinesApi
fun main() {
    render(kotlinx.browser.document.getElementById("root")) {
        AppDependenciesContext.Provider(AppDependencies) {
            child(App)
        }
    }
}