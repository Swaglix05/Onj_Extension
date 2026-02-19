@file:Suppress("UnstableApiUsage")

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
import org.onj.language.psi.OnjRenamableReference
import org.onj.language.psi.OnjTypes
import org.onj.language.reference.OnjPsiReferenceBySymbolReferenceWrapper
import org.onj.language.rename.OnjElementFactory
import org.onj.language.symbols.OnjKeySymbolReference
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils.findInstance
import java.util.Collections

class OnjVariableAccessorPsi(
    node: ASTNode
) : ASTWrapperPsiElement(node), NavigatablePsiElement, OnjRenamableReference, PsiReference {

    private val reference = OnjPsiReferenceBySymbolReferenceWrapper(this)

    fun getAccessValue(): OnjType {
        val complexAccessor = children.findInstance<OnjTypeResolvablePsi>()
        if (complexAccessor != null) {
            return complexAccessor.resolveTypeFull()
        }
        return node
            .findChildByType(OnjTypes.IDENTIFIER)
            ?.text
            ?.let { OnjType.SpecificStr(it) }
            ?: OnjType.Unknown
    }

    override fun rename(newName: String) {
        val toReplace = node.findChildByType(OnjTypes.IDENTIFIER)
            ?: children.findInstance<OnjStringPsi>()?.node
            ?: return
        val validIdentifier = OnjElementFactory.identifierPattern.matches(newName)
        val newChild = if (validIdentifier) {
            OnjElementFactory.createOnjIdentifier(project, newName)
        } else {
            OnjElementFactory.createOnjString(project, newName)
        }
        node.replaceChild(toReplace, newChild.node)
    }

    override fun getOwnReferences(): @Unmodifiable Collection<out PsiSymbolReference> {
        val complexAccessor = children.findInstance<OnjTypeResolvablePsi>()
        if (complexAccessor != null && complexAccessor !is OnjStringPsi) return listOf()
        val variableAccess = parent as OnjAccessPsi
        val accessed = variableAccess.children.findInstance<OnjTypeResolvablePsi>() ?: return listOf()
        return Collections.singletonList(OnjKeySymbolReference(accessed, this))
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
