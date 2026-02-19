package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.toNioPathOrNull
import org.onj.language.language.OnjLanguage
import org.onj.language.psi.OnjCanHaveVariableDeclaration
import org.onj.language.psi.OnjElementType
import org.onj.language.psi.OnjPsiElementWithDocumentation
import org.onj.language.psi.OnjTypes
import org.onj.language.rename.OnjElementFactory
import org.onj.language.utils.Utils
import org.onj.language.utils.Utils.findInstance
import java.io.File
import java.nio.file.Path

class OnjImportStructurePsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjCanHaveVariableDeclaration, OnjPsiElementWithDocumentation {

    fun getImportedPath(): String? {
        val strPsi = node
            .findChildByType(OnjTypes.IMPORT_PATH)
            ?.findChildByType(OnjTypes.STRING)
            ?.psi
        if (strPsi !is OnjStringPsi) return null
        return strPsi.literalString()
    }

    fun resolveToFile(): File? {
        val path = getImportedPath() ?: return null
        val rootPath = Utils.findContainingContentRoot(this) ?: return null
        return rootPath.resolve(path).toFile()
    }

    override fun renderDoc(): String? {
        val varDecl = children.findInstance<OnjVariableDeclNamePsi>() ?: return null
        val path = getImportedPath()
        val builder = StringBuilder()
        builder.append(DocumentationMarkup.DEFINITION_START)
        val text = "import ${if (path != null) "\"$path\"" else "?" } as ${varDecl.name}"
        HtmlSyntaxInfoUtil.appendHighlightedByLexerAndEncodedAsHtmlCodeSnippet(builder, project, OnjLanguage, text, 1f)
        builder.append(DocumentationMarkup.DEFINITION_END)
        return builder.toString()
    }
}

class OnjAsContextDependentKeywordPsi(node: ASTNode) : ASTWrapperPsiElement(node)

class OnjImportPathPsi(node: ASTNode) : ASTWrapperPsiElement(node) {

    fun replacePathString(newPath: String) {
        val currentPathString = node
            .findChildByType(OnjTypes.STRING)
            ?: throw RuntimeException("Cant replace path of onj import if it is not a simple string literal")
        val newPathString = OnjElementFactory.createOnjString(project, newPath)
        node.replaceChild(currentPathString, newPathString.node)
    }

}
