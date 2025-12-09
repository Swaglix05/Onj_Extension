@file:Suppress("UnstableApiUsage")

package org.onj.language.reference

import com.intellij.lang.tree.util.children
import com.intellij.model.Pointer
import com.intellij.model.SingleTargetReference
import com.intellij.model.Symbol
import com.intellij.openapi.util.TextRange
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.elementType
import org.onj.language.psi.OnjTypes
import org.onj.language.psi.OnjVariableDeclaringPsiElement
import org.onj.language.psi.impl.OnjVarStructurePsi

class OnjSymbol(val file: PsiFile, val name: String) : Symbol {

    override fun createPointer(): Pointer<out Symbol?> {
        return Pointer.fileRangePointer(file, file.textRange) { file, _ ->
            OnjSymbol(file, name)
        }
    }



}

//class OnjSymbolReference(val element: PsiElement) : SingleTargetReference() {
//
//    override fun resolveSingleTarget(): Symbol? {
//        val referencedVariable = element.text
//
//        element.containingFile.children.forEach { element ->
//
//            if (element.elementType != OnjTypes.TOP_LEVEL_STRUCTURE) return@forEach
//            val variableElement = element.children[0]
//
//            if (variableElement.elementType != OnjTypes.VARIABLE_STRUCTURE && variableElement.elementType != OnjTypes.IMPORT_STRUCTURE) return@forEach
//
//            val name = variableElement.children.find { it.elementType == OnjTypes.VARIABLE_DECLARATION_NAME }!!
//
//            if (!name.textMatches(referencedVariable)) return@forEach
//
//            return mutableListOf(
//                if (variableElement is OnjVariableStructureImpl) {
//                    variableElement.symbolDeclaration.symbol
//                } else {
//                    (variableElement as OnjImportStructureImpl).symbolDeclaration.symbol
//                }
//            )
//        }
//        return mutableListOf()
//    }
//
//}

class OnjReference(element: PsiElement, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange) {

    private val name: String = element.text

    override fun resolve(): PsiElement? {
        return element
            .containingFile
            .node
            .findChildByType(OnjTypes.FILE)
            ?.children()
            ?.mapNotNull { it.psi }
            ?.filterIsInstance<OnjVariableDeclaringPsiElement>()
            ?.filter { it.nameIdentifier?.textMatches(name) ?: false }
            ?.firstOrNull()
    }


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
}