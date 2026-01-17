package org.onj.language.env

import onj.parser.OnjSchemaParserData
import onj.schema.OnjSchemaArray
import onj.schema.OnjSchemaObject
import onj.value.OnjArray
import onj.value.OnjObject
import org.onj.language.typeResolution.OnjType

data class OnjFunctionModel(
    val name: String,
    val isInfix: Boolean,
    val paramsSchema: OnjSchemaArray,
    val paramsString: String,
    val returnType: OnjType
) {
    companion object {

        fun fromOnj(obj: OnjObject) = OnjFunctionModel(
            obj.get<String>("name"),
            obj.get<Boolean>("isInfix"),
            (
                onj.parser.OnjSchemaParser.parse(
                    obj.get<String>("paramSchema"),
                    OnjSchemaParserData(analysisMode = true)
                ) as OnjSchemaObject
            ).keys["params"]!! as OnjSchemaArray,
            obj.get<String>("paramSchema"),
            OnjType.fromString(obj.get<String>("returnType"))
        )
    }
}

data class OnjVariableModel(
    val name: String,
    val type: OnjType,
) {
    companion object {

        fun fromOnj(obj: OnjObject) = OnjVariableModel(
            obj.get<String>("name"),
            OnjType.fromString(obj.get<String>("type"))
        )
    }
}

data class OnjNamespaceModel(
    val name: String,
    val functions: List<OnjFunctionModel>,
    val variables: Map<String, OnjVariableModel>
) {
    companion object {

        fun fromOnj(onj: OnjObject) = OnjNamespaceModel(
            onj.get<String>("name"),
            onj
                .get<OnjArray>("functions")
                .value
                .map { OnjFunctionModel.fromOnj(it as OnjObject) },
            onj
                .get<OnjArray>("variables")
                .value
                .associate {
                    val variable = OnjVariableModel.fromOnj(it as OnjObject)
                    variable.name to variable
                },
        )
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
