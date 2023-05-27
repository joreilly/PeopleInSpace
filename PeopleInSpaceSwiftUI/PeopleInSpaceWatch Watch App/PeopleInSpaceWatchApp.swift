import SwiftUI
import common

@main
struct PeopleInSpaceWatch_Watch_AppApp: App {
    init() {
        KoinKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
