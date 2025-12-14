package org.onj.language.psi

import com.intellij.psi.tree.TokenSet

object OnjSchemaTokenSets {

    val comments: TokenSet = TokenSet.create(OnjSchemaTypes.LINE_COMMENT, OnjSchemaTypes.BLOCK_COMMENT)

    val keywords: TokenSet = TokenSet.create(
        OnjSchemaTypes.IMPORT,
        OnjSchemaTypes.VAR,
        OnjSchemaTypes.USE
    )

    val types: TokenSet = TokenSet.create(
        OnjSchemaTypes.STRING,
        OnjSchemaTypes.FLOAT,
        OnjSchemaTypes.BOOLEAN,
        OnjSchemaTypes.INT
    )

    val parens: TokenSet = TokenSet.create(OnjSchemaTypes.L_PAREN, OnjSchemaTypes.R_PAREN)
    val braces: TokenSet = TokenSet.create(OnjSchemaTypes.L_BRACE, OnjSchemaTypes.R_BRACE)
    val brackets: TokenSet = TokenSet.create(OnjSchemaTypes.L_BRACKET, OnjSchemaTypes.R_BRACKET)


}
