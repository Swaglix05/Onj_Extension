package org.onj.language.rename

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import org.onj.language.language.OnjFile
import org.onj.language.language.OnjFileType
import org.onj.language.psi.OnjTypes

object OnjElementFactory {

    fun createOnjIdentifier(project: Project, name: String): PsiElement {
        val text = "k: $name"
        val file = createFile(project, text)
        return file
            .node
            .findChildByType(OnjTypes.KEY_VALUE_PAIR)!!
            .findChildByType(OnjTypes.VARIABLE_USE)!!
            .findChildByType(OnjTypes.IDENTIFIER)!!
            .psi
    }

    fun createOnjVariableDeclaration(project: Project, name: String): PsiElement {
        val text = "var $name = 0;"
        val file = createFile(project, text)
        return file
            .node
            .findChildByType(OnjTypes.VAR_STRUCTURE)!!
            .findChildByType(OnjTypes.VARIABLE_DECL_NAME)!!
            .psi
    }

    fun createFile(project: Project, text: String): OnjFile {
        val name = "dummy.onj"
        return PsiFileFactory.getInstance(project).createFileFromText(name, OnjFileType, text) as OnjFile
    }
}