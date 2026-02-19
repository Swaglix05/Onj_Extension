package org.onj.language.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.model.Symbol
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.onj.language.psi.OnjPsiElementWithDocumentation
import org.onj.language.psi.OnjTypes
import org.onj.language.psi.OnjVariableDeclaringPsiElement
import org.onj.language.rename.OnjElementFactory
import org.onj.language.symbols.OnjVariableSymbol

class OnjVariableDeclNamePsi(node: ASTNode) : OnjVariableDeclaringPsiElement(node), OnjPsiElementWithDocumentation {


    override fun getNameIdentifier(): PsiElement? {
        val childNode = node.findChildByType(OnjTypes.IDENTIFIER) ?: return null
        return childNode.psi
    }

    override fun getName(): String? {
        return nameIdentifier!!.text
    }

    override fun renderDoc(): String? {
        val parent = parent
        if (parent !is OnjPsiElementWithDocumentation) return null
        return parent.renderDoc()
    }

    override fun setName(name: @NlsSafe String): PsiElement? {
        val oldIdentifier = node.findChildByType(OnjTypes.IDENTIFIER) ?: return null
        val newIdentifier = OnjElementFactory.createOnjIdentifier(project, name)
        node.replaceChild(oldIdentifier, newIdentifier.node)
        return this
    }

    override fun getDeclaringElement(): PsiElement {
        return nameIdentifier!!
    }

    override fun getRangeInDeclaringElement(): TextRange {
        val name = nameIdentifier ?: return TextRange(0, 0)
        return TextRange(0, name.textLength)
    }

    override fun delete() {
        val stmt = parent
        stmt.parent.node.removeChild(stmt.node)
    }

    override fun getSymbol(): Symbol {
        return OnjVariableSymbol(this)
    }

}