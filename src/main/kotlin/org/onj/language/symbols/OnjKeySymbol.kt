@file:Suppress("UnstableApiUsage")

package org.onj.language.symbols

import com.intellij.model.Pointer
import com.intellij.model.Symbol
import com.intellij.platform.backend.navigation.NavigationRequest
import com.intellij.platform.backend.navigation.NavigationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.PsiElement
import org.onj.language.psi.impl.OnjKeyPsi

class OnjKeySymbol(
    override val psiElement: OnjKeyPsi
) : Symbol, OnjPsiElementBasedSymbol, NavigationTarget {

    override fun createPointer(): Pointer<OnjKeySymbol> = Pointer.hardPointer(this)

    override fun computePresentation(): TargetPresentation {
        return TargetPresentation.builder(psiElement.text).presentation()
    }

    override fun navigationRequest(): NavigationRequest? = psiElement.navigationRequest()

    override fun toString(): String {
        return psiElement.text
    }

    override fun equals(other: Any?): Boolean {
        return other is OnjVariableSymbol && other.psiElement == psiElement
    }
}