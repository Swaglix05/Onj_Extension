package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils.findInstance

class OnjNamedObjectPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjTypeResolvablePsi {

    override fun resolveTypeSimple(): OnjType = OnjType.SomeObject

    override fun resolveTypeFull(): OnjType =
        children.findInstance<OnjObjectPsi>()?.resolveTypeFull() ?: OnjType.SomeObject

}
