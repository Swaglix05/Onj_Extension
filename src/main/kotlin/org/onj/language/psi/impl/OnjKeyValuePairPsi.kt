package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.NlsSafe
import org.onj.language.psi.OnjInStructureView
import org.onj.language.psi.OnjTypes
import javax.swing.Icon

class OnjKeyValuePairPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjInStructureView {

    fun getKey(): OnjKeyPsi = node.findChildByType(OnjTypes.KEY)!!.psi as OnjKeyPsi

    override fun getPresentation(): ItemPresentation = object : ItemPresentation {

        override fun getPresentableText(): @NlsSafe String? = getKey().getKeyText(true)

        override fun getIcon(unused: Boolean): Icon? = getIcon(0)

    }
}
