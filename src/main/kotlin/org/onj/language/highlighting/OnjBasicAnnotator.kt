package org.onj.language.highlighting

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.elementType
import org.onj.language.psi.OnjTypes
import org.onj.language.psi.impl.OnjImportStructurePsi
import org.onj.language.psi.impl.OnjVariableDeclNamePsi
import org.onj.language.quickFixes.DeleteUnusedQuickFix

class OnjBasicAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) = when (element.elementType) {

        OnjTypes.KEY ->
            annotateWithAttribute(element, holder, OnjSyntaxHighlighter.KEY_HIGHLIGHTING)

        OnjTypes.AS_CONTEXT_DEPENDENT_KEYWORD ->
            annotateWithAttribute(element, holder, OnjSyntaxHighlighter.KEYWORD_HIGHLIGHTING)

        OnjTypes.FUNCTION_NAME ->
            annotateWithAttribute(element, holder, OnjSyntaxHighlighter.FUNCTION_NAME_HIGHLIGHTING)

        OnjTypes.VARIABLE_USE ->
            annotateWithAttribute(element, holder, OnjSyntaxHighlighter.VARIABLE_NAME_HIGHLIGHTING)

        OnjTypes.VARIABLE_DECL_NAME -> {
            element as OnjVariableDeclNamePsi
            val query = ReferencesSearch.search(element)
            val anyUsages = query.any()
            if (!anyUsages && element.name != "_") {
                val annotationBuilder = holder
                    .newAnnotation(HighlightSeverity.INFORMATION, "Unused variable")
                    .range(element)
                    .highlightType(ProblemHighlightType.LIKE_UNUSED_SYMBOL)
                if (element.parent !is OnjImportStructurePsi) {
                    annotationBuilder
                        .newFix(DeleteUnusedQuickFix(element))
                        .registerFix()
                }
                annotationBuilder.create()
            } else {
                annotateWithAttribute(element, holder, OnjSyntaxHighlighter.VARIABLE_NAME_HIGHLIGHTING)
            }
        }

        OnjTypes.IMPORT_PATH -> {
            val parent = element.parent
            if (parent is OnjImportStructurePsi) {
                val file = parent.resolveToFile()
                if (file != null && !file.exists()) {
                    holder
                        .newAnnotation(HighlightSeverity.ERROR, "File not found")
                        .range(element)
                        .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                        .create()
                }
            }
            Unit
        }

        OnjTypes.NAMED_OBJECT_NAME ->
            annotateWithAttribute(element, holder, OnjSyntaxHighlighter.NAMED_OBJECT_NAME_HIGHLIGHTING)

        OnjTypes.VARIABLE_ACCESSOR ->
            annotateWithAttribute(element, holder, OnjSyntaxHighlighter.VARIABLE_ACCESS_HIGHLIGHTING)

        else -> {}
    }

    private fun annotateWithAttribute(
        element: PsiElement,
        holder: AnnotationHolder,
        attributes: Array<TextAttributesKey>
    ) {
        val annotationBuilder = holder
            .newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element)
        attributes.forEach(annotationBuilder::textAttributes)
        annotationBuilder.create()
    }

}
