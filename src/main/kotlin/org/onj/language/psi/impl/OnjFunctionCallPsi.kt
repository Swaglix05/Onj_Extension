package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.tree.util.children
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

class OnjFunctionCallPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjTypeResolvablePsi, OnjFunctionLikePsiElement {

    override fun resolvableName(): String? {
        val firstChild = node.children().first()
        if (firstChild.elementType == OnjTypes.FUNCTION_NAME) return firstChild.text
        return null
    }

    override fun findNameIdentifier(): PsiElement? {
        return node.findChildByType(OnjTypes.FUNCTION_NAME)?.psi
    }

    override fun printableName(): String? = resolvableName()

    override fun findParameters(): List<OnjTypeResolvablePsi> {
        return children.filterIsInstance<OnjTypeResolvablePsi>()
    }

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
        val paramTypes = findParameters().map { it.resolveTypeSimple() }
        candidates.forEach functionTest@{ functionModel ->
            val schema = functionModel.paramsSchema
            if (schema is LiteralOnjSchemaArray) {
                if (schema.schemas.size != paramTypes.size) return@functionTest
                schema.schemas.forEachIndexed { index, schema ->
                    if (paramTypes[index].matchTypeToSchema(schema) != null) return@functionTest
                }
                return functionModel
            }
            if (schema is TypeBasedOnjSchemaArray) {
                if (schema.size != null && schema.size != paramTypes.size) return@functionTest
                paramTypes.forEach { type ->
                    if (type.matchTypeToSchema(schema.type) != null) return@functionTest
                }
                return functionModel
            }
        }
        return null
    }

    override fun resolveTypeSimple(): OnjType {
        return resolveFunction()?.returnType ?: OnjType.Unknown
    }

    override fun resolveTypeFull(): OnjType = resolveTypeSimple()
}
