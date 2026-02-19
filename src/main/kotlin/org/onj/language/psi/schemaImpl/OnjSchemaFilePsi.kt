package org.onj.language.psi.schemaImpl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.NlsSafe
import org.onj.language.psi.OnjInStructureView
import javax.swing.Icon

class OnjSchemaFilePsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjInStructureView
