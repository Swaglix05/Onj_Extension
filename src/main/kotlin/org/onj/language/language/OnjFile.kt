package org.onj.language.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import org.onj.language.env.OnjEnvFile

class OnjFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, OnjLanguage) {

    private val LOCK = Any()

    private var _envFile: OnjEnvFile? = null
    private var cachedValueValid: Boolean = false

    private fun envFileLookup(): OnjEnvFile? {
        // very smart way of selecting the correct file
        val file = FilenameIndex.getAllFilesByExt(project, "onjenv").minByOrNull { it.path.length }
            ?: return null
        val psiFile = PsiManager.getInstance(project).findFile(file)
        if (psiFile !is OnjEnvFile) return null
        return psiFile
    }

    fun getEnvFile(): OnjEnvFile? {
        synchronized(LOCK) {
            if (cachedValueValid) return _envFile
            _envFile = envFileLookup()
            cachedValueValid = true
            return _envFile
        }
    }

    fun envFileCacheNoLongerValid() {
        cachedValueValid = false
    }

    override fun getFileType(): FileType {
        return OnjFileType
    }

    override fun toString(): String {
        return "Onj File"
    }
}
