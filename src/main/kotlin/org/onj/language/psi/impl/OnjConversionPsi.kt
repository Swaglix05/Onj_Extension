package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import onj.schema.LiteralOnjSchemaArray
import onj.schema.TypeBasedOnjSchemaArray
import org.onj.language.env.OnjFunctionModel
import org.onj.language.psi.OnjFunctionLikePsiElement
import org.onj.language.psi.OnjTypes
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils
import org.onj.language.utils.Utils.findInstance

class OnjConversionPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjTypeResolvablePsi, OnjFunctionLikePsiElement {

    override fun resolveTypeSimple(): OnjType {
        val name = printableName() ?: return OnjType.Unknown
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
        val name = printableName() ?: return OnjType.Unknown
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

    override fun resolvableName(): String? = findNameIdentifier()?.let { "convert%${it.text}" }

    override fun printableName(): String? = findNameIdentifier()?.text

    override fun findNameIdentifier(): PsiElement? = node.findChildByType(OnjTypes.FUNCTION_NAME)?.psi

    override fun resolveFunction(): OnjFunctionModel? {
        val env = Utils.findEnvFile(project)?.getEnvironmentModel() ?: return null
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
