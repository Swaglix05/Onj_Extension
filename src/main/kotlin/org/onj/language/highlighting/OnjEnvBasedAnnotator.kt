package org.onj.language.highlighting

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import onj.schema.LiteralOnjSchemaArray
import onj.schema.OnjSchema
import onj.schema.OnjSchemaAny
import onj.schema.OnjSchemaArray
import onj.schema.OnjSchemaBoolean
import onj.schema.OnjSchemaFloat
import onj.schema.OnjSchemaInt
import onj.schema.OnjSchemaNamedObjectGroup
import onj.schema.OnjSchemaObject
import onj.schema.OnjSchemaString
import onj.schema.TypeBasedOnjSchemaArray
import org.onj.language.env.OnjEnvModel
import org.onj.language.env.OnjFunctionModel
import org.onj.language.psi.OnjTypes
import org.onj.language.psi.impl.OnjFunctionCallPsi
import org.onj.language.psi.impl.OnjTopLevelPsi
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils
import org.onj.language.utils.Utils.findInstance

class OnjEnvBasedAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is OnjFunctionCallPsi) return
        val topLevel = element.containingFile.children.findInstance<OnjTopLevelPsi>()
            ?: return
        val namespaces = topLevel.findUsedNamespaces() + listOf("global")
        val envFile = Utils.findEnvFile(element.project) ?: return
        val envModel = envFile.getEnvironmentModel() ?: return
        annotateFunctionCall(element, namespaces, envModel, holder)
    }

    fun annotateFunctionCall(
        element: OnjFunctionCallPsi,
        usedNamespaces: List<String>,
        envModel: OnjEnvModel,
        holder: AnnotationHolder
    ) {
        val name = element.functionName()
        val nameIdentifier = element.node.findChildByType(OnjTypes.FUNCTION_NAME)?.psi ?: return
        val rightParen = element.node.findChildByType(OnjTypes.R_PAREN)?.psi ?: return
        val candidates = mutableListOf<OnjFunctionModel>()
        usedNamespaces.forEach { namespaceName ->
            val namespace = envModel.namespaces[namespaceName] ?: return@forEach
            namespace.functions.filter { it.name == name }.forEach { candidates.add(it) }
        }
        if (candidates.isEmpty()) {
            holder
                .newAnnotation(HighlightSeverity.WARNING, "Unknown function '$name'")
                .range(nameIdentifier)
                .highlightType(ProblemHighlightType.WARNING)
                .create()
            return
        }
        val params = element.findParameters()
        val paramTypes = params.map { it.resolveTypeSimple() }
        if (candidates.size == 1) {
            annotateSingleCandidate(candidates.first(), paramTypes, holder, params, rightParen)
            return
        }
        val result = findFittingFunction(candidates, paramTypes)
        if (result != null) return
        val message = StringBuilder("No overload of $name callable<br />Possibilities:<br />")
        candidates.forEach { candidate ->
            val index = candidate.paramsString.indexOf(':')
            val paramsString = candidate.paramsString.substring(index + 1, candidate.paramsString.length).trim()
            message.append("$paramsString<br />")
        }
        holder
            .newAnnotation(HighlightSeverity.WARNING, "")
            .tooltip(message.toString())
            .range(nameIdentifier)
            .highlightType(ProblemHighlightType.WARNING)
            .create()

    }

    private fun annotateSingleCandidate(
        candidate: OnjFunctionModel,
        paramTypes: List<OnjType>,
        holder: AnnotationHolder,
        params: List<OnjTypeResolvablePsi>,
        rightParen: PsiElement,
    ) {
        val schema = candidate.paramsSchema
        if (schema is LiteralOnjSchemaArray) {
            paramTypes.forEachIndexed { index, type ->
                val paramSchema = schema.schemas.getOrNull(index)
                if (paramSchema == null) {
                    holder
                        .newAnnotation(HighlightSeverity.ERROR, "Too many arguments")
                        .range(params[index])
                        .highlightType(ProblemHighlightType.GENERIC_ERROR)
                        .create()
                    return@forEachIndexed
                }
                val matchResult = matchTypeToSchema(type, paramSchema) ?: return@forEachIndexed
                holder
                    .newAnnotation(HighlightSeverity.ERROR, matchResult)
                    .range(params[index])
                    .highlightType(ProblemHighlightType.GENERIC_ERROR)
                    .create()
            }
            if (schema.schemas.size > params.size) {
                holder
                    .newAnnotation(HighlightSeverity.ERROR, "Too few arguments")
                    .range(rightParen)
                    .highlightType(ProblemHighlightType.GENERIC_ERROR)
                    .create()
            }
        }
        if (schema is TypeBasedOnjSchemaArray) {
            if (schema.size != null && schema.size != paramTypes.size) {
                val message = "Argument size mismatch: Expected ${schema.size}, found ${paramTypes.size}"
                holder
                    .newAnnotation(HighlightSeverity.ERROR, message)
                    .range(rightParen)
                    .highlightType(ProblemHighlightType.GENERIC_ERROR)
                    .create()
            }
            paramTypes.forEachIndexed { index, type ->
                val result = matchTypeToSchema(type, schema.type)
                    ?: return@forEachIndexed
                holder
                    .newAnnotation(HighlightSeverity.ERROR, result)
                    .range(params[index])
                    .highlightType(ProblemHighlightType.GENERIC_ERROR)
                    .create()
            }
        }
    }

    private fun findFittingFunction(
        candidates: MutableList<OnjFunctionModel>,
        paramTypes: List<OnjType>
    ): OnjFunctionModel? {
        candidates.forEach candidateSearch@{ functionModel ->
            val schema = functionModel.paramsSchema
            if (schema is LiteralOnjSchemaArray) {
                if (schema.schemas.size != paramTypes.size) return@candidateSearch
                schema.schemas.forEachIndexed { index, schema ->
                    if (matchTypeToSchema(paramTypes[index], schema) != null) return@candidateSearch
                }
                return functionModel
            }
            if (schema is TypeBasedOnjSchemaArray) {
                if (schema.size != null && schema.size != paramTypes.size) return@candidateSearch
                paramTypes.forEach {
                    if (matchTypeToSchema(it, schema.type) != null) return@candidateSearch
                }
                return functionModel
            }
        }
        return null
    }

    private fun matchTypeToSchema(type: OnjType, schema: OnjSchema): String? {
        if (type.isUnknown()) return null
        if (type.isNull()) {
            if (schema.nullable) return null
            return "null not allowed here"
        }
        return when (schema) {
            is OnjSchemaAny -> null
            is OnjSchemaFloat -> if (type.isFloat()) null else "Expected float, found: ${type.printableName}"
            is OnjSchemaInt -> if (type.isInt()) null else "Expected int, found: ${type.printableName}"
            is OnjSchemaString -> if (type.isString()) null else "Expected string, found: ${type.printableName}"
            is OnjSchemaBoolean -> if (type.isBool()) null else "Expected boolean, found: ${type.printableName}"
            // No deep matching performed here
            is OnjSchemaNamedObjectGroup -> if (type.isObject()) null else "Expected object, found: ${type.printableName}"
            is OnjSchemaObject -> if (type.isObject()) null else "Expected object, found: ${type.printableName}"
            is OnjSchemaArray -> if (type.isObject()) null else "Expected array, found: ${type.printableName}"
            else -> null
        }
    }
}
