package org.onj.language.documentation

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.Nls
import org.onj.language.psi.OnjPsiElementWithDocumentation

class OnjDocumentationProvider : AbstractDocumentationProvider() {

    override fun generateDoc(
        element: PsiElement?,
        originalElement: PsiElement?
    ): @Nls String? {
        return if (element is OnjPsiElementWithDocumentation) element.renderDoc() else null
    }

    override fun generateHoverDoc(
        element: PsiElement,
        originalElement: PsiElement?
    ): @Nls String? = generateDoc(element, originalElement)
}
