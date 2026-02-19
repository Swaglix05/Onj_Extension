package org.onj.language.env

import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.ColorKey
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.impl.ColorKeyColor
import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil
import com.intellij.openapi.project.Project
import com.intellij.ui.ColorUtil
import com.jetbrains.rd.generator.nova.PredefinedType
import onj.parser.OnjSchemaParserData
import onj.schema.OnjSchemaArray
import onj.schema.OnjSchemaObject
import onj.value.OnjArray
import onj.value.OnjObject
import org.onj.language.language.OnjLanguage
import org.onj.language.typeResolution.OnjType

data class OnjFunctionModel(
    val name: String,
    val isInfix: Boolean,
    val paramsSchema: OnjSchemaArray,
    val paramsString: String,
    val returnType: OnjType,
    val paramNames: List<String>,
    val fromNamespace: String,
) {

    fun renderDoc(project: Project): String {
        val keywordColor = EditorColorsManager
            .getInstance()
            .globalScheme
            .getAttributes(DefaultLanguageHighlighterColors.KEYWORD)
            .foregroundColor
        val htmlKeywordColor = ColorUtil.toHtmlColor(keywordColor)

        val stringColor = EditorColorsManager
            .getInstance()
            .globalScheme
            .getAttributes(DefaultLanguageHighlighterColors.STRING)
            .foregroundColor
        val htmlStringColor = ColorUtil.toHtmlColor(stringColor)

        val isOperator = name.startsWith("operator%")
        val isConversion = name.startsWith("convert%")
        val cleanName = name.removePrefix("operator%").removePrefix("convert%")
        val paramsText = paramNames.joinToString(separator = ", ")

        val builder = StringBuilder()
        builder
            .append(DocumentationMarkup.DEFINITION_START)
            .append("<span style=\"color: $htmlKeywordColor;\">")
        if (isInfix) builder.append("infix ")
        if (isOperator) builder.append("operator ")
        if (isConversion) builder.append("conversion ")
        builder
            .append("function ")
            .append("</span>")
            .append(cleanName).append("(")
            .append(paramsText)
            .append(")")
            .append(": ").append(returnType.printableName)
            .append("<br /><br />")
            .append("Schema: <br />")
            .append("<span style=\"color: $htmlStringColor;\">").append(paramsString).append("</span>")
            .append(DocumentationMarkup.DEFINITION_END)
            .append(DocumentationMarkup.CONTENT_START)
            .append("from namespace ").append(fromNamespace)
            .append(DocumentationMarkup.CONTENT_END)
        return builder.toString()
    }

    companion object {

        fun fromOnj(obj: OnjObject, fromNamespace: String,) = OnjFunctionModel(
            obj.get<String>("name"),
            obj.get<Boolean>("isInfix"),
            (
                onj.parser.OnjSchemaParser.parse(
                    obj.get<String>("paramSchema"),
                    OnjSchemaParserData(analysisMode = true)
                ) as OnjSchemaObject
            ).keys["params"]!! as OnjSchemaArray,
            obj.get<String>("paramSchema"),
            OnjType.fromString(obj.get<String>("returnType")),
            obj.get<OnjArray>("paramNames").value.map { it.value as String },
            fromNamespace
        )
    }
}

data class OnjVariableModel(
    val name: String,
    val type: OnjType,
    val fromNamespace: String,
) {

    fun renderDoc(project: Project): String {
        val builder = StringBuilder()
        builder.append(DocumentationMarkup.DEFINITION_START)
        val text = "var $name: ${type.printableName}"
        HtmlSyntaxInfoUtil.appendHighlightedByLexerAndEncodedAsHtmlCodeSnippet(builder, project, OnjLanguage, text, 1f)
        builder.append(DocumentationMarkup.DEFINITION_END)
        builder.append(DocumentationMarkup.CONTENT_START)
        builder.append("from namespace $fromNamespace")
        builder.append(DocumentationMarkup.CONTENT_END)
        return builder.toString()
    }

    companion object {

        fun fromOnj(obj: OnjObject, fromNamespace: String) = OnjVariableModel(
            obj.get<String>("name"),
            OnjType.fromString(obj.get<String>("type")),
            fromNamespace
        )
    }
}

data class OnjNamespaceModel(
    val name: String,
    val functions: List<OnjFunctionModel>,
    val variables: Map<String, OnjVariableModel>
) {
    companion object {

        fun fromOnj(onj: OnjObject): OnjNamespaceModel {
            val name = onj.get<String>("name")
            return OnjNamespaceModel(
                name,
                onj
                    .get<OnjArray>("functions")
                    .value
                    .map { OnjFunctionModel.fromOnj(it as OnjObject, name) },
                onj
                    .get<OnjArray>("variables")
                    .value
                    .associate {
                        val variable = OnjVariableModel.fromOnj(it as OnjObject, name)
                        variable.name to variable
                    },
            )
        }
    }
}

data class OnjEnvModel(
    val namespaces: Map<String, OnjNamespaceModel>
) {
    companion object {

        fun fromOnj(onj: OnjObject): OnjEnvModel = OnjEnvModel(
            onj
                .get<OnjArray>("namespaces")
                .value
                .associate {
                    val namespace = OnjNamespaceModel.fromOnj(it as OnjObject)
                    namespace.name to namespace
                }
        )
    }
}

enum class OnjFunctionType {
    OPERATOR, INFIX, NORMAL, CONVERSION
}
