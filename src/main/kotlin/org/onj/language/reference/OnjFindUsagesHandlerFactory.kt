package org.onj.language.reference

import com.intellij.find.findUsages.FindUsagesHandler
import com.intellij.find.findUsages.FindUsagesHandlerFactory
import com.intellij.find.findUsages.FindUsagesOptions
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.usageView.UsageInfo
import com.intellij.util.Processor
import org.onj.language.psi.OnjVariableDeclaringPsiElement


/**
 * this whole file exists to restict the "Find Usages" Part to the current file only
 */
class OnjFindUsagesHandlerFactory : FindUsagesHandlerFactory() {

    override fun canFindUsages(p0: PsiElement): Boolean {
        println("canFindUsages from OnjFindUsagesHandlerFactory")
        return p0 is OnjVariableDeclaringPsiElement
    }

    override fun createFindUsagesHandler(
        p0: PsiElement,
        p1: Boolean
    ): FindUsagesHandler {
        return OnjFindUsageHandler(p0)
    }

}

class OnjFindUsageHandler(psiElement: PsiElement) : FindUsagesHandler(psiElement) {

    override fun processElementUsages(
        element: PsiElement,
        processor: Processor<in UsageInfo>,
        options: FindUsagesOptions
    ): Boolean {
        println("hiiiiiiiiiiii")
        return super.processElementUsages(element, processor, options)
    }

    override fun getFindUsagesOptions(): FindUsagesOptions {
        return OnjFindUsagesOptions(this.project, psiElement)
    }
}

class OnjFindUsagesOptions(project: Project, psiElement: PsiElement) : FindUsagesOptions(project) {
    init {
        this.searchScope = GlobalSearchScope.fileScope(project, psiElement.containingFile.virtualFile)
    }
}