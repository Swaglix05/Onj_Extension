package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.onj.language.psi.OnjCanHaveVariableDeclaration
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils.findInstance

class OnjVarStructurePsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjCanHaveVariableDeclaration {

    fun simpleDeclarationType(): OnjType {
        return children.findInstance<OnjTypeResolvablePsi>()?.resolveTypeSimple() ?: OnjType.Unknown
    }

    fun fullDeclarationType(): OnjType {
        return children.findInstance<OnjTypeResolvablePsi>()?.resolveTypeFull() ?: OnjType.Unknown
    }

}
