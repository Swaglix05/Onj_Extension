package org.onj.language

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
//import org.onj.language.psi.OnjTypes
//
//class OnjPairedBracesMatcher : PairedBraceMatcher {
//
//    override fun getPairs(): Array<BracePair> = arrayOf(
//        BracePair(OnjTypes.L_PAREN, OnjTypes.R_PAREN, false),
//        BracePair(OnjTypes.L_BRACE, OnjTypes.R_BRACE, true),
//        BracePair(OnjTypes.L_BRACKET, OnjTypes.R_BRACKET, true)
//    )
//
//    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean {
//        return true
//    }
//
//    override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int): Int {
//        return openingBraceOffset
//    }
//}