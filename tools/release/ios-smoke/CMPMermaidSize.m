#import <CMPMermaid/CMPMermaid.h>

// Keep the same production entry point reachable in each linked size probe.
// This executable is linked for measurement; it is not an application to run.
int main(void) {
    @autoreleasepool {
        CMPMCMPMermaidViewControllerFactory *factory = [CMPMCMPMermaidViewControllerFactory new];
        UIViewController *controller = [factory makeViewControllerSource:@"flowchart LR\nA --> B"
                                                   contentDescription:@"Mermaid size probe"];
        return controller == nil;
    }
}
