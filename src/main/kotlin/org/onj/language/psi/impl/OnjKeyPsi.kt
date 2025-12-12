package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.intellij.markdown.IElementType
import org.onj.language.psi.OnjTypes

class OnjKeyPsi(node: ASTNode) : ASTWrapperPsiElement(node) {

    fun getKeyText(presentable: Boolean): String {
        node.findChildByType(OnjTypes.IDENTIFIER)?.text?.let { return it }
        val string = node.findChildByType(OnjTypes.STRING)?.psi as? OnjStringPsi ?: return ""
        return if (presentable) string.literalStringPreserveEscapes() else string.literalString()
    }

}
