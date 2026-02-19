package org.onj.language.highlighting

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import org.onj.language.psi.OnjTypes
import org.onj.language.psi.impl.OnjArrayPsi
import org.onj.language.psi.impl.OnjKeyValuePairPsi
import org.onj.language.psi.impl.OnjObjectPsi
import org.onj.language.psi.impl.OnjTripleDotPsi
import org.onj.language.psi.impl.OnjVariableAccessorPsi
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils.findInstance

class OnjTypeBasedAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) = when (element.elementType) {

        OnjTypes.IMPORT_PATH -> annotateImportPath(element, holder)

        OnjTypes.ACCESS -> annotateAccess(element, holder)

        OnjTypes.TRIPLE_DOT -> annotateTripleDot(element, holder)

        OnjTypes.OBJECT -> annotateObject(element, holder)

        OnjTypes.TOP_LEVEL -> annotateObject(element, holder) // top level is essentially like an object

        else -> {}
    }

    private fun annotateObject(element: PsiElement, holder: AnnotationHolder) {
        val seenKeys = mutableSetOf<String>()
        val tripleDots = mutableListOf<OnjTripleDotPsi>()
        element.children.forEach { key ->
            if (key is OnjTripleDotPsi) {
                tripleDots.add(key)
                return@forEach
            }
            if (key !is OnjKeyValuePairPsi) return@forEach
            val keyPsi = key.getKey()
            val keyName = keyPsi.getKeyText(false)
            if (keyName in seenKeys) {
                holder
                    .newAnnotation(HighlightSeverity.ERROR, "Duplicate key")
                    .range(keyPsi)
                    .highlightType(ProblemHighlightType.GENERIC_ERROR)
                    .create()
            }
            seenKeys.add(keyName)
        }
        tripleDots.forEach { tripleDot ->
            val toIncludePsi = tripleDot.children.findInstance<OnjTypeResolvablePsi>() ?: return@forEach
            val toInclude = toIncludePsi.resolveTypeFull()
            if (!toInclude.isSpecific() || !toInclude.isObject()) return@forEach
            toInclude as OnjType.SpecificObject
            toInclude.keys.keys.forEach { key ->
                if (key !in seenKeys) {
                    seenKeys.add(key)
                    return@forEach
                }
                holder
                    .newAnnotation(HighlightSeverity.ERROR, "key '$key' included here was already defined elsewhere")
                    .range(tripleDot)
                    .highlightType(ProblemHighlightType.GENERIC_ERROR)
                    .create()
            }
        }
    }

    private fun annotateTripleDot(element: PsiElement, holder: AnnotationHolder) {
        val toInclude = element.children.findInstance<OnjTypeResolvablePsi>()
            ?: return
        val type = toInclude.resolveTypeSimple()
        val parent = element.parent
        if (parent is OnjArrayPsi && !(type.isArray() || type.isUnknown())) {
            holder
                .newAnnotation(HighlightSeverity.ERROR, "Expression must resolve to an array to be included in an array")
                .range(toInclude)
                .highlightType(ProblemHighlightType.GENERIC_ERROR)
                .create()
        }
        if ((parent is OnjObjectPsi || parent is PsiFile) && !(type.isObject() || type.isUnknown())) {
            holder
                .newAnnotation(HighlightSeverity.ERROR, "Expression must resolve to an object to be included in an object")
                .range(toInclude)
                .highlightType(ProblemHighlightType.GENERIC_ERROR)
                .create()
        }
    }

    private fun annotateAccess(element: PsiElement, holder: AnnotationHolder) {
        val toAccess = element.children.findInstance<OnjTypeResolvablePsi>()
        val accessor = element.children.findInstance<OnjVariableAccessorPsi>()
        if (toAccess == null || accessor == null) return
        val toAccessType = toAccess.resolveTypeSimple()
        val accessorType = accessor.getAccessValue()
        if (toAccessType.isUnknown() || accessorType.isUnknown()) return
        if (toAccessType.isObject() && !accessorType.isString()) {
            holder
                .newAnnotation(HighlightSeverity.ERROR, "Expression must resolve to a string to access an object")
                .range(accessor)
                .highlightType(ProblemHighlightType.GENERIC_ERROR)
                .create()
        }
        if (toAccessType.isArray() && !accessorType.isInt()) {
            holder
                .newAnnotation(HighlightSeverity.ERROR, "Expression must resolve to an int to access an array")
                .range(accessor)
                .highlightType(ProblemHighlightType.GENERIC_ERROR)
                .create()
        }
    }

    private fun annotateImportPath(element: PsiElement, holder: AnnotationHolder) {
        val importExpr = element.children.findInstance<OnjTypeResolvablePsi>() ?: return
        val type = importExpr.resolveTypeSimple()
        if (type.isUnknown() || type.isString()) return
        holder
            .newAnnotation(HighlightSeverity.ERROR, "Expression must resolve to a string")
            .range(element)
            .highlightType(ProblemHighlightType.GENERIC_ERROR)
            .create()
    }
}
