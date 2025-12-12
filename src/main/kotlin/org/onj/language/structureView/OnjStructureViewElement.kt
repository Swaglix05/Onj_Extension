package org.onj.language.structureView

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.PsiElementBase
import org.onj.language.psi.OnjInStructureView

class OnjStructureViewElement(val psiElement: PsiElement) : StructureViewTreeElement {

    override fun getValue(): Any = psiElement

    override fun getPresentation(): ItemPresentation {
        return (psiElement as? PsiElementBase)?.presentation ?: PresentationData()
    }

    override fun navigate(requestFocus: Boolean) {
        (psiElement as? NavigatablePsiElement)?.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean {
        return (psiElement as? NavigatablePsiElement)?.canNavigate() ?: false
    }

    override fun canNavigateToSource(): Boolean {
        return (psiElement as? NavigatablePsiElement)?.canNavigateToSource() ?: false
    }

    override fun getChildren(): Array<out TreeElement?> {
        return psiElement
            .children
            .filterIsInstance<OnjInStructureView>()
            .map { OnjStructureViewElement(it) }
            .toTypedArray()
    }
}