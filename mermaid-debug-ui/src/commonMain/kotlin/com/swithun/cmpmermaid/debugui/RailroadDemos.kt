package com.swithun.cmpmermaid.debugui

internal data class RailroadDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val railroadDemos = listOf(
    RailroadDemo(
        id = "railroad_ebnf_expression",
        title = "EBNF expression grammar",
        category = "EBNF",
        source = """
            railroad-ebnf-beta
              title "Expression Grammar"
              expression = term ( ( "+" | "-" ) term )* ;
              term = factor ( ( "*" | "/" ) factor )* ;
              factor = number | "(" expression ")" ;
              number = digit+ ;
              digit = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" ;
        """.trimIndent(),
    ),
    RailroadDemo(
        id = "railroad_ebnf_json",
        title = "EBNF JSON subset",
        category = "EBNF",
        source = """
            railroad-ebnf-beta
              json = element ;
              element = object | array | string | number | "true" | "false" | "null" ;
              object = "{" [ member ( "," member )* ] "}" ;
              array = "[" [ element ( "," element )* ] "]" ;
              member = string ":" element ;
        """.trimIndent(),
    ),
    RailroadDemo(
        id = "railroad_abnf_address",
        title = "ABNF email address",
        category = "ABNF",
        source = """
            railroad-abnf-beta
              title "Email Address"
              address = local-part "@" domain ;
              local-part = 1*( ALPHA / DIGIT / "." / "-" ) ;
              domain = label *( "." label ) ;
              label = 1*( ALPHA / DIGIT / "-" ) ;
        """.trimIndent(),
    ),
    RailroadDemo(
        id = "railroad_peg_identifier",
        title = "PEG identifier",
        category = "PEG",
        source = """
            railroad-peg-beta
              title "Identifiers"
              Identifier <- !Keyword Letter Letter* ;
              Keyword <- "if" / "else" / "while" ;
              Letter <- "a" / "b" / "c" / "_" ;
        """.trimIndent(),
    ),
    RailroadDemo(
        id = "railroad_ir_primitives",
        title = "IR primitives",
        category = "IR",
        source = """
            railroad-beta
              accTitle: Railroad primitives
              accDescr: Explicit constructors define a list grammar.
              list = sequence(
                terminal("["),
                optional(sequence(
                  nonterminal("value"),
                  zeroOrMore(sequence(terminal(","), nonterminal("value")))
                )),
                terminal("]")
              ) ;
              value = choice(terminal("null"), terminal("true"), special("number")) ;
        """.trimIndent(),
    ),
)
