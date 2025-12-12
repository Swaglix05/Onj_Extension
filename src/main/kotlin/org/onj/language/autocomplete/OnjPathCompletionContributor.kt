package org.onj.language.autocomplete

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.icons.AllIcons
import com.intellij.openapi.util.io.toCanonicalPath
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import org.jetbrains.annotations.Unmodifiable
import org.onj.language.psi.OnjTypes
import org.onj.language.psi.impl.OnjImportPathPsi
import org.onj.language.psi.impl.OnjStringPsi
import org.onj.language.utils.Utils
import java.nio.file.FileVisitResult
import java.nio.file.Path
import javax.swing.Icon
import kotlin.io.path.*

class OnjPathCompletionContributor : CompletionContributor() {

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.or(
                PlatformPatterns.psiElement(OnjTypes.STRING_BEGIN).withSuperParent(2, PlatformPatterns.psiElement(OnjTypes.IMPORT_PATH)),
                PlatformPatterns.psiElement(OnjTypes.STRING_END).withSuperParent(2, PlatformPatterns.psiElement(OnjTypes.IMPORT_PATH)),
                PlatformPatterns.psiElement(OnjTypes.STRING_PART).withSuperParent(2, PlatformPatterns.psiElement(OnjTypes.IMPORT_PATH)),
                PlatformPatterns.psiElement(OnjTypes.STRING_ESCAPE).withSuperParent(2, PlatformPatterns.psiElement(OnjTypes.IMPORT_PATH)),
                PlatformPatterns.psiElement(OnjTypes.INVALID_STRING_ESCAPE).withSuperParent(2, PlatformPatterns.psiElement(OnjTypes.IMPORT_PATH)),
            ),
            OnjPathCompletionProvider()
        )
    }

}

class OnjPathCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val element = parameters.position
        val string = element.parent as? OnjStringPsi ?: return
        val offset = parameters.offset
        val typingOffsetInParent = element.startOffsetInParent + offset - element.textRange.startOffset
        val stringText = string.text.replace("IntellijIdeaRulezzz ", "")
        val typedPath = stringText.substring(1, typingOffsetInParent)
        val root = Utils.findContainingContentRoot(parameters.originalFile) ?: return
        val path = root.resolve(typedPath)
        val isDirectory = path.isDirectory()
        val parentPath = if (isDirectory) path else path.parent
        val name = path.fileName.name
        if (!parentPath.exists() || !parentPath.isDirectory()) return
        parentPath.visitFileTree(maxDepth = 1) {
            onVisitFile { path, _ ->
                if (isDirectory || path.name.contains(name)) {
                    val icon = lookupIconForFile(path, parameters)
                    result.consume(createLookupElement(root.relativize(path), path.isDirectory(), icon))
                }
                FileVisitResult.CONTINUE
            }
        }
    }

    private fun lookupIconForFile(path: Path, parameters: CompletionParameters): Icon? = parameters
        .originalFile
        .virtualFile
        .fileSystem
        .findFileByPath(path.toCanonicalPath())
        ?.fileType
        ?.icon

    private fun createLookupElement(
        path: Path,
        isDirectory: Boolean,
        icon: Icon?
    ): LookupElement = PrioritizedLookupElement.withPriority(
        OnjPathLookupElement(path, isDirectory, icon),
        if (isDirectory || path.extension == "onj") 1.0 else 0.0
    )

}

class OnjPathLookupElement(
    private val path: Path,
    private val isDirectory: Boolean,
    private val icon: Icon?
) : LookupElement() {

    override fun getLookupString(): String = path.name

    override fun getAllLookupStrings(): @Unmodifiable Set<String?> = setOf(
        lookupString, pathString()
    )

    override fun renderElement(presentation: LookupElementPresentation) {
        presentation.itemText = lookupString
        presentation.isItemTextBold = isDirectory || path.extension == "onj"
        if (isDirectory) {
            presentation.icon = AllIcons.Nodes.Folder
        } else {
            presentation.icon = icon
        }
    }

    private fun pathString(): String = path.pathString.replace('\\', '/')

    override fun handleInsert(context: InsertionContext) {
        val element = context.file.findElementAt(context.startOffset)
        val importPath = element?.parent?.parent as? OnjImportPathPsi ?: return
        var newPath = pathString()
        if (isDirectory) newPath += "/"
        importPath.replacePathString(newPath)
        val caret = context.editor.caretModel.primaryCaret
        caret.moveCaretRelatively(1, 0, false, false)
    }

}
