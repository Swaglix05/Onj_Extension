package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import onj.schema.LiteralOnjSchemaArray
import onj.schema.TypeBasedOnjSchemaArray
import org.onj.language.env.OnjFunctionModel
import org.onj.language.language.OnjFile
import org.onj.language.psi.OnjFunctionLikePsiElement
import org.onj.language.psi.OnjTypes
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils
import org.onj.language.utils.Utils.findInstance

class OnjBinaryOperationPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjTypeResolvablePsi, OnjFunctionLikePsiElement {

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
        val function = resolveFunction()
        return function?.returnType ?: OnjType.Unknown
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
            return resolveFunction()?.returnType ?: OnjType.Unknown
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
        val function = resolveFunction()
        return function?.returnType ?: OnjType.Unknown
    }

    private fun operatorName(): String? = when (getOperation()) {
        '+' -> "plus"
        '-' -> "minus"
        '*' -> "star"
        '/' -> "div"
        null -> null
        else -> throw RuntimeException("unreachable")
    }

    override fun resolvableName(): String? = operatorName()?.let { "operator%$it" }

    override fun printableName(): String? {
        val operator = getOperation() ?: return null
        return "operator($operator)"
    }

    override fun findNameIdentifier(): PsiElement? {
        node.findChildByType(OnjTypes.PLUS)?.let { return it.psi }
        node.findChildByType(OnjTypes.MINUS)?.let { return it.psi }
        node.findChildByType(OnjTypes.STAR)?.let { return it.psi }
        node.findChildByType(OnjTypes.DIV)?.let { return it.psi }
        return null
    }

    override fun resolveFunction(): OnjFunctionModel? {
        val env = (containingFile as OnjFile).getEnvFile()?.getEnvironmentModel() ?: return null
        val topLevel = containingFile.children.findInstance<OnjTopLevelPsi>() ?: return null
        val includedNamespaces = topLevel.findUsedNamespaces()
        val name = resolvableName()
        val candidates = mutableListOf<OnjFunctionModel>()
        includedNamespaces.forEach { namespaceName ->
            val namespace = env.namespaces[namespaceName] ?: return@forEach
            val functions = namespace.functions.filter { it.name == name }
            candidates.addAll(functions)
        }
        if (candidates.isEmpty()) return null
        if (candidates.size == 1) return candidates.first()
        val paramTypes = findParameters().map { it.resolveTypeSimple() }
        // user hasn't finished writing the call yet
        if (paramTypes.size != 2) return null
        candidates.forEach functionTest@{ functionModel ->
            val schema = functionModel.paramsSchema
            if (schema is LiteralOnjSchemaArray) {
                if (schema.schemas.size != 2) return@functionTest
                schema.schemas.forEachIndexed { index, schema ->
                    if (paramTypes[index].matchTypeToSchema(schema) != null) return@functionTest
                }
                return functionModel
            }
            if (schema is TypeBasedOnjSchemaArray) {
                if (schema.size != null && schema.size != 2) return@functionTest
                paramTypes.forEach { type ->
                    if (type.matchTypeToSchema(schema.type) != null) return@functionTest
                }
                return functionModel
            }
        }
        return null
    }

    override fun findParameters(): List<OnjTypeResolvablePsi> {
        return children.filterIsInstance<OnjTypeResolvablePsi>()
    }
}
