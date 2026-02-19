package org.onj.language.env

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import org.onj.language.psi.OnjTypes

class OnjEnvParser : PsiParser {

    override fun parse(
        root: IElementType,
        builder: PsiBuilder
    ): ASTNode {
        val mark = builder.mark()
        val mark2 = builder.mark()
        while (!builder.eof()) builder.advanceLexer()
        mark2.done(OnjTypes.FILE)
        mark.done(OnjTypes.FILE)
        return builder.treeBuilt
    }

}
