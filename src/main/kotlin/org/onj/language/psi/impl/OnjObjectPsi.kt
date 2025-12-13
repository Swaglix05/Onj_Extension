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

class OnjObjectPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjInStructureView, OnjTypeResolvablePsi {

    override fun resolveTypeSimple(): OnjType = OnjType.SomeObject

    override fun resolveTypeFull(): OnjType {
        val elements = mutableMapOf<String, OnjType>()
        children.forEach { child ->
            if (child is OnjTripleDotPsi) {
                val expr = child.children.findInstance<OnjTypeResolvablePsi>()
                    ?: return@forEach
                val includeType = expr.resolveTypeFull()
                if (includeType !is OnjType.SpecificObject) return@forEach
                elements.putAll(includeType.keys)
            }
            if (child !is OnjKeyValuePairPsi) return@forEach
            val value = child.getValue()?.resolveTypeFull() ?: return@forEach
            val key = child.getKey().getKeyText(false)
            elements[key] = value
        }
        return OnjType.SpecificObject(elements)
    }

    override fun getPresentation(): ItemPresentation = object : ItemPresentation {

        override fun getPresentableText(): @NlsSafe String = "..."

        override fun getIcon(unused: Boolean): Icon? = getIcon(0)

    }

}
