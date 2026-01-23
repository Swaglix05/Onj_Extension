package org.onj.language.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import org.onj.language.psi.schemaImpl.OnjSchemaFilePsi

object OnjSchemaTypes {

    @JvmField val FILE: IElementType = OnjSchemaTokenType("FILE")

    @JvmField val BLOCK_COMMENT: IElementType = OnjSchemaTokenType("BLOCK_COMMENT")
    @JvmField val COLON: IElementType = OnjSchemaTokenType("COLON")
    @JvmField val COMMA: IElementType = OnjSchemaTokenType("COMMA")
    @JvmField val DIV: IElementType = OnjSchemaTokenType("DIV")
    @JvmField val DOLLAR: IElementType = OnjSchemaTokenType("DOLLAR")
    @JvmField val DOT: IElementType = OnjSchemaTokenType("DOT")
    @JvmField val QUESTION_MARK: IElementType = OnjSchemaTokenType("QUESTION_MARK")
    @JvmField val EQUALS: IElementType = OnjSchemaTokenType("EQUALS")
    @JvmField val IDENTIFIER: IElementType = OnjSchemaTokenType("IDENTIFIER")
    @JvmField val IMPORT: IElementType = OnjSchemaTokenType("IMPORT")
    @JvmField val LINE_COMMENT: IElementType = OnjSchemaTokenType("LINE_COMMENT")
    @JvmField val L_BRACE: IElementType = OnjSchemaTokenType("L_BRACE")
    @JvmField val L_BRACKET: IElementType = OnjSchemaTokenType("L_BRACKET")
    @JvmField val L_PAREN: IElementType = OnjSchemaTokenType("L_PAREN")
    @JvmField val R_BRACE: IElementType = OnjSchemaTokenType("R_BRACE")
    @JvmField val R_BRACKET: IElementType = OnjSchemaTokenType("R_BRACKET")
    @JvmField val R_PAREN: IElementType = OnjSchemaTokenType("R_PAREN")
    @JvmField val SEMICOLON: IElementType = OnjSchemaTokenType("SEMICOLON")
    @JvmField val STAR: IElementType = OnjSchemaTokenType("STAR")
    @JvmField val USE: IElementType = OnjSchemaTokenType("USE")
    @JvmField val VAR: IElementType = OnjSchemaTokenType("VAR")

    @JvmField val STRING_BEGIN: IElementType = OnjSchemaTokenType("STRING_BEGIN")
    @JvmField val STRING_END: IElementType = OnjSchemaTokenType("STRING_END")
    @JvmField val STRING_ESCAPE: IElementType = OnjSchemaTokenType("STRING_ESCAPE")
    @JvmField val INVALID_STRING_ESCAPE: IElementType = OnjSchemaTokenType("INVALID_STRING_ESCAPE")
    @JvmField val STRING_PART: IElementType = OnjSchemaTokenType("STRING_PART")

    @JvmField val STRING: IElementType = OnjSchemaTokenType("STRING")
    @JvmField val FLOAT: IElementType = OnjSchemaTokenType("FLOAT")
    @JvmField val INT: IElementType = OnjSchemaTokenType("INT")
    @JvmField val BOOLEAN: IElementType = OnjSchemaTokenType("BOOLEAN")

    fun createElement(node: ASTNode?): PsiElement {
        if (node == null) throw RuntimeException("node is null")
        return when (node.elementType) {
            FILE -> OnjSchemaFilePsi(node)
            else -> throw RuntimeException("unknown node type ${node.elementType}")
        }
    }

}
