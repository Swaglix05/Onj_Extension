package org.onj.language.quickFixes

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.codeInspection.util.IntentionName
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.onj.language.psi.OnjVariableDeclaringPsiElement

class DeleteUnusedQuickFix(val toDelete: OnjVariableDeclaringPsiElement) : BaseIntentionAction() {

    override fun isAvailable(
        project: Project,
        editor: Editor?,
        psiFile: PsiFile?
    ): Boolean = true

    override fun invoke(
        project: Project,
        editor: Editor?,
        psiFile: PsiFile?
    ) {
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.writeCommandAction(project).run<Throwable> {
                toDelete.delete()
            }
        }
    }

    override fun getText(): @IntentionName String {
        return "Delete"
    }

    override fun getFamilyName(): @IntentionFamilyName String {
        return "Unused variable"
    }


}