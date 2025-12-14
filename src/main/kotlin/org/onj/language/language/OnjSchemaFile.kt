package org.onj.language.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class OnjSchemaFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, OnjSchemaLanguage) {

    override fun getFileType(): FileType {
        return OnjSchemaFileType
    }

}
