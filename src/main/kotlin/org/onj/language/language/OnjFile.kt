package org.onj.language.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import org.onj.language.language.OnjLanguage

class OnjFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, OnjLanguage) {
    override fun getFileType(): FileType {
        return OnjFileType
    }

    override fun toString(): String {
        return "Onj File"
    }
}