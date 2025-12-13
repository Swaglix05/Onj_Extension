package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.onj.language.psi.OnjTypes
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils.findInstance

class OnjConversionPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjTypeResolvablePsi {

    fun getFunctionName(): String? {
        return node.findChildByType(OnjTypes.CONVERSION_NAME)?.text
    }

    override fun resolveTypeSimple(): OnjType {
        val name = getFunctionName() ?: return OnjType.Unknown
        if (name == "string") return OnjType.SomeStr
        val firstType = children
            .findInstance<OnjTypeResolvablePsi>()
            ?.resolveTypeSimple()
            ?: return OnjType.Unknown
        if (firstType.isInt() && name == "float") return OnjType.SomeFloat
        if (firstType.isFloat() && name == "int") return OnjType.SomeInt
        return OnjType.Unknown
    }

    override fun resolveTypeFull(): OnjType {
        val name = getFunctionName() ?: return OnjType.Unknown
        val firstType = children
            .findInstance<OnjTypeResolvablePsi>()
            ?.resolveTypeFull()
            ?: return OnjType.Unknown
        if (firstType.isSome()) {
            if (name == "string") return OnjType.SomeStr
            if (firstType.isInt() && name == "float") return OnjType.SomeFloat
            if (firstType.isFloat() && name == "int") return OnjType.SomeInt
            return OnjType.Unknown
        } else {
            return when (firstType) {
                is OnjType.SpecificFloat if name == "string" -> OnjType.SpecificStr(firstType.value.toString())
                is OnjType.SpecificInt if name == "string" -> OnjType.SpecificStr(firstType.value.toString())
                is OnjType.SpecificStr if name == "string" -> OnjType.SpecificStr(firstType.value)
                is OnjType.SpecificBool if name == "string" -> OnjType.SpecificStr(firstType.value.toString())
                is OnjType.SpecificObject if name == "string" -> OnjType.SomeStr
                is OnjType.SpecificArray if name == "string" -> OnjType.SomeStr
                is OnjType.SpecificInt if name == "float" -> OnjType.SpecificFloat(firstType.value.toDouble())
                is OnjType.SpecificFloat if name == "int" -> OnjType.SpecificInt(firstType.value.toLong())
                else -> OnjType.Unknown
            }
        }
    }
}
