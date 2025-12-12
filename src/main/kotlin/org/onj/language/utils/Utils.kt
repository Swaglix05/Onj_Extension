package org.onj.language.utils

import com.intellij.lang.ASTNode
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import java.nio.file.Path

object Utils {

    fun findContainingContentRoot(psiElement: PsiElement): Path? {
        return findContainingContentRoot(psiElement.containingFile)
    }

    fun findContainingContentRoot(psiFile: PsiFile): Path? {
        val thisFilePath = psiFile.virtualFile.toNioPathOrNull() ?: return null
        var bestFit: Path? = null
        ProjectRootManager.getInstance(psiFile.project).contentRoots.forEach { root ->
            val modulePath = root.toNioPathOrNull() ?: return@forEach
            if (!thisFilePath.startsWith(modulePath)) return@forEach
            if (bestFit == null) {
                bestFit = modulePath
                return@forEach
            }
            if (!modulePath.startsWith(bestFit)) return@forEach
            // more specific path
            bestFit = modulePath
        }
        return bestFit
    }

    inline fun iterateOverAstChildren(astNode: ASTNode, block: (child: ASTNode) -> Unit) {
        var curChild = astNode.firstChildNode
        while (curChild != null) {
            block(curChild)
            curChild = curChild.treeNext
        }
    }

}
