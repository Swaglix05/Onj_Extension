package org.onj.language.autocomplete

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.icons.AllIcons
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.util.ProcessingContext
import org.onj.language.psi.OnjCanHaveVariableDeclaration
import org.onj.language.psi.OnjTypes
import org.onj.language.psi.impl.OnjImportStructurePsi
import org.onj.language.psi.impl.OnjTopLevelPsi
import org.onj.language.psi.impl.OnjVariableDeclNamePsi
import org.onj.language.utils.Utils.findInstance


class OnjVariableCompletionContributor : CompletionContributor() {

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
            OnjVariableCompletionProvider()
        )
    }

}

class OnjVariableCompletionProvider : CompletionProvider<CompletionParameters>() {

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
        topLevel
            .children
            .filter { it is OnjCanHaveVariableDeclaration }
            .mapNotNull { it.node.findChildByType(OnjTypes.VARIABLE_DECL_NAME)?.psi }
            .map { it as OnjVariableDeclNamePsi }
            .forEach { element ->
                val lookupElement = OnjVariableLookupElement(element)
                result.consume(lookupElement)
            }
    }

}

class OnjVariableLookupElement(
    val variableDeclaration: OnjVariableDeclNamePsi
) : LookupElement() {

    override fun getLookupString(): String = variableDeclaration.name!!

    override fun getPsiElement(): PsiElement = variableDeclaration

    override fun renderElement(presentation: LookupElementPresentation) {
        presentation.itemText = lookupString
        presentation.icon = AllIcons.Nodes.Variable
        val parent = variableDeclaration.parent
        if (parent.elementType == OnjTypes.IMPORT_STRUCTURE) {
            val importPath = (parent as OnjImportStructurePsi).getImportedPath()
            presentation.tailText = if (importPath == null) {
                "   from import"
            } else {
                "   import $importPath"
            }
        } else if (parent.elementType == OnjTypes.VAR_STRUCTURE) {
            presentation.tailText = "   from variable"
        }
    }

}
