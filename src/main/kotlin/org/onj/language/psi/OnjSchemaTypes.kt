package org.onj.language.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import org.onj.language.psi.schemaImpl.OnjSchemaFilePsi

object OnjSchemaTypes {

    @JvmField val FILE: IElementType = OnjTokenType("FILE")

    @JvmField val BLOCK_COMMENT: IElementType = OnjTokenType("BLOCK_COMMENT")
    @JvmField val COLON: IElementType = OnjTokenType("COLON")
    @JvmField val COMMA: IElementType = OnjTokenType("COMMA")
    @JvmField val DIV: IElementType = OnjTokenType("DIV")
    @JvmField val DOLLAR: IElementType = OnjTokenType("DOLLAR")
    @JvmField val DOT: IElementType = OnjTokenType("DOT")
    @JvmField val EQUALS: IElementType = OnjTokenType("EQUALS")
    @JvmField val IDENTIFIER: IElementType = OnjTokenType("IDENTIFIER")
    @JvmField val IMPORT: IElementType = OnjTokenType("IMPORT")
    @JvmField val LINE_COMMENT: IElementType = OnjTokenType("LINE_COMMENT")
    @JvmField val L_BRACE: IElementType = OnjTokenType("L_BRACE")
    @JvmField val L_BRACKET: IElementType = OnjTokenType("L_BRACKET")
    @JvmField val L_PAREN: IElementType = OnjTokenType("L_PAREN")
    @JvmField val R_BRACE: IElementType = OnjTokenType("R_BRACE")
    @JvmField val R_BRACKET: IElementType = OnjTokenType("R_BRACKET")
    @JvmField val R_PAREN: IElementType = OnjTokenType("R_PAREN")
    @JvmField val SEMICOLON: IElementType = OnjTokenType("SEMICOLON")
    @JvmField val STAR: IElementType = OnjTokenType("STAR")
    @JvmField val USE: IElementType = OnjTokenType("USE")
    @JvmField val VAR: IElementType = OnjTokenType("VAR")

    @JvmField val STRING: IElementType = OnjTokenType("STRING")
    @JvmField val FLOAT: IElementType = OnjTokenType("FLOAT")
    @JvmField val INT: IElementType = OnjTokenType("INT")
    @JvmField val BOOLEAN: IElementType = OnjTokenType("BOOLEAN")

    fun createElement(node: ASTNode?): PsiElement {
        if (node == null) throw RuntimeException("node is null")
        return when (node.elementType) {
            FILE -> OnjSchemaFilePsi(node)
            else -> throw RuntimeException("unknown node type ${node.elementType}")
        }
    }

}
