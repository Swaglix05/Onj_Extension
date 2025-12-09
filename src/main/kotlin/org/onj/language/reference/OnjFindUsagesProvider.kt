package org.onj.language.reference

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.lexer.FlexAdapter
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.onj.language.OnjLexer
import org.onj.language.psi.OnjTokenSets
//import org.onj.language.psi.OnjVariableDeclarationName
//import org.onj.language.psi.OnjNamedElement
//
//class OnjFindUsagesProvider : FindUsagesProvider {
//
//    override fun canFindUsagesFor(p0: PsiElement): Boolean = p0 is OnjNamedElement
//
//    override fun getHelpId(p0: PsiElement): @NonNls String? = null
//
//    override fun getType(p0: PsiElement): @Nls String = when(p0){
//        is OnjVariableDeclarationName -> "Local Variable"
//        else -> ""
//    }
//
//    override fun getDescriptiveName(p0: PsiElement): @Nls String = (p0 as OnjNamedElement).name.toString()
//
//    override fun getNodeText(
//        p0: PsiElement,
//        p1: Boolean
//    ): @Nls String = (p0.text)
//
//
//    override fun getWordsScanner(): WordsScanner? {
//        return DefaultWordsScanner(object : FlexAdapter(OnjLexer(null)) { }, OnjTokenSets.calledNames, OnjTokenSets.comments,TokenSet.EMPTY)
//    }
//}