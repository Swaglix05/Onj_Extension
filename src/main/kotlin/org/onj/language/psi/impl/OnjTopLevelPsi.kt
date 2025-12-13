package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.onj.language.psi.OnjCanHaveVariableDeclaration

class OnjTopLevelPsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjCanHaveVariableDeclaration
