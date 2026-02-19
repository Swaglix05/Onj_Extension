package org.onj.language

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import org.onj.language.psi.OnjVariableDeclaringPsiElement

class OnjRefactoringSupportProvider : RefactoringSupportProvider() {

    override fun isInplaceRenameAvailable(
        element: PsiElement,
        context: PsiElement?
    ): Boolean {
        return element is OnjVariableDeclaringPsiElement
    }

    override fun isSafeDeleteAvailable(element: PsiElement): Boolean {
        return element is OnjVariableDeclaringPsiElement
    }
}
