import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.*
import platform.UIKit.*
import platform.Foundation.*
import com.surrus.common.di.initKoin
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface

private val koin = initKoin(enableNetworkLogs = true).koin

@OptIn(ExperimentalForeignApi::class)
fun main() {
    val args = emptyArray<String>()
    memScoped {
        val argc = args.size + 1
        val argv = (arrayOf("skikoApp") + args).map { it.cstr.ptr }.toCValues()
        autoreleasepool {
            UIApplicationMain(argc, argv, null, NSStringFromClass(SkikoAppDelegate))
        }
    }
}

class SkikoAppDelegate : UIResponder, UIApplicationDelegateProtocol {
    companion object : UIResponderMeta(), UIApplicationDelegateProtocolMeta

    @ObjCObjectBase.OverrideInit
    constructor() : super()

    private var _window: UIWindow? = null
    override fun window() = _window
    override fun setWindow(window: UIWindow?) {
        _window = window
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun application(application: UIApplication, didFinishLaunchingWithOptions: Map<Any?, *>?): Boolean {
        val repo = koin.get<PeopleInSpaceRepositoryInterface>()

        window = UIWindow(frame = UIScreen.mainScreen.bounds).apply {
            rootViewController = ComposeUIViewController {
                Column {
                    // To skip upper part of screen.
                    Box(modifier = Modifier.height(48.dp))
                    PersonListScreen(repo)
                }
            }
            makeKeyAndVisible()
        }
        return true
    }
}

