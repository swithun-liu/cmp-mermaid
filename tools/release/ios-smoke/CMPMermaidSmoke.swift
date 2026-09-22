import CMPMermaid
import UIKit

func makeMermaidViewController() -> UIViewController {
    CMPMermaidViewControllerFactory().makeViewController(
        source: """
        flowchart LR
            Source --> Consumer
        """,
        contentDescription: "Mermaid release smoke test",
        onContentSizeChanged: nil,
        onError: { error in
            _ = error.type
            _ = error.message
            _ = error.source
        }
    )
}
