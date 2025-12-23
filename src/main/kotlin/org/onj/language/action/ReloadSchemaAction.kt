@file:Suppress("UnstableApiUsage")

package org.onj.language.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.vfs.findPsiFile
import org.onj.language.language.OnjFile
import org.onj.language.language.OnjFileType
import org.onj.language.language.OnjSchemaFile
import org.onj.language.language.OnjSchemaFileType
import org.onj.language.psi.impl.OnjTopLevelPsi
import org.onj.language.utils.Utils
import org.onj.language.utils.Utils.findInstance
import kotlin.io.path.Path
import kotlin.io.path.pathString

class ReloadSchemaAction : AnAction() {

    override fun createTemplatePresentation(): Presentation {
        val presentation = super.createTemplatePresentation()
        presentation.icon = OnjFileType.icon
        return presentation
    }

    override fun actionPerformed(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val project = e.project ?: return
        val psiFile = file.findPsiFile(project)
        if (psiFile is OnjSchemaFile) {
            psiFile.clearSchema()
        }
        if (psiFile !is OnjFile) return
        val topLevel = psiFile.children.findInstance<OnjTopLevelPsi>() ?: return
        val schemaPath = topLevel.findSchemaPath() ?: return
        val root = Utils.findContainingContentRoot(topLevel.containingFile) ?: return
        val path = root.resolve(Path(schemaPath))
        val virtualFile = psiFile.containingFile.virtualFile.fileSystem.findFileByPath(path.pathString)
        if (virtualFile == null || !virtualFile.exists() || virtualFile.fileType != OnjSchemaFileType) {
            return
        }
        val schemaFile = virtualFile.findPsiFile(project) as? OnjSchemaFile ?: return
        schemaFile.clearSchema()
    }

}