package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils.findInstance

class OnjNegationPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjTypeResolvablePsi {

    override fun resolveTypeSimple(): OnjType {
        val childType = children
            .findInstance<OnjTypeResolvablePsi>()
            ?.resolveTypeSimple()
            ?: OnjType.Unknown
        if (childType.isFloat() || childType.isInt()) return childType
        return OnjType.Unknown
    }

    override fun resolveTypeFull(): OnjType {
        val childType = children
            .findInstance<OnjTypeResolvablePsi>()
            ?.resolveTypeFull()
            ?: OnjType.Unknown
        if (childType.isSome()) {
            if (childType.isFloat() || childType.isInt()) return childType
            return OnjType.Unknown
        }
        return when (childType) {
            is OnjType.SpecificFloat -> OnjType.SpecificFloat(-childType.value)
            is OnjType.SpecificInt -> OnjType.SpecificInt(-childType.value)
            else -> OnjType.Unknown
        }
    }
}
