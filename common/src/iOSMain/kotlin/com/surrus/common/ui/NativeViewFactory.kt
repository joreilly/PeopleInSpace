package dev.johnoreilly.common.ui

import dev.johnoreilly.common.viewmodel.ISSPositionViewModel
import platform.UIKit.UIViewController

// Internal: Swift Export can't pass a Swift implementation of a Kotlin interface (or a Swift
// subclass of an exported abstract class) back to Kotlin -- the bridge's cast fails with a
// ClassCastException at runtime (Kotlin 2.5.0-Beta1). Swift supplies a closure instead, see
// ISSPositionContentViewController.
internal fun interface NativeViewFactory {
    fun createISSMapView(viewModel: ISSPositionViewModel): UIViewController
}
