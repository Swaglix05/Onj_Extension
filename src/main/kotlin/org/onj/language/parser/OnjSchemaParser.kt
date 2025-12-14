package org.onj.language.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import org.onj.language.psi.OnjSchemaTypes

class OnjSchemaParser : PsiParser {

    override fun parse(
        root: IElementType,
        builder: PsiBuilder
    ): ASTNode {
        // TODO: actual parser
        val mark = builder.mark()
        while (!builder.eof()) builder.advanceLexer()
        mark.done(OnjSchemaTypes.FILE)
        return builder.treeBuilt
    }

}
