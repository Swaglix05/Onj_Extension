package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil
import org.onj.language.language.OnjLanguage
import org.onj.language.psi.OnjCanHaveVariableDeclaration
import org.onj.language.psi.OnjPsiElementWithDocumentation
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils.findInstance

class OnjVarStructurePsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjCanHaveVariableDeclaration, OnjPsiElementWithDocumentation {

    fun simpleDeclarationType(): OnjType {
        return children.findInstance<OnjTypeResolvablePsi>()?.resolveTypeSimple() ?: OnjType.Unknown
    }

    fun fullDeclarationType(): OnjType {
        return children.findInstance<OnjTypeResolvablePsi>()?.resolveTypeFull() ?: OnjType.Unknown
    }

    override fun renderDoc(): String? {
        val varDecl = children.findInstance<OnjVariableDeclNamePsi>() ?: return null
        val type = simpleDeclarationType()
        val builder = StringBuilder()
        builder.append(DocumentationMarkup.DEFINITION_START)
        val text = "var ${varDecl.name}: ${type.printableName}"
        HtmlSyntaxInfoUtil.appendHighlightedByLexerAndEncodedAsHtmlCodeSnippet(builder, project, OnjLanguage, text, 1f)
        builder.append(DocumentationMarkup.DEFINITION_END)
        return builder.toString()
    }
}
