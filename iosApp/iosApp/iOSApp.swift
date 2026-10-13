import SwiftUI
import SharedLogic

@main
struct iOSApp: App {
    init() {
        InitKoinKt.doInitKoin(additionalModules: [], appDeclaration: nil)
    }

    var body: some Scene {
        WindowGroup {
            RootTabView()
        }
    }
}
