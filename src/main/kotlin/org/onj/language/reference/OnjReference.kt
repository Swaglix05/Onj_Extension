@file:Suppress("UnstableApiUsage")

package org.onj.language.reference

import com.intellij.lang.tree.util.children
import com.intellij.model.Pointer
import com.intellij.model.SingleTargetReference
import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.navigation.NavigatableSymbol
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.platform.backend.navigation.NavigationRequest
import com.intellij.platform.backend.navigation.NavigationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiReferenceBase
import org.jetbrains.annotations.Unmodifiable
import org.onj.language.psi.OnjTypes
import org.onj.language.psi.OnjVariableDeclaringPsiElement

class OnjSymbol(val psiElement: NavigatablePsiElement) : Symbol, NavigationTarget {

    init {
        println("OnjSymbol") }

    override fun createPointer(): Pointer<out OnjSymbol?> {
        return Pointer.hardPointer(this)
    }

    override fun computePresentation(): TargetPresentation {
        return TargetPresentation.builder(psiElement.text).presentation()
    }

    override fun navigationRequest(): NavigationRequest? {
        return psiElement.navigationRequest()
    }

//    override fun getNavigationTargets(p0: Project): @Unmodifiable Collection<NavigationTarget?> {
//        return mutableListOf(this)
//    }

    override fun toString(): String {
        return psiElement.text
    }

    override fun equals(other: Any?): Boolean {
        println(this)
        println(other)
        return other is OnjSymbol && other.psiElement == psiElement
    }
}

class OnjSymbolReference(val referencingElement: PsiElement) : SingleTargetReference(), PsiSymbolReference {

    init {
        println("OnjSymbolReference") }

    override fun resolveSingleTarget(): Symbol? {
        println("resolveSingleTarget")
        val referencedVariable = referencingElement.text

        referencingElement.containingFile.children.forEach { element ->

            if (element !is OnjVariableDeclaringPsiElement) return@forEach

            if (!(element.nameIdentifier?.textMatches(referencedVariable) ?: false)) return@forEach

            return element.symbol
        }
        return null
    }

    override fun getElement(): PsiElement = referencingElement.also { println("getElement from Reference") }

    override fun getRangeInElement(): TextRange = TextRange(0, referencingElement.textLength)

}

//class OnjReference(element: PsiElement, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange) {
//
//    private val name: String = element.text
//
//    override fun resolve(): PsiElement? {
//        println("resolve in normal reference!!!")
//        return element
//            .containingFile
//            .node
//            .findChildByType(OnjTypes.FILE)
//            ?.children()
//            ?.mapNotNull { it.psi }
//            ?.filterIsInstance<OnjVariableDeclaringPsiElement>()
//            ?.filter { it.nameIdentifier?.textMatches(name) ?: false }
//            ?.firstOrNull()
//    }
//
//
////    override fun resolve(): PsiElement? {
////        val results = resolveSingle { it.elementType != element.elementType }
////        return if (results.size == 1) return results[0]!!.element else null
////    }
////
////    private fun resolveSingle(check: (PsiElement) -> Boolean): Array<out ResolveResult?> {
////        val project = myElement.project
////        val properties = OnjUtil.findNamedElementDeclarations(project, name, myElement.containingFile as OnjFile, check)
////        val results: MutableList<ResolveResult> = mutableListOf()
////        for (property in properties) {
////            results.add(PsiElementResolveResult(property))
////        }
////        return results.toTypedArray()
////    }
////
////
////    override fun getVariants(): Array<out Any?> {
////        println("get variants called")
////        val project = myElement.project
////        val properties = OnjUtil.findNamedElementDeclarations(project)
////        val variants: MutableList<LookupElement> = mutableListOf()
////        for (property in properties) {
////            if (property.name != null) {
////                variants.add(
////                    LookupElementBuilder
////                        .create(property).withIcon(OnjIcons.FILE)
////                        .withTypeText(property.containingFile.name)
////                )
////            }
////        }
////        return variants.toTypedArray()
////    }
//}