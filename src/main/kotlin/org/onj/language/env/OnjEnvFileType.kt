package org.onj.language.env

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import org.jetbrains.annotations.NonNls
import org.onj.language.OnjIcons
import javax.swing.Icon

object OnjEnvFileType : LanguageFileType(OnjEnvLanguage) {

    override fun getName(): @NonNls String = "OnjEnv"

    override fun getDescription(): @NlsContexts.Label String = "Onj environment file"

    override fun getDefaultExtension(): @NlsSafe String = "onjenv"

    override fun getIcon(): Icon = OnjIcons.FILE

}