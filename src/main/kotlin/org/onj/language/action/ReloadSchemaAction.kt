@file:Suppress("UnstableApiUsage")

package org.onj.language.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import org.onj.language.env.OnjEnvFile
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
        val project = e.project ?: return
        val psiManager = PsiManager.getInstance(project)
        val schemaFiles = FilenameIndex.getAllFilesByExt(project, "onjschema")
        schemaFiles.forEach { file ->
            val psiFile = psiManager.findFile(file)
            if (psiFile !is OnjSchemaFile) return@forEach
            psiFile.clearSchema()
        }
        val onjFiles = FilenameIndex.getAllFilesByExt(project, "onj")
        onjFiles.forEach { file ->
            val psiFile = psiManager.findFile(file)
            if (psiFile !is OnjFile) return@forEach
            psiFile.envFileCacheNoLongerValid()
        }
        val onjEnvFiles = FilenameIndex.getAllFilesByExt(project, "onjenv")
        onjEnvFiles.forEach { file ->
            val psiFile = psiManager.findFile(file)
            if (psiFile !is OnjEnvFile) return@forEach
            psiFile.clearCachedEnvModel()
        }
    }

}