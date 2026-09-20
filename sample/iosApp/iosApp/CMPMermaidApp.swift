import CmpMermaidDebugUi
import SwiftUI
import UIKit

@main
struct CMPMermaidApp: App {
    var body: some Scene {
        WindowGroup {
            ComposeView()
                .ignoresSafeArea(.keyboard)
        }
    }
}

private struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        if ProcessInfo.processInfo.arguments.contains("--load-test") {
            return IosDebugUiKt.MermaidLoadTestViewController()
        }
        return IosDebugUiKt.MermaidDebugViewController()
    }

    func updateUIViewController(
        _ uiViewController: UIViewController,
        context: Context
    ) {
    }
}
