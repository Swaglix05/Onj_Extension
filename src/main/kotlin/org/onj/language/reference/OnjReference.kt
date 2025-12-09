package org.onj.language.reference

//import com.intellij.codeInsight.lookup.LookupElement
//import com.intellij.codeInsight.lookup.LookupElementBuilder
//import com.intellij.openapi.util.TextRange
//import com.intellij.psi.PsiElement
//import com.intellij.psi.PsiElementResolveResult
//import com.intellij.psi.PsiPolyVariantReference
//import com.intellij.psi.PsiReferenceBase
//import com.intellij.psi.ResolveResult
//import com.intellij.psi.util.elementType
//import org.onj.language.OnjIcons
//import org.onj.language.psi.OnjFile
//import org.onj.language.psi.OnjNamedElement
//import org.onj.language.utils.OnjUtil
//
//class OnjReference(element: OnjNamedElement, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange),
//    PsiPolyVariantReference {
//
//    private val name: String = element.text.substring(textRange.startOffset, textRange.endOffset)
//
//    override fun resolve(): PsiElement? {
//        val results = resolveSingle { it.elementType != element.elementType }
//        return if (results.size == 1) return results[0]!!.element else null
//    }
//
//    private fun resolveSingle(check: (PsiElement) -> Boolean): Array<out ResolveResult?> {
//        val project = myElement.project
//        val properties = OnjUtil.findNamedElementDeclarations(project, name, myElement.containingFile as OnjFile, check)
//        val results: MutableList<ResolveResult> = mutableListOf()
//        for (property in properties) {
//            results.add(PsiElementResolveResult(property))
//        }
//        return results.toTypedArray()
//    }
//
//    override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult?> {
//        val res = resolveSingle { it.elementType != element.elementType }
//        return res
//    }
//
//    override fun getVariants(): Array<out Any?> {
//        println("get variants called")
//        val project = myElement.project
//        val properties = OnjUtil.findNamedElementDeclarations(project)
//        val variants: MutableList<LookupElement> = mutableListOf()
//        for (property in properties) {
//            if (property.name != null) {
//                variants.add(
//                    LookupElementBuilder
//                        .create(property).withIcon(OnjIcons.FILE)
//                        .withTypeText(property.containingFile.name)
//                )
//            }
//        }
//        return variants.toTypedArray()
//    }
//}