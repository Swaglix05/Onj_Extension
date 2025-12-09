package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import org.onj.language.reference.OnjReference

class OnjVariableUsePsi(node: ASTNode) : ASTWrapperPsiElement(node) {

    override fun getReference(): PsiReference {
        return OnjReference(this, textRange)
    }
}
