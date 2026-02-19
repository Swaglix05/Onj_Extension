package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils.findInstance

class OnjGroupedValuePsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjTypeResolvablePsi {

    override fun resolveTypeSimple(): OnjType {
        return children.findInstance<OnjTypeResolvablePsi>()?.resolveTypeSimple() ?: OnjType.Unknown
    }

    override fun resolveTypeFull(): OnjType {
        return children.findInstance<OnjTypeResolvablePsi>()?.resolveTypeFull() ?: OnjType.Unknown
    }
}
