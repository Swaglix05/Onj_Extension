package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.psi.NavigatablePsiElement
import org.jetbrains.annotations.Unmodifiable
import org.onj.language.reference.OnjSymbolReference
import java.util.Collections

class OnjVariableUsePsi(node: ASTNode) : ASTWrapperPsiElement(node), NavigatablePsiElement {

//    override fun getReference(): PsiReference {
//        return OnjReference(this, textRange)
//    }

    override fun getOwnReferences(): @Unmodifiable Collection<out PsiSymbolReference> {
        println("getOwnReferences")
        return Collections.singletonList(OnjSymbolReference(this))
    }


}
