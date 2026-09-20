import CMPMermaid
import UIKit

func makeMermaidViewController() -> UIViewController {
    CMPMermaidViewControllerFactory().makeViewController(
        source: """
        flowchart LR
            Source --> Consumer
        """,
        contentDescription: "Mermaid release smoke test"
    )
}
