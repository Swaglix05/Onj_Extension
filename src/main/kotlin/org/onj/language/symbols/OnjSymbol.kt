@file:Suppress("UnstableApiUsage")

package org.onj.language.symbols

import com.intellij.model.Pointer
import com.intellij.model.Symbol
import com.intellij.platform.backend.navigation.NavigationRequest
import com.intellij.platform.backend.navigation.NavigationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.NavigatablePsiElement

class OnjSymbol(val psiElement: NavigatablePsiElement) : Symbol, NavigationTarget {

    override fun createPointer(): Pointer<out OnjSymbol?> {
        return Pointer.hardPointer(this)
    }

    override fun computePresentation(): TargetPresentation {
        return TargetPresentation.builder(psiElement.text).presentation()
    }

    override fun navigationRequest(): NavigationRequest? {
        return psiElement.navigationRequest()
    }

    override fun toString(): String {
        return psiElement.text
    }

    override fun equals(other: Any?): Boolean {
        return other is OnjSymbol && other.psiElement == psiElement
    }
}
