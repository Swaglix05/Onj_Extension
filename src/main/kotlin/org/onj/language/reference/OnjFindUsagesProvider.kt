package org.onj.language.reference

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.lexer.FlexAdapter
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.onj.language.OnjLexer
import org.onj.language.psi.OnjTokenSets
import org.onj.language.psi.OnjVariableDeclaringPsiElement
import org.onj.language.psi.impl.OnjKeyPsi

class OnjFindUsagesProvider : FindUsagesProvider {

    override fun canFindUsagesFor(p0: PsiElement): Boolean {
        return p0 is OnjVariableDeclaringPsiElement || p0 is OnjKeyPsi
    }

    override fun getHelpId(p0: PsiElement): @NonNls String? = null

    override fun getType(p0: PsiElement): @Nls String = when(p0){
        is OnjVariableDeclaringPsiElement -> "Local Variable"
        is OnjKeyPsi -> "Object Key"
        else -> ""
    }

    override fun getDescriptiveName(p0: PsiElement): @Nls String = when (p0) { //= (p0 as OnjVariableDeclaringPsiElement).name.toString()
        is OnjVariableDeclaringPsiElement -> p0.name.toString()
        is OnjKeyPsi -> p0.getKeyText(true)
        else -> ""
    }

    override fun getNodeText(
        p0: PsiElement,
        p1: Boolean
    ): @Nls String = (p0.text)

    override fun getWordsScanner(): WordsScanner {
        return DefaultWordsScanner(
            object : FlexAdapter(OnjLexer(null)) {},
            OnjTokenSets.possibleUsage, OnjTokenSets.comments,
            OnjTokenSets.strings
        )
    }
}
