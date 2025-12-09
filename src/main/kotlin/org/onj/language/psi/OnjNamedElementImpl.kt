package org.onj.language.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.util.elementType

abstract class OnjNamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node) {
    override fun getReference(): PsiReference? {
        val references = getReferences()
        return if (references.isEmpty()) null else references[0]
    }

    override fun getReferences(): Array<PsiReference> {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this);
    }

    override fun isEquivalentTo(another: PsiElement?): Boolean {
        return isEqual(this, another)
    }

    companion object {
        fun isEqual(first: PsiElement?, second: PsiElement?): Boolean {
            if (first == second) return true
            if (first == null || second == null) return false
            val hasSameText = first.text == second.text
            if (first.elementType == second.elementType && hasSameText) return true
            var curChild = first.firstChild
            var otherChild = second.firstChild
            while (curChild != null || otherChild != null) {
                if (!isEqual(curChild, otherChild)) return false
                curChild = curChild.nextSibling
                otherChild = otherChild.nextSibling
            }
            return true
        }
    }
}