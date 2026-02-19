package org.onj.language.structureView

import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.onj.language.psi.OnjInStructureView
import org.onj.language.psi.impl.OnjArrayEntryPsi
import org.onj.language.psi.impl.OnjArrayPsi
import org.onj.language.psi.impl.OnjKeyValuePairPsi
import org.onj.language.psi.impl.OnjObjectPsi
import org.onj.language.psi.impl.OnjTopLevelPsi
import org.onj.language.utils.Utils.findInstance

class OnjTreeBasedStructureViewBuilder(val psiFile: PsiFile) : TreeBasedStructureViewBuilder() {

    override fun isRootNodeShown(): Boolean = true

    override fun createStructureViewModel(editor: Editor?): StructureViewModel {
        return OnjStructureViewModel(editor, psiFile.children.findInstance<OnjTopLevelPsi>()!!, psiFile)
    }

}

class OnjStructureViewModel(
    editor: Editor?,
    root: PsiElement,
    psiFile: PsiFile
) : StructureViewModelBase(psiFile, editor, OnjStructureViewElement(root)), StructureViewModel.ElementInfoProvider {

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement?): Boolean {
        val psiElement = (element as? OnjStructureViewElement)?.psiElement ?: return false
        return psiElement is OnjArrayPsi || psiElement is OnjObjectPsi
    }

    override fun isAlwaysLeaf(element: StructureViewTreeElement?): Boolean {
        if (element !is OnjStructureViewElement) return false
        return element.psiElement.children.none { it is OnjInStructureView }
    }
}
