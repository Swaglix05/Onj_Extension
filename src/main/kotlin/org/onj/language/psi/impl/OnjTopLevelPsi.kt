package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.tree.util.children
import org.onj.language.psi.OnjCanHaveVariableDeclaration
import org.onj.language.psi.OnjTypes

class OnjTopLevelPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjCanHaveVariableDeclaration {

    fun findSchemaComment(): String? {

        fun checkChild(node: ASTNode): String? {
            if (node.elementType != OnjTypes.LINE_COMMENT) return null
            val text = node.text
            if (!text.startsWith("///")) return null
            val trimmed = text.substring(3).trim()
            if (!trimmed.startsWith("schema=")) return null
            val path = trimmed.removePrefix("schema=")
            return path
        }

        node
            .children()
            .forEach { node ->
                checkChild(node)?.let { return it }
            }
        parent
            .node
            .children()
            .forEach { node ->
                checkChild(node)?.let { return it }
            }
        return null
    }

}
