package org.onj.language.highlighting

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.onj.language.psi.OnjTypes
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils.findInstance

class OnjTypeBasedAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) = when (element.elementType) {

        OnjTypes.IMPORT_PATH -> {
            val importExpr = element.children.findInstance<OnjTypeResolvablePsi>()
            if (importExpr != null) {
                val type = importExpr.resolveTypeSimple()
                if (!type.isUnknown() && !type.isString()) {
                    holder
                        .newAnnotation(HighlightSeverity.ERROR, "Expression must resolve to a string")
                        .range(element)
                        .highlightType(ProblemHighlightType.GENERIC_ERROR)
                        .create()
                }
            }
            Unit
        }

        else -> {}
    }
}
