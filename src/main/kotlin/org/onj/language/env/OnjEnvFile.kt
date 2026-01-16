package org.onj.language.env

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import onj.parser.OnjParser
import onj.parser.OnjSchemaParser
import onj.schema.OnjSchema
import onj.value.OnjObject

class OnjEnvFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, OnjEnvLanguage) {

    private val LOCK = Any()

    private var envModel: OnjEnvModel? = null
    private var cacheOutdated: Boolean = true

    override fun getFileType(): FileType {
        return OnjEnvFileType
    }

    override fun clearCaches() {
        super.clearCaches()
    }

    fun getEnvironmentModel(): OnjEnvModel? {
        synchronized(LOCK) {
            if (!cacheOutdated) return envModel
            val parsed = parse()
            envModel = parsed
            cacheOutdated = false
            return parsed
        }
    }

    private fun parse(): OnjEnvModel? {
        try {
            val parsed = OnjParser.parse(text)
            parsedSchema.assertMatches(parsed)
            return OnjEnvModel.fromOnj(parsed as OnjObject)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun toString(): String {
        return "OnjEnv File"
    }

    companion object {
        private const val schemaStr = """
            
            var function = {
                name: string,
                type: string,
                paramSchema: string,
                returnType: string,
            };
            
            var variable = {
                name: string,
                type: string,
            };
            
            var namespace = {
                name: string,
                functions: function[],
                variables: variable[],
            };
            
            namespaces: namespace[],
        """

        private val parsedSchema: OnjSchema by lazy {
            OnjSchemaParser.parse(schemaStr)
        }
    }

}
