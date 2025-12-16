package org.onj.language.language

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import org.jetbrains.annotations.NonNls
import org.onj.language.OnjIcons
import javax.swing.Icon

object OnjSchemaFileType : LanguageFileType(OnjSchemaLanguage) {

    override fun getName(): @NonNls String {
        return "OnjSchema File"
    }

    override fun getDescription(): @NlsContexts.Label String {
        return "OnjSchema"
    }

    override fun getDefaultExtension(): @NlsSafe String {
        return "onjschema"
    }

    override fun getIcon(): Icon {
        return OnjIcons.SCHEMA_FILE
    }
}
