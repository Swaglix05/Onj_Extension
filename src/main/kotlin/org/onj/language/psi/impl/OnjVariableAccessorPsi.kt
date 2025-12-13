package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.onj.language.psi.OnjTypes
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils.findInstance

class OnjVariableAccessorPsi(node: ASTNode) : ASTWrapperPsiElement(node) {

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

}
