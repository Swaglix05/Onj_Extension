package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.model.Symbol
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.onj.language.psi.OnjTypes
import org.onj.language.psi.OnjVariableDeclaringPsiElement
import org.onj.language.symbols.OnjSymbol

class OnjImportStructurePsi(node: ASTNode) : OnjVariableDeclaringPsiElement(node) {

    override fun setName(p0: @NlsSafe String): PsiElement? {
        TODO()
    }

    override fun getNameIdentifier(): PsiElement? {
        val childNode = node.findChildByType(OnjTypes.VARIABLE_DECL_NAME) ?: return null
        return childNode.psi
    }


    override fun getDeclaringElement(): PsiElement {
        return nameIdentifier!!
    }

    override fun getRangeInDeclaringElement(): TextRange {
        val name = nameIdentifier ?: return TextRange(0, 0)
        return TextRange(0, name.textLength)
    }

    override fun getSymbol(): Symbol {
        return OnjSymbol(this)
    }
}

class OnjAsContextDependentKeywordPsi(node: ASTNode) : ASTWrapperPsiElement(node)
