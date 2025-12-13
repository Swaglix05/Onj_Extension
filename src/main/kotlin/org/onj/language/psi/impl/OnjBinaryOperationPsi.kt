package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.onj.language.psi.OnjTypes
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi

class OnjBinaryOperationPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjTypeResolvablePsi {

    override fun resolveTypeSimple(): OnjType {
        val childElements = children.filterIsInstance<OnjTypeResolvablePsi>()
        if (childElements.size != 2) return OnjType.Unknown
        val first = childElements[0].resolveTypeSimple()
        val second = childElements[1].resolveTypeSimple()
        if (first.isNumber() && second.isNumber()) {
            if (first.isFloat() || second.isFloat()) return OnjType.SomeFloat
            return OnjType.SomeInt
        }
        if (first.isString() && second.isString()) return OnjType.SomeStr
        return OnjType.Unknown
    }

    fun getOperation(): Char? {
        if (node.findChildByType(OnjTypes.PLUS) != null) return '+'
        if (node.findChildByType(OnjTypes.MINUS) != null) return '-'
        if (node.findChildByType(OnjTypes.STAR) != null) return '*'
        if (node.findChildByType(OnjTypes.DIV) != null) return '/'
        return null
    }

    fun getDoubleOperation(): ((Double, Double) -> Double)? = when (getOperation()) {
        '+' -> { a, b, -> a + b }
        '-' -> { a, b, -> a - b }
        '*' -> { a, b, -> a * b }
        '/' -> { a, b, -> a / b }
        else -> null
    }

    fun getLongOperation(): ((Long, Long) -> Long)? = when (getOperation()) {
        '+' -> { a, b, -> a + b }
        '-' -> { a, b, -> a - b }
        '*' -> { a, b, -> a * b }
        '/' -> { a, b, -> a / b }
        else -> null
    }

    fun getStringOperation(): ((String, String) -> String)? = when (getOperation()) {
        '+' -> { a, b, -> a + b }
        else -> null
    }

    override fun resolveTypeFull(): OnjType {
        val childElements = children.filterIsInstance<OnjTypeResolvablePsi>()
        if (childElements.size != 2) return OnjType.Unknown
        val first = childElements[0].resolveTypeFull()
        val second = childElements[1].resolveTypeFull()
        if (first.isSome() || second.isSome()) {
            if (first.isNumber() && second.isNumber()) {
                if (first.isFloat() || second.isFloat()) return OnjType.SomeFloat
                return OnjType.SomeInt
            }
            if (first.isString() && second.isString()) return OnjType.SomeStr
            return OnjType.Unknown
        }
        when {
            first.isString() && second.isString() -> {
                first as OnjType.SpecificStr
                second as OnjType.SpecificStr
                val op = getStringOperation()
                return op
                    ?.let { op(first.value, second.value) }
                    ?.let { OnjType.SpecificStr(it) }
                    ?: OnjType.SomeStr
            }
            first.isInt() && second.isInt() -> {
                first as OnjType.SpecificInt
                second as OnjType.SpecificInt
                val op = getLongOperation()
                return op
                    ?.let { op(first.value, second.value) }
                    ?.let { OnjType.SpecificInt(it) }
                    ?: OnjType.SomeInt
            }
            first.isFloat() && second.isInt() -> {
                first as OnjType.SpecificFloat
                second as OnjType.SpecificInt
                val op = getDoubleOperation()
                return op
                    ?.let { op(first.value, second.value.toDouble()) }
                    ?.let { OnjType.SpecificFloat(it) }
                    ?: OnjType.SomeFloat
            }
            first.isInt() && second.isFloat() -> {
                first as OnjType.SpecificInt
                second as OnjType.SpecificFloat
                val op = getDoubleOperation()
                return op
                    ?.let { op(first.value.toDouble(), second.value) }
                    ?.let { OnjType.SpecificFloat(it) }
                    ?: OnjType.SomeFloat
            }
            first.isFloat() && second.isFloat() -> {
                first as OnjType.SpecificFloat
                second as OnjType.SpecificFloat
                val op = getDoubleOperation()
                return op
                    ?.let { op(first.value, second.value) }
                    ?.let { OnjType.SpecificFloat(it) }
                    ?: OnjType.SomeFloat
            }
        }
        return OnjType.Unknown
    }
}
