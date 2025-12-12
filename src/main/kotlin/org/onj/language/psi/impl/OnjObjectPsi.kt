package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.NlsSafe
import org.onj.language.psi.OnjInStructureView
import javax.swing.Icon

class OnjObjectPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjInStructureView {

    override fun getPresentation(): ItemPresentation = object : ItemPresentation {

        override fun getPresentableText(): @NlsSafe String = "..."

        override fun getIcon(unused: Boolean): Icon? = getIcon(0)

    }

}
