package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.psi.NavigatablePsiElement
import org.jetbrains.annotations.Unmodifiable
import org.onj.language.symbols.OnjSymbolReference
import java.util.Collections

class OnjVariableUsePsi(node: ASTNode) : ASTWrapperPsiElement(node), NavigatablePsiElement {

    override fun getOwnReferences(): @Unmodifiable Collection<out PsiSymbolReference> {
        return Collections.singletonList(OnjSymbolReference(this))
    }

}
