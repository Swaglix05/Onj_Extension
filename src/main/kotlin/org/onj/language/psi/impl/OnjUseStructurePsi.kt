package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.onj.language.psi.OnjTypes

class OnjUseStructurePsi(node: ASTNode) : ASTWrapperPsiElement(node) {

    fun includedNamespace(): String? = includedNamespacePsi()?.text

    fun includedNamespacePsi(): PsiElement? = node.findChildByType(OnjTypes.IDENTIFIER)?.psi

}
