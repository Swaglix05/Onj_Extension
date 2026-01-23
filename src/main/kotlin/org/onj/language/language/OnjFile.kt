package org.onj.language.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import org.onj.language.env.OnjEnvFile

class OnjFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, OnjLanguage) {

    private val _envFile: OnjEnvFile? by lazy { envFileLookup() }

    private fun envFileLookup(): OnjEnvFile? {
        // very smart way of selecting the correct file
        val file = FilenameIndex.getAllFilesByExt(project, "onjenv").minByOrNull { it.path.length }
            ?: return null
        val psiFile = PsiManager.getInstance(project).findFile(file)
        if (psiFile !is OnjEnvFile) return null
        return psiFile
    }

    fun getEnvFile(): OnjEnvFile? = _envFile

    override fun getFileType(): FileType {
        return OnjFileType
    }

    override fun toString(): String {
        return "Onj File"
    }
}
