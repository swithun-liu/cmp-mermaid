package com.swithun.cmpmermaid.debugui

internal data class GitGraphDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val gitGraphDemos = listOf(
    GitGraphDemo(
        id = "gitgraph_official_basic",
        title = "Basic branching workflow",
        category = "Syntax",
        source = """
            gitGraph
                commit id: "Initial"
                commit id: "Baseline"
                branch develop
                commit id: "Feature A"
                commit id: "Feature B"
                checkout main
                merge develop id: "Release"
                commit id: "Patch"
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_commit_types",
        title = "Commit symbols",
        category = "Commits",
        source = """
            gitGraph
                commit id: "Normal"
                commit id: "Rollback" type: REVERSE
                commit id: "Milestone" type: HIGHLIGHT
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_tags",
        title = "Release tags",
        category = "Commits",
        source = """
            gitGraph
                commit id: "alpha" tag: "v1.0.0-alpha"
                commit id: "rc" tag: "v1.0.0-rc.1"
                commit id: "stable" type: HIGHLIGHT tag: "v1.0.0"
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_switch",
        title = "Checkout and switch",
        category = "Branches",
        source = """
            gitGraph
                commit id: "root"
                branch feature
                commit id: "feature-1"
                switch main
                commit id: "main-1"
                checkout feature
                commit id: "feature-2"
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_custom_merge",
        title = "Customized merge commit",
        category = "Merge",
        source = """
            gitGraph
                commit id: "root"
                branch feature
                commit id: "feature-1"
                commit id: "feature-2"
                checkout main
                commit id: "main-1"
                merge feature id: "merge-feature" tag: "reviewed" type: REVERSE
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_nested_branches",
        title = "Nested feature branches",
        category = "Merge",
        source = """
            gitGraph
                commit id: "1"
                commit id: "2"
                branch feature
                commit id: "3"
                branch experiment
                commit id: "4" type: HIGHLIGHT
                checkout feature
                commit id: "5"
                merge experiment id: "6"
                checkout main
                commit id: "7"
                merge feature id: "8" tag: "release"
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_cherry_pick",
        title = "Cherry-pick",
        category = "Cherry-pick",
        source = """
            gitGraph
                commit id: "root"
                branch feature
                commit id: "feature-fix"
                checkout main
                commit id: "main-work"
                cherry-pick id: "feature-fix"
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_cherry_pick_merge",
        title = "Cherry-pick a merge",
        category = "Cherry-pick",
        source = """
            gitGraph
                commit id: "ZERO"
                branch develop
                branch release
                commit id: "A"
                checkout main
                commit id: "ONE"
                checkout develop
                commit id: "B"
                checkout main
                merge develop id: "MERGE"
                commit id: "TWO"
                checkout release
                cherry-pick id: "MERGE" parent: "B"
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_direction_lr",
        title = "Left to right",
        category = "Direction",
        source = """
            gitGraph LR:
                commit id: "root"
                branch develop
                commit id: "develop-1"
                checkout main
                commit id: "main-1"
                merge develop id: "merge"
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_direction_tb",
        title = "Top to bottom",
        category = "Direction",
        source = """
            gitGraph TB:
                commit id: "root"
                branch develop
                commit id: "develop-1"
                checkout main
                commit id: "main-1"
                merge develop id: "merge"
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_direction_bt",
        title = "Bottom to top",
        category = "Direction",
        source = """
            gitGraph BT:
                commit id: "root"
                branch develop
                commit id: "develop-1"
                checkout main
                commit id: "main-1"
                merge develop id: "merge"
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_branch_order",
        title = "Explicit branch order",
        category = "Branches",
        source = """
            ---
            config:
              gitGraph:
                mainBranchOrder: 2
            ---
            gitGraph
                commit id: "root"
                branch test1 order: 3
                branch test2
                branch test3
                branch test4 order: 1
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_parallel_commits",
        title = "Parallel commit ranks",
        category = "Configuration",
        source = """
            ---
            config:
              gitGraph:
                parallelCommits: true
            ---
            gitGraph
                commit id: "root"
                branch develop
                commit id: "develop-1"
                commit id: "develop-2"
                checkout main
                commit id: "main-1"
                commit id: "main-2"
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_horizontal_labels",
        title = "Horizontal commit labels",
        category = "Configuration",
        source = """
            ---
            config:
              gitGraph:
                rotateCommitLabel: false
            ---
            gitGraph
                commit id: "plan"
                commit id: "build"
                branch verify
                commit id: "test"
                checkout main
                merge verify id: "ship"
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_hidden_decorations",
        title = "Graph without labels",
        category = "Configuration",
        source = """
            ---
            config:
              gitGraph:
                showBranches: false
                showCommitLabel: false
            ---
            gitGraph
                commit
                branch hotfix
                commit type: HIGHLIGHT
                checkout main
                commit type: REVERSE
                merge hotfix
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_custom_main",
        title = "Custom main branch",
        category = "Configuration",
        source = """
            ---
            config:
              gitGraph:
                mainBranchName: trunk
            ---
            gitGraph
                commit id: "root"
                branch release
                commit id: "candidate"
                checkout trunk
                commit id: "patch"
                merge release id: "publish" tag: "v2"
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_metadata",
        title = "Title and accessibility",
        category = "Metadata",
        source = """
            ---
            title: Release train
            ---
            gitGraph
                accTitle: Release branch history
                accDescr {
                  A release branch is verified and merged into the main branch.
                }
                commit id: "baseline"
                branch release
                commit id: "candidate"
                checkout main
                merge release id: "published"
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_quoted_unicode",
        title = "Quoted names and Unicode",
        category = "Text",
        source = """
            gitGraph
                commit id: "start"
                branch "release candidate"
                commit id: "verify-日本語" tag: "候选版本"
                checkout main
                commit id: "准备发布"
                merge "release candidate" id: "发布"
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_theme_variables",
        title = "Git Graph theme variables",
        category = "Styling",
        source = """
            ---
            title: Styled release
            config:
              theme: base
              themeVariables:
                git0: "#0f766e"
                git1: "#c2410c"
                gitInv0: "#ccfbf1"
                gitBranchLabel0: "#ffffff"
                commitLineColor: "#475569"
                commitLabelColor: "#0f172a"
                commitLabelBackground: "#f8fafc"
                tagLabelColor: "#134e4a"
                tagLabelBackground: "#ccfbf1"
                tagLabelBorder: "#0f766e"
                textColor: "#0f172a"
            ---
            gitGraph
                commit id: "root" tag: "v1"
                branch feature
                commit id: "feature" type: HIGHLIGHT
                checkout main
                merge feature id: "release"
        """.trimIndent(),
    ),
    GitGraphDemo(
        id = "gitgraph_complex_release",
        title = "Multi-branch release",
        category = "Complex",
        source = """
            gitGraph LR:
                commit id: "bootstrap"
                commit id: "baseline"
                branch develop order: 2
                commit id: "api"
                branch experiment order: 3
                commit id: "prototype" type: HIGHLIGHT
                checkout develop
                commit id: "integration"
                merge experiment id: "accept-experiment"
                branch release order: 1
                commit id: "candidate" tag: "rc.1"
                checkout main
                commit id: "hotfix" type: REVERSE
                checkout release
                cherry-pick id: "hotfix"
                commit id: "verified"
                checkout main
                merge release id: "v2.0.0" tag: "stable"
        """.trimIndent(),
    ),
)
