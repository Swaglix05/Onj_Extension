@file:Suppress("UnstableApiUsage")

package org.onj.language

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
//import org.onj.language.psi.OnjTypes
//import org.onj.language.psi.OnjVariableExpression
//
//class OnjVariableSymbolReference(
//    private val referencingPsiElement: OnjVariableExpression
//) : PsiSymbolReference {
//
//    override fun getElement(): PsiElement = referencingPsiElement
//
//    override fun getRangeInElement(): TextRange = referencingPsiElement.textRangeInParent
//
//    override fun resolveReference(): MutableCollection<out Symbol> {
//        val referencedVariable = referencingPsiElement.text
//
//        referencingPsiElement.containingFile.children.forEach { element ->
//
//            if (element.elementType != OnjTypes.TOP_LEVEL_STRUCTURE) return@forEach
//            val variableElement = element.children[0]
//
//            if (
//                variableElement.elementType != OnjTypes.VARIABLE_STRUCTURE &&
//                variableElement.elementType != OnjTypes.IMPORT_STRUCTURE
//            ) return@forEach
//
////            val name = variableElement.children.find { it.elementType == OnjTypes.VARIABLE_DECLARATION_NAME }!!
////
////            if (!name.textMatches(referencedVariable)) return@forEach
//
//            return mutableListOf(
////                if (variableElement is OnjVariableStructureImpl) {
////                    variableElement.symbolDeclaration.symbol
////                } else {
////                    (variableElement as OnjImportStructureImpl).symbolDeclaration.symbol
////                }
//            )
//        }
//        return mutableListOf()
//    }
//
//}
