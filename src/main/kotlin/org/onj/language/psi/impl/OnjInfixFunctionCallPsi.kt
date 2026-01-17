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

class OnjInfixFunctionCallPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjTypeResolvablePsi, OnjFunctionLikePsiElement {

    override fun resolveTypeSimple(): OnjType = resolveFunction()?.returnType ?: OnjType.Unknown

    override fun resolveTypeFull(): OnjType = resolveTypeSimple()

    override fun resolvableName(): String? = node.findChildByType(OnjTypes.FUNCTION_NAME)?.text
    override fun printableName(): String? = resolvableName()

    override fun findNameIdentifier(): PsiElement? {
        return node.findChildByType(OnjTypes.FUNCTION_NAME)?.psi
    }

    override fun findParameters(): List<OnjTypeResolvablePsi> {
        return children.filterIsInstance<OnjTypeResolvablePsi>()
    }

    override fun resolveFunction(): OnjFunctionModel? {
        // Note that this function doesn't really care if the functionModel it resolves isn't actually an infix
        // function. In case the user accidentally calls a function that wasn't declared as infix with infix syntax, it will
        // resolve correctly anyway. Reporting errors is the job of the annotator
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
}
