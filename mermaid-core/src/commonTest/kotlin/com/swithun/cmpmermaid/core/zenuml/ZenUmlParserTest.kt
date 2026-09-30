package com.swithun.cmpmermaid.core.zenuml

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.core.zenuml.upstream.zenuml.ZEN_UML_STARTER
import com.swithun.cmpmermaid.core.zenuml.upstream.zenuml.ZenUmlDocument
import com.swithun.cmpmermaid.core.zenuml.upstream.zenuml.ZenUmlFragment
import com.swithun.cmpmermaid.core.zenuml.upstream.zenuml.ZenUmlFragmentKind
import com.swithun.cmpmermaid.core.zenuml.upstream.zenuml.ZenUmlMessage
import com.swithun.cmpmermaid.core.zenuml.upstream.zenuml.ZenUmlMessageKind
import com.swithun.cmpmermaid.core.zenuml.upstream.zenuml.ZenUmlParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ZenUmlParserTest {
    @Test
    fun parsesOfficialParticipantAndMessageForms() {
        val document = parse(
            """
                zenuml
                title Demo
                @Actor Alice
                @Database Bob
                A as "API Gateway"
                Alice->Bob: Hello Bob
                result = A.load(id) {
                  Bob.persist()
                  return done
                }
                service = new Service(region="us")
            """.trimIndent(),
        )

        assertEquals("Demo", document.title)
        assertEquals(
            listOf(ZEN_UML_STARTER, "Alice", "Bob", "A", "service:Service"),
            document.participants.map { it.name },
        )
        assertEquals("Actor", document.participants[1].type)
        assertEquals("API Gateway", document.participants[3].label)

        val async = assertIs<ZenUmlMessage>(document.statements[0])
        assertEquals(ZenUmlMessageKind.Async, async.kind)
        assertEquals("Alice", async.from)
        assertEquals("Bob", async.to)
        assertEquals("Hello Bob", async.label)

        val sync = assertIs<ZenUmlMessage>(document.statements[1])
        assertEquals("load(id)", sync.label)
        assertEquals("result", sync.assignment?.assignee)
        assertEquals(2, sync.body.size)
        assertEquals(ZenUmlMessageKind.Return, assertIs<ZenUmlMessage>(sync.body[1]).kind)

        val creation = assertIs<ZenUmlMessage>(document.statements[2])
        assertEquals(ZenUmlMessageKind.Creation, creation.kind)
        assertEquals("service:Service", creation.to)
        assertEquals("«region=us»", creation.label)
    }

    @Test
    fun preservesExplicitOrderStarterGroupsAndQuotedNames() {
        val document = parse(
            """
                zenuml
                group "Back End" {
                  @EC2 B
                  C as "Cache Node"
                }
                A
                @Starter("Browser User")
                "Browser User"->B: request
            """.trimIndent(),
        )

        assertEquals(
            listOf("B", "C", "A", "Browser User"),
            document.participants.map { it.name },
        )
        assertEquals("Back End", document.participants[0].groupId)
        assertEquals("Cache Node", document.participants[1].label)
        assertTrue(document.participants.last().isStarter)
    }

    @Test
    fun parsesAllFragmentFamiliesAndBranchBodies() {
        val document = parse(
            """
                zenuml
                if(has more items) {
                  A.next()
                } else if(empty) {
                  B.none()
                } else {
                  C.done()
                }
                loop(3) { A.tick() }
                par { A->B: one; A->C: two }
                opt(enabled) { B.optional() }
                critical(resource is locked) { C.lock() }
                section(frame one) { A.section() }
                ref(A, B, C)
                try {
                  A.work()
                } catch(error) {
                  B.recover()
                } finally {
                  C.close()
                }
            """.trimIndent(),
        )

        val fragments = document.statements.filterIsInstance<ZenUmlFragment>()
        assertEquals(
            listOf(
                ZenUmlFragmentKind.Alt,
                ZenUmlFragmentKind.Loop,
                ZenUmlFragmentKind.Par,
                ZenUmlFragmentKind.Opt,
                ZenUmlFragmentKind.Critical,
                ZenUmlFragmentKind.Section,
                ZenUmlFragmentKind.Ref,
                ZenUmlFragmentKind.TryCatchFinally,
            ),
            fragments.map { it.kind },
        )
        assertEquals(listOf("Alt", "[ empty ]", "[else]"), fragments[0].sections.map { it.label })
        assertEquals("has more items", fragments[0].label)
        assertEquals("", fragments[5].label)
        assertEquals("", fragments[5].sections.single().label)
        assertEquals("ref(A,B,C)", fragments[6].label)
        assertEquals(listOf("Try", "catch error", "finally"), fragments.last().sections.map { it.label })
        assertEquals(3, fragments.last().sections.sumOf { it.statements.size })
    }

    @Test
    fun parsesCommentsDividersUnicodeAndReturnArrowForms() {
        val document = parse(
            """
                zenuml
                用户 数据库
                // **Markdown** comment
                用户.登录() {
                  用户-->数据库: 已完成
                  @return
                  数据库->用户: 返回值
                  ===== phase two =====
                }
            """.trimIndent(),
        )

        val outer = assertIs<ZenUmlMessage>(document.statements.single())
        assertEquals(" **Markdown** comment", outer.comment)
        assertEquals("登录()", outer.label)
        assertEquals(3, outer.body.size)
        assertTrue(outer.body.take(2).all {
            assertIs<ZenUmlMessage>(it).kind == ZenUmlMessageKind.Return
        })
    }

    @Test
    fun acceptsTrailingProseSentenceLikeUpstreamAntlrRecovery() {
        val document = parse(
            """
                zenuml
                Customer->Checkout: Place order
                This diagram shows generated result 1.
            """.trimIndent(),
        )

        val message = assertIs<ZenUmlMessage>(document.statements.last())
        assertEquals("This diagram shows generated result 1", message.label)
        assertEquals(message.from, message.to)
    }

    @Test
    fun acceptsMismatchedBlockCloserLikeUpstreamAntlrRecovery() {
        val document = parse(
            """
                zenuml
                par {
                  Coordinator->Inventory: Reserve stock
                )
                critical(commit_order) {
                  OrderStore.save()
                }
            """.trimIndent(),
        )

        val fragments = document.statements.filterIsInstance<ZenUmlFragment>()
        assertEquals(
            listOf(ZenUmlFragmentKind.Par, ZenUmlFragmentKind.Critical),
            fragments.map { it.kind },
        )
        assertEquals(1, fragments.first().sections.single().statements.size)
    }

    @Test
    fun acceptsMissingBlockCloserAtEofLikeUpstreamAntlrRecovery() {
        val document = parse(
            """
                zenuml
                classDiagram
                class Broken {
            """.trimIndent(),
        )

        assertEquals(
            ZenUmlFragmentKind.Section,
            assertIs<ZenUmlFragment>(document.statements.single()).kind,
        )
    }

    @Test
    fun acceptsMissingEmptyBlockAtEofLikeUpstreamAntlrRecovery() {
        val document = parse(
            """
                zenuml
                gantt
                section
            """.trimIndent(),
        )

        assertEquals(
            ZenUmlFragmentKind.Section,
            assertIs<ZenUmlFragment>(document.statements.single()).kind,
        )
    }

    @Test
    fun acceptsParticipantGroupWithoutClosingBraceLikeUpstreamGrammar() {
        val document = parse(
            """
                zenuml
                @Actor Customer
                group "Regional Services" {
                  @EC2 Gateway
                  @Database SessionS
            """.trimIndent(),
        )

        assertEquals(
            listOf("Regional Services", "Regional Services"),
            document.participants.takeLast(2).map { participant -> participant.groupId },
        )
    }

    @Test
    fun acceptsSingleTrailingBlockCloserAtEofLikeUpstreamAntlrRecovery() {
        val document = parse(
            """
                zenuml
                title Nested Transaction
                ParityEvidence132 as "Multi Stage Delivery Coordination Evidence Label 132"
                transaction = new Transaction(region="eu")
                validation = Policy.validate(transaction)
                Repository.save(transaction) {
                  return stored
                }
                return result
                }
            """.trimIndent(),
        )

        assertEquals("Nested Transaction", document.title)
        assertEquals(4, document.statements.size)
    }

    @Test
    fun stopsAtUnexpectedTopLevelBlockCloserLikeUpstreamAntlrRecovery() {
        val document = parse(
            """
                zenuml
                title Conditional Fulfillment
                Customer->Checkout: Place order
                if(in_stock) {
                  Inventory.reserve()
                }
                opt(payment_required) {
                  Payment.authorize()
                }
                } else {
                  Checkout->Customer: Report unavailable
                }
            """.trimIndent(),
        )

        assertEquals(3, document.statements.size)
        assertEquals(
            listOf(ZenUmlFragmentKind.Alt, ZenUmlFragmentKind.Opt),
            document.statements.filterIsInstance<ZenUmlFragment>().map { it.kind },
        )
    }

    @Test
    fun acceptsRefWithoutClosingParenthesisAtEofLikeUpstreamAntlrRecovery() {
        val document = parse(
            """
                zenuml
                ref(Coordinator, Inventory, Billi
            """.trimIndent(),
        )

        assertEquals(
            "ref(Coordinator,Inventory,Billi",
            assertIs<ZenUmlFragment>(document.statements.single()).label,
        )
    }

    @Test
    fun returnsStructuredErrorsAndAppliesMessageLimit() {
        val invalid = ZenUmlParser(
            options = MermaidRenderOptions(),
            lineOffset = 4,
        ).parse("sequenceDiagram\nA->B")
        val parseError = assertIs<MermaidError.Parse>(
            assertIs<GMResult.Err<MermaidError>>(invalid).error,
        )
        assertEquals(5, parseError.line)

        val limited = ZenUmlParser(
            options = MermaidRenderOptions(maxEdges = 1),
        ).parse("zenuml\nA->B: one\nB->C: two")
        val limit = assertIs<MermaidError.ResourceLimit>(
            assertIs<GMResult.Err<MermaidError>>(limited).error,
        )
        assertEquals("ZenUML messages", limit.resource)
    }

    private fun parse(source: String): ZenUmlDocument =
        assertIs<GMResult.Ok<ZenUmlDocument>>(
            ZenUmlParser(
                options = MermaidRenderOptions(),
            ).parse(source),
        ).value
}
