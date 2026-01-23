@file:Suppress("UnstableApiUsage")

package org.onj.language.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.model.psi.PsiSymbolDeclaration
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import org.onj.language.env.OnjFunctionModel
import org.onj.language.typeResolution.OnjTypeResolvablePsi

abstract class OnjVariableDeclaringPsiElement(node: ASTNode) : ASTWrapperPsiElement(node), PsiNameIdentifierOwner, PsiSymbolDeclaration

interface OnjCanHaveVariableDeclaration : PsiElement

interface OnjPsiElementWithDocumentation : PsiElement {
    fun renderDoc(): String?

}

interface OnjRenamableReference : PsiElement {
    fun rename(newName: String)
}

interface OnjInStructureView : PsiElement, NavigatablePsiElement

interface OnjFunctionLikePsiElement : PsiElement, OnjTypeResolvablePsi {

    fun resolvableName(): String?
    fun printableName(): String?
    fun findNameIdentifier(): PsiElement?
    fun resolveFunction(): OnjFunctionModel?
    fun findParameters(): List<OnjTypeResolvablePsi>
}
