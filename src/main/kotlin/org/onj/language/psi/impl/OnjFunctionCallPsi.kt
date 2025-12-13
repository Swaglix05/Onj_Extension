package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi

class OnjFunctionCallPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjTypeResolvablePsi {

    override fun resolveTypeSimple(): OnjType = OnjType.Unknown
    override fun resolveTypeFull(): OnjType = OnjType.Unknown
}
