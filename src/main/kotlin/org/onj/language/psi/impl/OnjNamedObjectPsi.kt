package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.onj.language.psi.OnjTypes
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils.findInstance

class OnjNamedObjectPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjTypeResolvablePsi {

    override fun getName(): String? {
        return node
            .findChildByType(OnjTypes.NAMED_OBJECT_NAME)
            ?.findChildByType(OnjTypes.IDENTIFIER)
            ?.text
    }

    fun getNameIdentifier(): PsiElement? {
        return node
            .findChildByType(OnjTypes.NAMED_OBJECT_NAME)
            ?.findChildByType(OnjTypes.IDENTIFIER)
            ?.psi
    }

    override fun resolveTypeSimple(): OnjType = OnjType.SomeObject

    override fun resolveTypeFull(): OnjType =
        children.findInstance<OnjObjectPsi>()?.resolveTypeFull() ?: OnjType.SomeObject

}
