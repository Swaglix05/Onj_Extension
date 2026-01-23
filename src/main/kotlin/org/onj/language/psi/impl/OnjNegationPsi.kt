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
import org.onj.language.utils.Utils.findInstance

class OnjNegationPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjTypeResolvablePsi, OnjFunctionLikePsiElement {

    override fun resolveTypeSimple(): OnjType {
        val childType = children
            .findInstance<OnjTypeResolvablePsi>()
            ?.resolveTypeSimple()
            ?: OnjType.Unknown
        if (childType.isFloat() || childType.isInt()) return childType
        return resolveFunction()?.returnType ?: OnjType.Unknown
    }

    override fun resolveTypeFull(): OnjType {
        val childType = children
            .findInstance<OnjTypeResolvablePsi>()
            ?.resolveTypeFull()
            ?: OnjType.Unknown
        if (childType.isSome()) {
            if (childType.isFloat() || childType.isInt()) return childType
        }
        return when (childType) {
            is OnjType.SpecificFloat -> OnjType.SpecificFloat(-childType.value)
            is OnjType.SpecificInt -> OnjType.SpecificInt(-childType.value)
            else -> resolveFunction()?.returnType ?: OnjType.Unknown
        }
    }

    override fun resolvableName(): String = "operator%unaryMinus"

    override fun printableName(): String = "operator(unary -)"

    override fun findNameIdentifier(): PsiElement? = node.findChildByType(OnjTypes.MINUS)?.psi

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
        val params = findParameters()
        if (params.size != 1) return null
        val paramType = params.first().resolveTypeSimple()
        candidates.forEach functionTest@{ functionModel ->
            val schema = functionModel.paramsSchema
            if (schema is LiteralOnjSchemaArray) {
                if (schema.schemas.size != 1) return@functionTest
                if (paramType.matchTypeToSchema(schema.schemas.first()) != null) return@functionTest
                return functionModel
            }
            if (schema is TypeBasedOnjSchemaArray) {
                if (schema.size != null && schema.size != 1) return@functionTest
                if (paramType.matchTypeToSchema(schema.type) != null) return@functionTest
                return functionModel
            }
        }
        return null
    }

    override fun findParameters(): List<OnjTypeResolvablePsi> = children.filterIsInstance<OnjTypeResolvablePsi>()
}
