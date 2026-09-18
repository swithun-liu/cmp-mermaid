package com.swithun.cmpmermaid.debugui

internal data class AgentflowDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val agentflowDemos = listOf(
    AgentflowDemo(
        id = "agentflow_review_pipeline",
        title = "Review pipeline",
        category = "Official syntax",
        source = """
            ---
            config:
              layout: dagre
              theme: default
              look: classic
            ---
            agentflow-beta TB
              brief["Release brief"]@{ shape: input }
              flow writer["Writer Agent"]
                draft["Draft notes"]@{ shape: task }
                lookup["search_changelog"]@{ shape: tool }
                draft --> lookup
              end
              flow reviewer["Review Agent"]
                check["Check claims"]@{ shape: task }
                accurate["Accurate?"]@{ shape: decision }
                check --> accurate
              end
              publish["Publish"]@{ shape: action }
              brief --> writer --> reviewer
              accurate --> publish
        """.trimIndent(),
    ),
    AgentflowDemo(
        id = "agentflow_edge_semantics",
        title = "Edge semantics",
        category = "Edges",
        source = """
            ---
            config:
              layout: dagre
              theme: default
              look: classic
            ---
            agentflow-beta LR
              check["Tests pass?"]@{ shape: decision }
              ship["Ship release"]@{ shape: action }
              fix["Fix build"]@{ shape: task }
              logs["Build logs"]@{ shape: refdoc }
              check -- yes --> ship
              check -- no --> fix
              fix --x check
              fix -.- logs
        """.trimIndent(),
    ),
    AgentflowDemo(
        id = "agentflow_nested_shared_context",
        title = "Nested shared context",
        category = "Containers and global nodes",
        source = """
            ---
            config:
              layout: dagre
              theme: default
              look: classic
            ---
            agentflow-beta TB
              global
                corpus["Shared corpus"]@{ shape: refdoc }
              end
              flow team["Content Team"]
                flow researcher["Researcher"]
                  gather["Gather sources"]@{ shape: task }
                  gather -.- corpus
                end
                flow writer["Writer"]
                  compose["Compose draft"]@{ shape: task }
                  compose -.- corpus
                end
                researcher --> writer
              end
        """.trimIndent(),
    ),
    AgentflowDemo(
        id = "agentflow_collapsed_processing",
        title = "Collapsed processing",
        category = "Collapsed containers",
        source = """
            ---
            config:
              layout: dagre
              theme: default
              look: classic
            ---
            agentflow-beta TB
              intake["Receive request"]@{ shape: input }
              flow processing["Processing"]
                validate["Validate"]@{ shape: task }
                enrich["Enrich"]@{ shape: task }
                validate --> enrich
              end
              processing@{ view: "collapsed" }
              output["Publish result"]@{ shape: action }
              intake --> validate
              enrich --> output
        """.trimIndent(),
    ),
    AgentflowDemo(
        id = "agentflow_connector_metadata",
        title = "Connector metadata",
        category = "Connectors and configuration",
        source = """
            ---
            title: Regional issue automation
            config:
              layout: dagre
              theme: forest
              look: classic
              agentflow:
                nodeSpacing: 64
                rankSpacing: 72
                useMaxWidth: false
            ---
            agentflow-beta LR
              accTitle: Regional issue automation
              accDescr: A typed request is sent through an external connector
              connector github["GitHub API"]
              github@{ protocol: "http", endpoint: "https://api.github.com" }
              request["東京 incident"]@{ shape: input, value: "서울 service" }
              create["create_issue"]@{
                shape: tool
                connectorRef: "github.create_issue"
                params: "incident :: String"
                returns: "Issue"
              }
              request --> create
        """.trimIndent(),
    ),
)
