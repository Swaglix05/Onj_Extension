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
import org.onj.language.psi.OnjTypes
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

class OnjEnvBasedCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val topLevel = parameters
            .originalFile
            .children
            .findInstance<OnjTopLevelPsi>()
            ?: return
        val usedNamespaces = topLevel.findUsedNamespaces()
        val project = parameters.originalFile.project
        val psiFile = Utils.findEnvFile(project) ?: return
        val model = psiFile.getEnvironmentModel() ?: return
        usedNamespaces.forEach {
            val namespaceModel = model.namespaces[it] ?: return@forEach
            namespaceModel.functions.forEach { functionModel ->
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

    override fun getLookupString(): String = functionModel.name + "()"

    override fun getAllLookupStrings(): @Unmodifiable Set<String?> {
        return setOf(lookupString, functionModel.name)
    }

    override fun renderElement(presentation: LookupElementPresentation) {
        presentation.itemText = functionModel.name
        presentation.icon = AllIcons.Nodes.Function
        presentation.typeText = functionModel.returnType.printableName
        presentation.tailText = " from namespace $originNamespace"
    }

    override fun handleInsert(context: InsertionContext) {
        super.handleInsert(context)
        val caret = context.editor.caretModel.primaryCaret
        caret.moveCaretRelatively(-1, 0, false, false)
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
