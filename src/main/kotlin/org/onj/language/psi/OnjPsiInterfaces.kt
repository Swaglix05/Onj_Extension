@file:Suppress("UnstableApiUsage")

package org.onj.language.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.model.psi.PsiSymbolDeclaration
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner

abstract class OnjVariableDeclaringPsiElement(node: ASTNode) : ASTWrapperPsiElement(node), PsiNameIdentifierOwner, PsiSymbolDeclaration

interface OnjCanHaveVariableDeclaration : PsiElement

interface OnjRenamableReference : PsiElement {
    fun rename(newName: String)
}

interface OnjInStructureView : PsiElement, NavigatablePsiElement
