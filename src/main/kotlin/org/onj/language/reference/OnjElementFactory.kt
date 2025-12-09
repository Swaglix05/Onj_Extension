package org.onj.language.reference

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import org.onj.language.OnjFileType
import org.onj.language.psi.OnjFile
//import org.onj.language.psi.OnjNamedElement
//
//object OnjElementFactory {
//
//    fun createOnjVariableStructure(project: Project, name: String): OnjNamedElement {
//        val file: OnjFile = createFile(project, name)
//        return file.firstChild as OnjNamedElement
//    }
//
//    fun createFile(project: Project, text: String): OnjFile {
//        val name = "dummy.onj"
//        return PsiFileFactory.getInstance(project).createFileFromText(name, OnjFileType, text) as OnjFile;
//    }
//}