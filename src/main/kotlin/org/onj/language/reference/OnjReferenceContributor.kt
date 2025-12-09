package org.onj.language.reference

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.util.ProcessingContext
//import org.onj.language.psi.OnjTypes
//import org.onj.language.psi.OnjNamedElement
//
//class OnjReferenceContributor : PsiReferenceContributor() {
//    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
//        registrar.registerReferenceProvider(
//            OnjLanguagePatterns.variableExpressionNames(),
//            object : PsiReferenceProvider() {
//                override fun getReferencesByElement(
//                    element: PsiElement,
//                    context: ProcessingContext
//                ): Array<PsiReference> {
//                    element as OnjNamedElement
//                    return arrayOf(OnjReference(element, TextRange(0, element.text.length)))
//                }
//            })
//    }
//}
//
//object OnjLanguagePatterns {
//    fun variableExpressionNames(): PsiElementPattern.Capture<PsiElement> {
//        return PlatformPatterns.psiElement(OnjTypes.VARIABLE_EXPRESSION)
//    }
//}