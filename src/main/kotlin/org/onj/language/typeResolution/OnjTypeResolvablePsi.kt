package org.onj.language.typeResolution

import com.intellij.psi.PsiElement

interface OnjTypeResolvablePsi : PsiElement {

    fun resolveTypeSimple(): OnjType

    fun resolveTypeFull(): OnjType

}
