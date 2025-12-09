package org.onj.language.highlighting

import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.onj.language.OnjLexer
import org.onj.language.psi.OnjTokenSets
import org.onj.language.psi.OnjTypes


class OnjSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = object : FlexAdapter(OnjLexer(null)) { }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = when (tokenType) {
        TokenType.BAD_CHARACTER -> BAD_CHARACTER_HIGHLIGHTING
        OnjTypes.STRING_ESCAPE -> ESCAPE_HIGHLIGHTING
        OnjTypes.INVALID_STRING_ESCAPE -> INVALID_ESCAPE_HIGHLIGHTING
        in OnjTokenSets.strings -> STRING_HIGHLIGHTING
        OnjTypes.LINE_COMMENT -> LINE_COMMENT_HIGHLIGHTING
        OnjTypes.BLOCK_COMMENT -> BLOCK_COMMENT_HIGHLIGHTING
        OnjTypes.INTEGER, OnjTypes.FLOAT -> NUMBER_HIGHLIGHTING
        in OnjTokenSets.operators -> OPERATOR_HIGHLIGHTING
        in OnjTokenSets.parens -> PAREN_HIGHLIGHTING
        in OnjTokenSets.brackets -> BRACKET_HIGHLIGHTING
        in OnjTokenSets.braces -> BRACES_HIGHLIGHTING
        in OnjTokenSets.keywords -> KEYWORD_HIGHLIGHTING
        OnjTypes.DOT -> DOT_HIGHLIGHTING
        OnjTypes.SEMICOLON -> SEMICOLON_HIGHLIGHTING
        OnjTypes.COMMA -> COMMA_HIGHLIGHTING

        else -> arrayOf()
    }

    companion object {

        val KEY_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_KEY", DefaultLanguageHighlighterColors.LOCAL_VARIABLE)
        )

        val BAD_CHARACTER_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)
        )

        val STRING_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_STRING", DefaultLanguageHighlighterColors.STRING)
        )

        val LINE_COMMENT_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        )

        val BLOCK_COMMENT_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
        )

        val NUMBER_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        )

        val ESCAPE_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_ESCAPE", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE)
        )

        val INVALID_ESCAPE_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_INVALID_ESCAPE", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE)
        )

        val FUNCTION_NAME_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_FUNCTION_NAME", DefaultLanguageHighlighterColors.STATIC_METHOD)
        )

        val NAMED_OBJECT_NAME_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_NAMED_OBJECT_NAME", DefaultLanguageHighlighterColors.CLASS_NAME)
        )

        val VARIABLE_NAME_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_VARIABLE_NAME", DefaultLanguageHighlighterColors.STATIC_FIELD)
        )

        val OPERATOR_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        )

        val SEMICOLON_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
        )

        val DOT_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_DOT", DefaultLanguageHighlighterColors.DOT)
        )

        val KEYWORD_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        )

        val BRACKET_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS)
        )

        val BRACES_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_BRACES", DefaultLanguageHighlighterColors.BRACES)
        )

        val PAREN_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_PARENS", DefaultLanguageHighlighterColors.PARENTHESES)
        )

        val COMMA_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_COMMA", DefaultLanguageHighlighterColors.COMMA)
        )

        val VARIABLE_ACCESS_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_VARIABLE_ACCESS", DefaultLanguageHighlighterColors.INSTANCE_FIELD)
        )

    }
}