@file:Suppress("UnstableApiUsage")

package org.onj.language

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.impl.include.FileIncludeInfo
import com.intellij.psi.impl.include.FileIncludeProvider
import com.intellij.psi.util.childrenOfType
import com.intellij.util.Consumer
import com.intellij.util.indexing.FileContent
import org.onj.language.language.OnjFileType
import org.onj.language.psi.impl.OnjImportStructurePsi

// What exactly does this do?
class OnjFileIncludeProvider : FileIncludeProvider() {

    override fun getId(): String = "OnjFileIncludeProvider" // no clue what to return here

    override fun acceptFile(file: VirtualFile): Boolean {
        return file.fileType == OnjFileType
    }

    override fun registerFileTypesUsedForIndexing(fileTypeSink: Consumer<in FileType>) {
        fileTypeSink.consume(OnjFileType)
    }

    override fun getIncludeInfos(content: FileContent): Array<out FileIncludeInfo?> {
        return content
            .psiFile
            .childrenOfType<OnjImportStructurePsi>()
            .mapNotNull { it.resolveToFile()?.canonicalPath }
            .onEach { println(it) }
            .map { path -> FileIncludeInfo(path) }
            .toTypedArray()
    }
}