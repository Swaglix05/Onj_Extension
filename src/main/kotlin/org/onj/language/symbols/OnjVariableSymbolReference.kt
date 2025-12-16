@file:Suppress("UnstableApiUsage")

package org.onj.language.symbols

import com.intellij.model.SingleTargetReference
import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.onj.language.psi.OnjCanHaveVariableDeclaration
import org.onj.language.psi.OnjVariableDeclaringPsiElement
import org.onj.language.psi.impl.OnjFilePsi
import org.onj.language.psi.impl.OnjTopLevelPsi
import org.onj.language.psi.impl.OnjVarStructurePsi
import org.onj.language.utils.Utils.findInstance


class OnjVariableSymbolReference(val referencingElement: PsiElement) : SingleTargetReference(), PsiSymbolReference {

    override fun resolveSingleTarget(): Symbol? {
        val referencedVariable = referencingElement.text

        var parent = referencingElement
        while (parent.parent !is OnjTopLevelPsi) {
            if (parent is OnjFilePsi) return null
            parent = parent.parent
        }

        fun search(root: OnjCanHaveVariableDeclaration): Symbol? {
            root.children.forEach { child ->
                if (
                    child is OnjVariableDeclaringPsiElement &&
                    (child.nameIdentifier?.textMatches(referencedVariable) ?: false)
                ) {
                    return child.symbol
                }
                if (child !is OnjCanHaveVariableDeclaration) return@forEach
                search(child)?.let { return it }
            }
            return null
        }

        val topLevel = referencingElement
            .containingFile
            .children
            .findInstance<OnjTopLevelPsi>()
            ?: return null

        topLevel.children.forEach { element ->
            if (element == parent) return null // Dont search beyond the reference
            if (element !is OnjCanHaveVariableDeclaration) return@forEach
            search(element)?.let { return it }
        }

        return null
    }

    override fun getElement(): PsiElement = referencingElement

    override fun getRangeInElement(): TextRange = TextRange(0, referencingElement.textLength)

}
