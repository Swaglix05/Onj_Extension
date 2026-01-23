package org.onj.language.autocomplete

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.icons.AllIcons
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import org.jetbrains.annotations.Unmodifiable
import org.onj.language.env.OnjFunctionModel
import org.onj.language.env.OnjVariableModel
import org.onj.language.language.OnjFile
import org.onj.language.psi.OnjTypes
import org.onj.language.psi.impl.OnjConversionPsi
import org.onj.language.psi.impl.OnjTopLevelPsi
import org.onj.language.utils.Utils
import org.onj.language.utils.Utils.findInstance


class OnjEnvBasedCompletionContributor : CompletionContributor() {

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns
                .psiElement(OnjTypes.IDENTIFIER)
                .andNot(
                    PlatformPatterns
                        .psiElement()
                        .withParent(PlatformPatterns.psiElement(OnjTypes.KEY))
                )
                .andNot(
                    PlatformPatterns
                        .psiElement()
                        .withParent(PlatformPatterns.psiElement(OnjTypes.VARIABLE_ACCESSOR))
                ),
            OnjEnvBasedCompletionProvider()
        )
    }

}

class OnjEnvBasedCompletionProvider() : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val searchForConversion = parameters.position.parent.parent is OnjConversionPsi
        val topLevel = parameters
            .originalFile
            .children
            .findInstance<OnjTopLevelPsi>()
            ?: return
        val usedNamespaces = topLevel.findUsedNamespaces()
        val psiFile = (parameters.originalFile as OnjFile).getEnvFile() ?: return
        val model = psiFile.getEnvironmentModel() ?: return
        if (searchForConversion) {
            usedNamespaces.forEach {
                val namespaceModel = model.namespaces[it] ?: return@forEach
                namespaceModel.functions.forEach { functionModel ->
                    if (!functionModel.name.startsWith("convert%")) return@forEach
                    result.consume(OnjFunctionModelLookupElement(functionModel, it))
                }
            }
            return
        }
        usedNamespaces.forEach {
            val namespaceModel = model.namespaces[it] ?: return@forEach
            namespaceModel.functions.forEach { functionModel ->
                if (
                    functionModel.name.startsWith("operator%") ||
                    functionModel.name.startsWith("convert%")
                ) {
                    return@forEach
                }
                result.consume(OnjFunctionModelLookupElement(functionModel, it))
            }
            namespaceModel.variables.forEach { (_, variableModel) ->
                result.consume(OnjVariableModelLookupElement(variableModel, it))
            }
        }
    }

}

class OnjFunctionModelLookupElement(
    val functionModel: OnjFunctionModel,
    val originNamespace: String
) : LookupElement() {

    private val insertionText: String
    private val moveCaretBack: Boolean

    init {
        when {
            functionModel.isInfix -> {
                insertionText = functionModel.name + " "
                moveCaretBack = false
            }
            functionModel.name.startsWith("operator%") -> {
                insertionText = when (functionModel.name.removePrefix("operator%")) {
                    "plus" -> "+"
                    "minus" -> "-"
                    "div" -> "/"
                    "star" -> "*"
                    "unaryMinus" -> "-"
                    else -> ""
                }
                moveCaretBack = false
            }
            functionModel.name.startsWith("convert%") -> {
                insertionText = functionModel.name.removePrefix("convert%")
                moveCaretBack = false
            }
            else -> {
                insertionText = functionModel.name + "()"
                moveCaretBack = true
            }
        }
    }

    override fun getLookupString(): String = insertionText

    override fun getAllLookupStrings(): @Unmodifiable Set<String?> {
        return setOf(insertionText, functionModel.name)
    }

    override fun renderElement(presentation: LookupElementPresentation) {
        presentation.itemText = functionModel
            .name
            .removePrefix("convert%")
            .removePrefix("operator%")
        presentation.icon = AllIcons.Nodes.Function
        presentation.typeText = functionModel.returnType.printableName
        presentation.tailText = " from namespace $originNamespace"
    }

    override fun handleInsert(context: InsertionContext) {
        super.handleInsert(context)
        if (moveCaretBack) {
            val caret = context.editor.caretModel.primaryCaret
            caret.moveCaretRelatively(-1, 0, false, false)
        }
    }
}

class OnjVariableModelLookupElement(
    val variableModel: OnjVariableModel,
    val originNamespace: String,
) : LookupElement() {

    override fun getLookupString(): String = variableModel.name

    override fun renderElement(presentation: LookupElementPresentation) {
        presentation.itemText = variableModel.name
        presentation.icon = AllIcons.Nodes.Variable
        presentation.typeText = variableModel.type.printableName
        presentation.tailText = " from namespace $originNamespace"
    }
}
