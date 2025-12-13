@file:Suppress("UnstableApiUsage")

package org.onj.language.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.ui.Messages
import org.onj.language.language.OnjFileType

class CreateOnjFileAction : AnAction() {

    override fun createTemplatePresentation(): Presentation {
        val presentation = super.createTemplatePresentation()
        presentation.icon = OnjFileType.icon
        return presentation
    }

    override fun actionPerformed(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val name = Messages.showInputDialog("Name for onj file", "New Onj File", Messages.getQuestionIcon())
        WriteAction.compute<Unit, Throwable> {
            val fileName = when {
                name == null -> "file.onj"
                name.endsWith("onj") -> name
                else -> "$name.onj"
            }
            file!!.createChildData(null, fileName)
        }
    }

}
