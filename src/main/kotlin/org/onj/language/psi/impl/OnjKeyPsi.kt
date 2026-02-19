@file:Suppress("UnstableApiUsage")

package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolDeclaration
import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.TextRange
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.jetbrains.rd.util.reactive.KeyValuePair
import org.intellij.markdown.IElementType
import org.onj.language.language.OnjLanguage
import org.onj.language.psi.OnjPsiElementWithDocumentation
import org.onj.language.psi.OnjTypes
import org.onj.language.rename.OnjElementFactory
import org.onj.language.symbols.OnjKeySymbol
import org.onj.language.utils.Utils.findInstance

class OnjKeyPsi(node: ASTNode) : ASTWrapperPsiElement(node), NavigatablePsiElement, PsiNameIdentifierOwner, PsiSymbolDeclaration, OnjPsiElementWithDocumentation {

    fun getKeyText(presentable: Boolean): String {
        node.findChildByType(OnjTypes.IDENTIFIER)?.text?.let { return it }
        val string = node.findChildByType(OnjTypes.STRING)?.psi as? OnjStringPsi ?: return ""
        return if (presentable) string.literalStringPreserveEscapes() else string.literalString()
    }

    override fun getNameIdentifier(): PsiElement? {
        val childNode = node.findChildByType(OnjTypes.IDENTIFIER)
            ?: node.findChildByType(OnjTypes.STRING)
            ?: return null
        return childNode.psi
    }

    override fun getName(): String = getKeyText(true)

    override fun setName(name: @NlsSafe String): PsiElement {
        val toReplace = node.findChildByType(OnjTypes.IDENTIFIER)
            ?: children.findInstance<OnjStringPsi>()?.node
            ?: return this
        val validIdentifier = OnjElementFactory.identifierPattern.matches(name)
        val newChild = if (validIdentifier) {
            OnjElementFactory.createOnjIdentifier(project, name)
        } else {
            OnjElementFactory.createOnjString(project, name)
        }
        node.replaceChild(toReplace, newChild.node)
        return this
    }

    override fun renderDoc(): String? {
        val key = getKeyText(true)
        val type = (parent as? OnjKeyValuePairPsi)?.getValue()?.resolveTypeSimple() ?: return null
        val builder = StringBuilder()
        builder.append(DocumentationMarkup.DEFINITION_START)
        val text = "\"$key\": ${type.printableName}"
        HtmlSyntaxInfoUtil.appendHighlightedByLexerAndEncodedAsHtmlCodeSnippet(builder, project, OnjLanguage, text, 1f)
        builder.append(DocumentationMarkup.DEFINITION_END)
        return builder.toString()
    }

    override fun getDeclaringElement(): PsiElement {
        return nameIdentifier!!
    }

    override fun getRangeInDeclaringElement(): TextRange {
        val name = nameIdentifier ?: return TextRange(0, 0)
        val isString = name is OnjStringPsi
        return TextRange(if (isString) 1 else 0, if (isString) name.textLength - 1 else name.textLength)
    }

    override fun getSymbol(): Symbol = OnjKeySymbol(this)

}
