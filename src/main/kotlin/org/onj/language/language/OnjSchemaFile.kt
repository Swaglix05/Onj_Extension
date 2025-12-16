package org.onj.language.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.util.io.toCanonicalPath
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.openapi.vfs.readText
import com.intellij.psi.FileViewProvider
import onj.parser.OnjParserException
import onj.parser.OnjSchemaParser
import onj.parser.OnjSchemaParserData
import onj.schema.OnjSchema
import onj.schema.OnjSchemaNamedObject
import org.onj.language.utils.Utils

class OnjSchemaFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, OnjSchemaLanguage) {

    private val LOCK = Any()

    private var parsed: Pair<OnjSchema, MutableMap<String, List<OnjSchemaNamedObject>>>? = null

    fun getParsed(): Pair<OnjSchema, MutableMap<String, List<OnjSchemaNamedObject>>>? {
        synchronized(LOCK) {
            parsed?.let { return it }
            val root = Utils.findContainingContentRoot(this)
            val parserData = OnjSchemaParserData(
                importBasePath = root?.toCanonicalPath(),
                importCache = { file ->
                    println("in import cache")
                    val virtualFile = virtualFile.fileSystem.findFileByPath(file.path)
                    if (virtualFile == null || !virtualFile.exists() || virtualFile.fileType == OnjSchemaFileType) {
                        return@OnjSchemaParserData null
                    }
                    val psiFile = virtualFile.findPsiFile(project) as? OnjSchemaFile ?: return@OnjSchemaParserData null
                    println("from import cache")
                    psiFile.getParsed()
                }
            )
            try {
                val result = OnjSchemaParser.parseFileComplete(virtualFile.toNioPath().toFile(), parserData)
                parsed = result
                return result
            } catch (e: OnjParserException) {
                println(e)
                parsed = null
                return null
            }
        }
    }

    fun getParsedSchema(): OnjSchema? = getParsed()?.first

    override fun clearCaches() {
        super.clearCaches()
        synchronized(LOCK) {
            println("caches cleared!!!!!!!")
            parsed = null
        }
    }

    override fun getFileType(): FileType {
        return OnjSchemaFileType
    }

}
