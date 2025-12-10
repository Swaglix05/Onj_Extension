package org.onj.language

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import org.onj.language.psi.OnjTypes
import org.onj.language.utils.Utils.iterateOverAstChildren

class OnjFoldingDescriptor : FoldingBuilder, DumbAware {

    override fun buildFoldRegions(node: ASTNode, document: Document): Array<FoldingDescriptor> {
        val foldRegions = mutableListOf<FoldingDescriptor>()
        buildFoldRegions(node, foldRegions)
        return foldRegions.toTypedArray()
    }

    private fun buildFoldRegions(node: ASTNode, regions: MutableList<FoldingDescriptor>) {
        val foldableTypes = listOf(OnjTypes.OBJECT, OnjTypes.ARRAY, OnjTypes.BLOCK_COMMENT)
        if (node.elementType in foldableTypes) {
            regions.add(FoldingDescriptor(node, node.textRange))
        }
        iterateOverAstChildren(node) { buildFoldRegions(it, regions) }
    }

    override fun getPlaceholderText(node: ASTNode): String =
        when (node.elementType) {
            OnjTypes.OBJECT -> "{ ... }"
            OnjTypes.ARRAY -> "[ ... ]"
            OnjTypes.BLOCK_COMMENT -> "/* ... */"
            else -> "..."
        }

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false
}
