package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.tree.util.children
import org.onj.language.psi.OnjTypes

class OnjStringPsi(node: ASTNode) : ASTWrapperPsiElement(node) {

    fun literalString(): String = node
        .children()
        .joinToString(separator = "") { astNode ->
            when (astNode.elementType) {
                OnjTypes.STRING_PART -> astNode.text
                OnjTypes.STRING_ESCAPE -> when (astNode.text) {
                    "\\n" -> "\n"
                    "\\t" -> "\t"
                    "\\r" -> "\r"
                    "\\\"" -> "\""
                    "\\'" -> "'"
                    "\\\\'" -> "\\"
                    else -> ""
                }
                else -> ""
            }
        }

    fun literalStringPreserveEscapes(): String = node
        .children()
        .joinToString(separator = "") { astNode ->
            when (astNode.elementType) {
                OnjTypes.STRING_PART -> astNode.text
                OnjTypes.STRING_ESCAPE -> astNode.text
                else -> ""
            }
        }

}

class OnjFloatPsi(node: ASTNode) : ASTWrapperPsiElement(node)
class OnjIntPsi(node: ASTNode) : ASTWrapperPsiElement(node)
