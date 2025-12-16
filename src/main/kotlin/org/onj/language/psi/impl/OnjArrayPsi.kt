package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.NlsSafe
import org.onj.language.psi.OnjInStructureView
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils.findInstance
import javax.swing.Icon

class OnjArrayPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjInStructureView, OnjTypeResolvablePsi {

    fun indexOf(entry: OnjArrayEntryPsi): Int {
        var count = 0
        children.forEach { child ->
            if (child !is OnjArrayEntryPsi) return@forEach
            if (child == entry) return count
            count++
        }
        return -1
    }

    override fun resolveTypeSimple(): OnjType = OnjType.SomeArray

    override fun resolveTypeFull(): OnjType {
        val elements = mutableListOf<OnjType>()
        var mayHaveMoreElements = false
        children.forEach { child ->
            if (child is OnjTripleDotPsi) {
                val expr = child.children.findInstance<OnjTypeResolvablePsi>()
                    ?: return@forEach
                val includeType = expr.resolveTypeFull()
                if (includeType !is OnjType.SpecificArray) {
                    mayHaveMoreElements = true
                    return@forEach
                }
                includeType.elements.forEach { elements.add(it) }
            }
            if (child is OnjTypeResolvablePsi) {
                elements.add(child.resolveTypeFull())
            }
        }
        return OnjType.SpecificArray(elements, mayHaveMoreElements)
    }

    override fun getPresentation(): ItemPresentation = object : ItemPresentation {

        override fun getPresentableText(): @NlsSafe String = "..."

        override fun getIcon(unused: Boolean): Icon? = getIcon(0)

    }

}
