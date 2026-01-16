package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.tree.util.children
import org.onj.language.psi.OnjTypes
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi

class OnjFunctionCallPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjTypeResolvablePsi {

    fun functionName(): String? {
        val firstChild = node.children().first()
        if (firstChild.elementType == OnjTypes.FUNCTION_NAME) return firstChild.text
        return null
    }

    fun findParameters(): List<OnjTypeResolvablePsi> {
        return children.filterIsInstance<OnjTypeResolvablePsi>()
    }

    override fun resolveTypeSimple(): OnjType = OnjType.Unknown
    override fun resolveTypeFull(): OnjType = OnjType.Unknown
}
