@file:Suppress("UnstableApiUsage")

package org.onj.language.symbols

import com.intellij.model.Symbol
import com.intellij.psi.PsiElement

interface OnjPsiElementBasedSymbol : Symbol {

    val psiElement: PsiElement

}
