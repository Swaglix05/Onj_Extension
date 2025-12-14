@file:Suppress("UnstableApiUsage")

package org.onj.language.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import org.onj.language.psi.OnjRenamableReference
import org.onj.language.symbols.OnjPsiElementBasedSymbol

class OnjPsiReferenceBySymbolReferenceWrapper(
    private val elementWithSymbol: PsiElement
) : PsiReferenceBase<PsiElement>(elementWithSymbol) {

    override fun resolve(): PsiElement? {
        val result = elementWithSymbol.ownReferences.firstOrNull()?.resolveReference()?.firstOrNull() ?: return null
        if (result !is OnjPsiElementBasedSymbol) return null
        return result.psiElement
    }

    override fun getRangeInElement(): TextRange {
        return TextRange(0, elementWithSymbol.textLength)
    }

    override fun handleElementRename(newElementName: String): PsiElement? {
        return if (elementWithSymbol is OnjRenamableReference) {
            elementWithSymbol.rename(newElementName)
            elementWithSymbol
        } else {
            null
        }
    }
}
