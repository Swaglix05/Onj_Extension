package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

class OnjStringPsi(node: ASTNode) : ASTWrapperPsiElement(node)
class OnjFloatPsi(node: ASTNode) : ASTWrapperPsiElement(node)
class OnjIntPsi(node: ASTNode) : ASTWrapperPsiElement(node)
