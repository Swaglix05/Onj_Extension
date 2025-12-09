package org.onj.language.symbols

import com.intellij.model.psi.PsiSymbolDeclaration
import com.intellij.model.psi.PsiSymbolDeclarationProvider
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
//import org.onj.language.psi.OnjTypes
//
//@Suppress("UnstableApiUsage")
//class OnjSymbolDeclarationProvider : PsiSymbolDeclarationProvider {
//
//    override fun getDeclarations(
//        element: PsiElement,
//        offsetInElement: Int
//    ): MutableCollection<out PsiSymbolDeclaration> {
//
//        if (element !is NavigatablePsiElement) return mutableListOf()
//
//        if (element.elementType == OnjTypes.VARIABLE_EXPRESSION) {
//            return mutableListOf(OnjVariableSymbolDeclaration(element))
//        }
//
//        return mutableListOf()
//    }
//
//}