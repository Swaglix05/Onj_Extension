package org.onj.language.highlighting

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.onj.language.psi.impl.OnjTypes

class OnjAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) = when (element.elementType) {

        OnjTypes.KEY ->
            annotateWithAttribute(element, holder, OnjSyntaxHighlighter.KEY_HIGHLIGHTING)

        OnjTypes.AS_CONTEXT_DEPENDENT_KEYWORD ->
            annotateWithAttribute(element, holder, OnjSyntaxHighlighter.KEYWORD_HIGHLIGHTING)

        OnjTypes.FUNCTION_NAME ->
            annotateWithAttribute(element, holder, OnjSyntaxHighlighter.FUNCTION_NAME_HIGHLIGHTING)

        OnjTypes.CONVERSION_NAME ->
            annotateWithAttribute(element, holder, OnjSyntaxHighlighter.FUNCTION_NAME_HIGHLIGHTING)

        OnjTypes.VARIABLE_USE ->
            annotateWithAttribute(element, holder, OnjSyntaxHighlighter.VARIABLE_NAME_HIGHLIGHTING)

        OnjTypes.VARIABLE_DECL_NAME ->
            annotateWithAttribute(element, holder, OnjSyntaxHighlighter.VARIABLE_NAME_HIGHLIGHTING)

        OnjTypes.NAMED_OBJECT_NAME ->
            annotateWithAttribute(element, holder, OnjSyntaxHighlighter.NAMED_OBJECT_NAME_HIGHLIGHTING)

        OnjTypes.VARIABLE_ACCESSOR ->
            annotateWithAttribute(element, holder, OnjSyntaxHighlighter.VARIABLE_ACCESS_HIGHLIGHTING)

        else -> {}
    }

//    private fun annotateVariableCall(
//        element: PsiElement,
//        holder: AnnotationHolder,
//        attributes: Array<TextAttributesKey>
//    ) {
//        if (element !is OnjNamedElement) {
//            annotateWithAttribute(element, holder, attributes)
//            return
//        }
//
//        val key = element.name
//        if (key == null || key == "true" || key == "false") {
//            annotateWithAttribute(element, holder, attributes)
//            return
//        }
//        val properties = OnjUtil.findNamedElementDeclarations(element.project, key, element.containingFile as OnjFile)
//            .filterIsInstance<OnjVariableDeclarationName>()
//        if (properties.isEmpty()) {
//           val newAnnot = holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Unresolved property, hopefully defined in a Namespace") //TODO this should be an error, but namespace support doesn't work yet
//                .range(element)
//                // ** Tutorial step 19. - Add a quick fix for the string containing possible properties
////                .withFix(SimpleCreatePropertyQuickFix (key))
//            attributes.forEach(newAnnot::textAttributes)
//            newAnnot.create()
//        } else {
//            val annotationBuilder2 = holder
//                .newAnnotation(HighlightSeverity.INFORMATION, properties.first().parent.text)
//                .range(element)
//            attributes.forEach(annotationBuilder2::textAttributes)
//            annotationBuilder2.create()
//        }
//    }
//
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
