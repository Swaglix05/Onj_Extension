package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils.findInstance

class OnjAccessPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjTypeResolvablePsi {

    override fun resolveTypeSimple(): OnjType {
        val accessor = children.findInstance<OnjVariableAccessorPsi>()
            ?: return OnjType.Unknown
        val accessValue = accessor.getAccessValue()
        if (accessValue.isUnknown() || !accessValue.isSpecific()) return OnjType.Unknown
        val toAccess = children.findInstance<OnjTypeResolvablePsi>()
            ?: return OnjType.Unknown
        val toAccessType = toAccess.resolveTypeFull()
        if (toAccessType.isSome()) return OnjType.Unknown
        return when {
            toAccessType.isObject() && accessValue.isString() -> {
                (toAccessType as OnjType.SpecificObject)
                    .keys[(accessValue as OnjType.SpecificStr).value] ?: OnjType.Unknown
            }
            toAccessType.isArray() && accessValue.isInt() -> {
                (toAccessType as OnjType.SpecificArray)
                    .elements
                    .getOrNull((accessValue as OnjType.SpecificInt).value.toInt())
                    ?: OnjType.Unknown
            }
            else -> OnjType.Unknown
        }
    }

    override fun resolveTypeFull(): OnjType = resolveTypeSimple()
}
