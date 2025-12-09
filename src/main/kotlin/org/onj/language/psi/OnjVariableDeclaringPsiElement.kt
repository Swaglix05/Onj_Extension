package org.onj.language.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.model.psi.PsiSymbolDeclaration
import com.intellij.psi.PsiNameIdentifierOwner

abstract class OnjVariableDeclaringPsiElement(node: ASTNode) : ASTWrapperPsiElement(node), PsiNameIdentifierOwner, PsiSymbolDeclaration
//abstract class OnjVariableDeclaringPsiElement(node: ASTNode) : ASTWrapperPsiElement(node), PsiNameIdentifierOwner
