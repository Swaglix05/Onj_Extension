package org.onj.language.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import org.onj.language.OnjSchemaLexer
import org.onj.language.language.OnjSchemaFile
import org.onj.language.language.OnjSchemaLanguage
import org.onj.language.psi.OnjSchemaTokenSets
import org.onj.language.psi.OnjSchemaTypes

class OnjSchemaParserDefinition : ParserDefinition {

    private val file = IFileElementType(OnjSchemaLanguage)

    override fun createLexer(project: Project?): Lexer = object : FlexAdapter(OnjSchemaLexer(null)) { }

    override fun createParser(project: Project?): PsiParser = OnjSchemaParser()

    override fun getFileNodeType(): IFileElementType = file

    override fun getWhitespaceTokens(): TokenSet = TokenSet.WHITE_SPACE

    override fun getCommentTokens(): TokenSet = OnjSchemaTokenSets.comments

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createElement(node: ASTNode?): PsiElement = OnjSchemaTypes.createElement(node)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = OnjSchemaFile(viewProvider)

}
