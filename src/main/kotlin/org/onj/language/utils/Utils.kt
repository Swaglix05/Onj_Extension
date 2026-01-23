package org.onj.language.utils

import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import org.onj.language.env.OnjEnvFile
import java.nio.file.Path

object Utils {

    fun findContainingContentRoot(psiElement: PsiElement): Path? {
        return findContainingContentRoot(psiElement.containingFile)
    }

//    fun findEnvFile(project: Project): OnjEnvFile? {
//        // very smart way of selecting the correct file
//        val file = FilenameIndex.getAllFilesByExt(project, "onjenv").minByOrNull { it.path.length }
//            ?: return null
//        val psiFile = PsiManager.getInstance(project).findFile(file)
//        if (psiFile !is OnjEnvFile) return null
//        return psiFile
//    }

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

    inline fun <reified T> Iterable<*>.findInstance(): T? {
        forEach { if (it is T) return it }
        return null
    }

    inline fun <reified T> Array<*>.findInstance(): T? {
        forEach { if (it is T) return it }
        return null
    }

}
