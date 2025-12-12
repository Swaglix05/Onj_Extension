package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.NlsSafe
import org.onj.language.psi.OnjInStructureView
import javax.swing.Icon

class OnjArrayPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjInStructureView {

    fun indexOf(entry: OnjArrayEntryPsi): Int {
        var count = 0
        children.forEach { child ->
            if (child !is OnjArrayEntryPsi) return@forEach
            if (child == entry) return count
            count++
        }
        return -1
    }

    override fun getPresentation(): ItemPresentation = object : ItemPresentation {

        override fun getPresentableText(): @NlsSafe String = "..."

        override fun getIcon(unused: Boolean): Icon? = getIcon(0)

    }

}
