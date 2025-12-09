package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import org.onj.language.psi.OnjTypes

class OnjVariableDeclNamePsi(node: ASTNode) : ASTWrapperPsiElement(node)