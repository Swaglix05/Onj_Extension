package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.NlsSafe
import org.onj.language.psi.OnjInStructureView
import org.onj.language.psi.OnjTypes
import javax.swing.Icon

class OnjNamedObjectNamePsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjInStructureView {

    fun getObjectName(): String = node
        .findChildByType(OnjTypes.NAMED_OBJECT_NAME)!!
        .findChildByType(OnjTypes.IDENTIFIER)!!
        .psi
        .text

    override fun getPresentation(): ItemPresentation = object : ItemPresentation {

        override fun getPresentableText(): @NlsSafe String = "$${getObjectName()}"

        override fun getIcon(unused: Boolean): Icon? = getIcon(0)

    }

}