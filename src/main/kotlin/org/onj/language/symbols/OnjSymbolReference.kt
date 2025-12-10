@file:Suppress("UnstableApiUsage")

package org.onj.language.symbols

import com.intellij.model.SingleTargetReference
import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.onj.language.psi.OnjVariableDeclaringPsiElement


class OnjSymbolReference(val referencingElement: PsiElement) : SingleTargetReference(), PsiSymbolReference {

    override fun resolveSingleTarget(): Symbol? {
        val referencedVariable = referencingElement.text

        referencingElement.containingFile.children.forEach { element ->

            if (element !is OnjVariableDeclaringPsiElement) return@forEach

            if (!(element.nameIdentifier?.textMatches(referencedVariable) ?: false)) return@forEach

            return element.symbol
        }
        return null
    }

    override fun getElement(): PsiElement = referencingElement

    override fun getRangeInElement(): TextRange = TextRange(0, referencingElement.textLength)

}
