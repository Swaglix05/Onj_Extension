package org.onj.language.highlighting

import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.onj.language.OnjSchemaLexer
import org.onj.language.highlighting.OnjSyntaxHighlighter.Companion.BAD_CHARACTER_HIGHLIGHTING
import org.onj.language.highlighting.OnjSyntaxHighlighter.Companion.BLOCK_COMMENT_HIGHLIGHTING
import org.onj.language.highlighting.OnjSyntaxHighlighter.Companion.BRACES_HIGHLIGHTING
import org.onj.language.highlighting.OnjSyntaxHighlighter.Companion.BRACKET_HIGHLIGHTING
import org.onj.language.highlighting.OnjSyntaxHighlighter.Companion.COMMA_HIGHLIGHTING
import org.onj.language.highlighting.OnjSyntaxHighlighter.Companion.DOT_HIGHLIGHTING
import org.onj.language.highlighting.OnjSyntaxHighlighter.Companion.ESCAPE_HIGHLIGHTING
import org.onj.language.highlighting.OnjSyntaxHighlighter.Companion.INVALID_ESCAPE_HIGHLIGHTING
import org.onj.language.highlighting.OnjSyntaxHighlighter.Companion.KEYWORD_HIGHLIGHTING
import org.onj.language.highlighting.OnjSyntaxHighlighter.Companion.LINE_COMMENT_HIGHLIGHTING
import org.onj.language.highlighting.OnjSyntaxHighlighter.Companion.PAREN_HIGHLIGHTING
import org.onj.language.highlighting.OnjSyntaxHighlighter.Companion.SEMICOLON_HIGHLIGHTING
import org.onj.language.highlighting.OnjSyntaxHighlighter.Companion.STRING_HIGHLIGHTING
import org.onj.language.psi.OnjSchemaTokenSets
import org.onj.language.psi.OnjSchemaTypes
import org.onj.language.psi.OnjTokenSets
import org.onj.language.psi.OnjTypes

class OnjSchemaSyntaxHighlighter : SyntaxHighlighterBase() {
    
    override fun getHighlightingLexer(): Lexer = object : FlexAdapter(OnjSchemaLexer(null)) { }

    override fun getTokenHighlights(tokenType: IElementType?): Array<out TextAttributesKey?> = when (tokenType) {

        TokenType.BAD_CHARACTER -> BAD_CHARACTER_HIGHLIGHTING
        OnjTypes.STRING_ESCAPE -> ESCAPE_HIGHLIGHTING
        OnjTypes.INVALID_STRING_ESCAPE -> INVALID_ESCAPE_HIGHLIGHTING
        in OnjSchemaTokenSets.strings -> STRING_HIGHLIGHTING
        OnjSchemaTypes.LINE_COMMENT -> LINE_COMMENT_HIGHLIGHTING
        OnjSchemaTypes.BLOCK_COMMENT -> BLOCK_COMMENT_HIGHLIGHTING
        in OnjSchemaTokenSets.parens -> PAREN_HIGHLIGHTING
        in OnjSchemaTokenSets.brackets -> BRACKET_HIGHLIGHTING
        in OnjSchemaTokenSets.braces -> BRACES_HIGHLIGHTING
        in OnjSchemaTokenSets.keywords -> KEYWORD_HIGHLIGHTING
        in OnjSchemaTokenSets.types -> TYPE_HIGHLIGHTING
        OnjSchemaTypes.DOT -> DOT_HIGHLIGHTING
        OnjSchemaTypes.STAR -> KEYWORD_HIGHLIGHTING
        OnjSchemaTypes.QUESTION_MARK -> KEYWORD_HIGHLIGHTING
        OnjSchemaTypes.SEMICOLON -> SEMICOLON_HIGHLIGHTING
        OnjSchemaTypes.COMMA -> COMMA_HIGHLIGHTING
        
        else -> arrayOf()
    }

    companion object {
        val TYPE_HIGHLIGHTING = arrayOf(
            TextAttributesKey
                .createTextAttributesKey("ONJ_SCHEMA_TYPE", DefaultLanguageHighlighterColors.KEYWORD)
        )
    }

}
