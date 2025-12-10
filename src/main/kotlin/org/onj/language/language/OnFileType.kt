package org.onj.language.language

import com.intellij.openapi.fileTypes.LanguageFileType
import org.onj.language.OnjIcons
import org.onj.language.language.OnjLanguage
import javax.swing.Icon

object OnjFileType : LanguageFileType(OnjLanguage) {

    override fun getName(): String {
        return "Onj File"
    }

    override fun getDescription(): String {
        return "Onj language file"
    }

    override fun getDefaultExtension(): String {
        return "onj"
    }

    override fun getIcon(): Icon {
        return OnjIcons.FILE
    }
}