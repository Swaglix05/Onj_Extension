package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.tree.util.children
import org.onj.language.psi.OnjTypes
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi

class OnjStringPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjTypeResolvablePsi {

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

    override fun resolveTypeSimple(): OnjType = OnjType.SomeStr
    override fun resolveTypeFull(): OnjType = OnjType.SpecificStr(literalString())
}

class OnjFloatPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjTypeResolvablePsi {

    override fun resolveTypeSimple(): OnjType = OnjType.SomeFloat

    override fun resolveTypeFull(): OnjType = try {
        OnjType.SpecificFloat(text.toDouble())
    } catch (_: NumberFormatException) {
        OnjType.SomeFloat
    }
}

class OnjIntPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjTypeResolvablePsi {

    override fun resolveTypeSimple(): OnjType = OnjType.SomeInt

    override fun resolveTypeFull(): OnjType = try {
        OnjType.SpecificInt(text.toLong())
    } catch (_: NumberFormatException) {
        OnjType.SomeInt
    }
}
