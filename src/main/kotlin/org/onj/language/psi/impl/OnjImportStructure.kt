package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.onj.language.psi.OnjCanHaveVariableDeclaration
import org.onj.language.psi.OnjTypes
import java.io.File
import kotlin.io.path.Path

class OnjImportStructurePsi(node: ASTNode) : ASTWrapperPsiElement(node), OnjCanHaveVariableDeclaration {

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
        return containingFile?.virtualFile?.canonicalPath?.let { Path(it).parent.resolve(path).toFile() }
    }

}

class OnjAsContextDependentKeywordPsi(node: ASTNode) : ASTWrapperPsiElement(node)
class OnjImportPathPsi(node: ASTNode) : ASTWrapperPsiElement(node)
