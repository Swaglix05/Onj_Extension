package org.onj.language

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
import org.onj.language.psi.OnjFile
import org.onj.language.psi.OnjTokenSets
import org.onj.language.psi.impl.OnjTypes

class OnjParserDefinition : ParserDefinition {

    private val file = IFileElementType(OnjLanguage)

    override fun createLexer(project: Project?): Lexer = object : FlexAdapter(OnjLexer(null)) { }

    override fun createParser(project: Project?): PsiParser = NewOnjParser()

    override fun getFileNodeType(): IFileElementType = file

    override fun getWhitespaceTokens(): TokenSet = TokenSet.WHITE_SPACE

    override fun getCommentTokens(): TokenSet = OnjTokenSets.comments

    override fun getStringLiteralElements(): TokenSet = OnjTokenSets.strings

    override fun createElement(node: ASTNode?): PsiElement = OnjTypes.createElement(node)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = OnjFile(viewProvider)
}