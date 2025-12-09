package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

class OnjImportStructurePsi(node: ASTNode) : ASTWrapperPsiElement(node)
class OnjAsContextDependentKeywordPsi(node: ASTNode) : ASTWrapperPsiElement(node)
