package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.TextRange
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import org.jetbrains.annotations.Unmodifiable
import org.onj.language.psi.OnjRenamableVariableReference
import org.onj.language.psi.OnjTypes
import org.onj.language.reference.OnjPsiReferenceBySymbolReferenceWrapper
import org.onj.language.rename.OnjElementFactory
import org.onj.language.symbols.OnjSymbolReference
import java.util.Collections

class OnjVariableUsePsi(node: ASTNode) : ASTWrapperPsiElement(node), NavigatablePsiElement, OnjRenamableVariableReference, PsiReference {

    private val reference = OnjPsiReferenceBySymbolReferenceWrapper(this)

    override fun rename(newName: String) {
        val oldIdentifier = node.findChildByType(OnjTypes.IDENTIFIER) ?: return
        val newIdentifier = OnjElementFactory.createOnjIdentifier(project, newName)
        node.replaceChild(oldIdentifier, newIdentifier.node)
    }

    override fun getOwnReferences(): @Unmodifiable Collection<out PsiSymbolReference> {
        return Collections.singletonList(OnjSymbolReference(this))
    }

    override fun getReference(): PsiReference {
        return reference
    }

    override fun getElement(): PsiElement = reference.element

    override fun getRangeInElement(): TextRange = reference.rangeInElement

    override fun resolve(): PsiElement? = reference.resolve()

    override fun getCanonicalText(): @NlsSafe String = reference.canonicalText

    override fun handleElementRename(newElementName: String): PsiElement? = reference.handleElementRename(newElementName)

    override fun bindToElement(element: PsiElement): PsiElement? = reference.bindToElement(element)

    override fun isReferenceTo(element: PsiElement): Boolean = reference.isReferenceTo(element)

    override fun isSoft(): Boolean = reference.isSoft
}
