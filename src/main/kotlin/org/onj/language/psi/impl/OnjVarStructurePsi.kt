package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.model.Symbol
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.onj.language.psi.OnjCanHaveVariableDeclaration
import org.onj.language.psi.OnjTypes
import org.onj.language.psi.OnjVariableDeclaringPsiElement
import org.onj.language.rename.OnjElementFactory
import org.onj.language.symbols.OnjSymbol
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
