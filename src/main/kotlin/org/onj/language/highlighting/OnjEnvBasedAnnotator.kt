package org.onj.language.highlighting

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.tree.util.children
import com.intellij.psi.PsiElement
import onj.customization.OnjFunction
import onj.schema.LiteralOnjSchemaArray
import onj.schema.TypeBasedOnjSchemaArray
import org.onj.language.env.OnjEnvModel
import org.onj.language.env.OnjFunctionModel
import org.onj.language.language.OnjFile
import org.onj.language.psi.OnjFunctionLikePsiElement
import org.onj.language.psi.OnjTypes
import org.onj.language.psi.impl.OnjFunctionCallPsi
import org.onj.language.psi.impl.OnjInfixFunctionCallPsi
import org.onj.language.psi.impl.OnjTopLevelPsi
import org.onj.language.psi.impl.OnjVariableUsePsi
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils
import org.onj.language.utils.Utils.findInstance

class OnjEnvBasedAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is OnjFunctionLikePsiElement && element !is OnjVariableUsePsi) return
        val topLevel = element.containingFile.children.findInstance<OnjTopLevelPsi>()
            ?: return
        val namespaces = topLevel.findUsedNamespaces()
        val envFile = (element.containingFile as OnjFile).getEnvFile() ?: return
        val envModel = envFile.getEnvironmentModel() ?: return
        if (element is OnjFunctionLikePsiElement) {
            val resolved = annotateFunctionCall(element, namespaces, envModel, holder)
            resolved?.let { checkFunctionCallMethod(element, it, holder) }
        } else if (element is OnjVariableUsePsi) {
            annotateVariableUse(element, holder)
        }
    }

    private fun annotateVariableUse(
        element: OnjVariableUsePsi,
        holder: AnnotationHolder
    ) {
        if (element.name == "null") return
        val localResolve = element
            .reference
            .resolve()
        if (localResolve != null) return
        val globalResolve = element.resolveGlobalVariable()
        if (globalResolve != null) {
            holder
                .newSilentAnnotation(HighlightSeverity.INFORMATION)
                .textAttributes(OnjSyntaxHighlighter.NAMESPACE_VARIABLE_NAME_HIGHLIGHTING.first())
                .create()
            return
        }
        holder
            .newAnnotation(HighlightSeverity.ERROR, "Unknown variable")
            .range(element)
            .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
            .create()
    }

    private fun checkFunctionCallMethod(
        element: OnjFunctionLikePsiElement,
        functionModel: OnjFunctionModel,
        holder: AnnotationHolder
    ) {
        if (element is OnjInfixFunctionCallPsi && !functionModel.isInfix) {
            val nameIdentifier = element.findNameIdentifier() ?: return
            val printableName = element.printableName()
            holder
                .newAnnotation(HighlightSeverity.ERROR, "Function '$printableName' is not callable as infix")
                .range(nameIdentifier)
                .highlightType(ProblemHighlightType.GENERIC_ERROR)
                .create()
        }
        if (element is OnjFunctionCallPsi && functionModel.isInfix) {
            val nameIdentifier = element.findNameIdentifier() ?: return
            val printableName = element.printableName()
            holder
                .newAnnotation(HighlightSeverity.WEAK_WARNING, "Function '$printableName' could be called as infix")
                .range(nameIdentifier)
                .highlightType(ProblemHighlightType.WEAK_WARNING)
                .create()
        }
    }

    private fun annotateFunctionCall(
        element: OnjFunctionLikePsiElement,
        usedNamespaces: List<String>,
        envModel: OnjEnvModel,
        holder: AnnotationHolder
    ): OnjFunctionModel? {
        val resolveName = element.resolvableName()
        val printName = element.printableName()
        val nameIdentifier = element.findNameIdentifier() ?: return null
        val rightParen = element.node.children().last().psi ?: return null
        val candidates = mutableListOf<OnjFunctionModel>()
        usedNamespaces.forEach { namespaceName ->
            val namespace = envModel.namespaces[namespaceName] ?: return@forEach
            namespace.functions.filter { it.name == resolveName }.forEach { candidates.add(it) }
        }
        if (candidates.isEmpty()) {
            holder
                .newAnnotation(HighlightSeverity.ERROR, "Unknown function '$printName'")
                .range(nameIdentifier)
                .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                .create()
            return null
        }
        val params = element.findParameters()
        val paramTypes = params.map { it.resolveTypeSimple() }
        if (candidates.size == 1) {
            val functionModel = candidates.first()
            annotateSingleCandidate(functionModel, paramTypes, holder, params, rightParen)
            return functionModel
        }
        val result = findFittingFunction(candidates, paramTypes)
        if (result != null) return result
        val message = StringBuilder("No overload of $printName callable<br />Possibilities:<br />")
        candidates.forEach { candidate ->
            val index = candidate.paramsString.indexOf(':')
            val paramsString = candidate.paramsString.substring(index + 1, candidate.paramsString.length).trim()
            message.append("$paramsString<br />")
        }
        holder
            .newAnnotation(HighlightSeverity.ERROR, "")
            .tooltip(message.toString())
            .range(nameIdentifier)
            .highlightType(ProblemHighlightType.GENERIC_ERROR)
            .create()
        return null
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
                val matchResult = type.matchTypeToSchema(paramSchema) ?: return@forEachIndexed
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
                val result = type.matchTypeToSchema(schema.type)
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
                    if (paramTypes[index].matchTypeToSchema(schema) != null) return@candidateSearch
                }
                return functionModel
            }
            if (schema is TypeBasedOnjSchemaArray) {
                if (schema.size != null && schema.size != paramTypes.size) return@candidateSearch
                paramTypes.forEach {
                    if (it.matchTypeToSchema(schema.type) != null) return@candidateSearch
                }
                return functionModel
            }
        }
        return null
    }
}
