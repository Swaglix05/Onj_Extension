package org.onj.language.psi

import com.intellij.psi.tree.TokenSet
import org.onj.language.psi.OnjTypes

object OnjTokenSets {

    val comments: TokenSet = TokenSet.create(OnjTypes.LINE_COMMENT, OnjTypes.BLOCK_COMMENT)

    val strings: TokenSet = TokenSet.create(
        OnjTypes.STRING_BEGIN, OnjTypes.STRING_PART, OnjTypes.STRING_ESCAPE,
        OnjTypes.INVALID_STRING_ESCAPE, OnjTypes.STRING_END
    )

    val operators: TokenSet = TokenSet.create(
        OnjTypes.PLUS, OnjTypes.MINUS, OnjTypes.STAR, OnjTypes.DIV, OnjTypes.HASH, OnjTypes.DOLLAR
    )

    val binaryOperators: TokenSet = TokenSet.create(
        OnjTypes.PLUS, OnjTypes.MINUS, OnjTypes.STAR, OnjTypes.DIV
    )

    val keywords: TokenSet = TokenSet.create(
        OnjTypes.USE, OnjTypes.IMPORT, OnjTypes.VAR
    )

//        val calledNames: TokenSet = TokenSet.create(
//            OnjTypes.NAMED_OBJECT_NAME, OnjTypes.FUNCTION_NAME, OnjTypes.VARIABLE_EXPRESSION
//        )

    val parens: TokenSet = TokenSet.create(OnjTypes.L_PAREN, OnjTypes.R_PAREN)
    val braces: TokenSet = TokenSet.create(OnjTypes.L_BRACE, OnjTypes.R_BRACE)
    val brackets: TokenSet = TokenSet.create(OnjTypes.L_BRACKET, OnjTypes.R_BRACKET)

}