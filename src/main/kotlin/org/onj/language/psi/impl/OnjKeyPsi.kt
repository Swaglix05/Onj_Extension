@file:Suppress("UnstableApiUsage")

package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolDeclaration
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.TextRange
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import org.intellij.markdown.IElementType
import org.onj.language.psi.OnjTypes
import org.onj.language.rename.OnjElementFactory
import org.onj.language.symbols.OnjKeySymbol

class OnjKeyPsi(node: ASTNode) : ASTWrapperPsiElement(node), NavigatablePsiElement, PsiNameIdentifierOwner, PsiSymbolDeclaration {

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

    override fun getName(): String = getKeyText(false)

    override fun setName(name: @NlsSafe String): PsiElement? {
        TODO()
//        val oldIdentifier = node.findChildByType(OnjTypes.IDENTIFIER) ?: return null
//        val newIdentifier = OnjElementFactory.createOnjIdentifier(project, name)
//        node.replaceChild(oldIdentifier, newIdentifier.node)
//        return this
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
