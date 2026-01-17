package org.onj.language.typeResolution

import onj.schema.OnjSchema
import onj.schema.OnjSchemaAny
import onj.schema.OnjSchemaArray
import onj.schema.OnjSchemaBoolean
import onj.schema.OnjSchemaCustomDataType
import onj.schema.OnjSchemaFloat
import onj.schema.OnjSchemaInt
import onj.schema.OnjSchemaNamedObjectGroup
import onj.schema.OnjSchemaObject
import onj.schema.OnjSchemaString
import org.onj.language.psi.impl.OnjKeyValuePairPsi

sealed class OnjType(val printableName: String) {

    object SomeStr : OnjType("string")
    object SomeInt : OnjType("int")
    object SomeFloat : OnjType("float")
    object SomeBool : OnjType("boolean")
    object SomeObject : OnjType("{...}")
    object SomeArray : OnjType("[...]")
    object Unknown : OnjType("*")
    object Null : OnjType("null")

    class SpecificStr(val value: String) : OnjType("string")
    class SpecificInt(val value: Long) : OnjType("int")
    class SpecificFloat(val value: Double) : OnjType("float")
    class SpecificBool(val value: Boolean) : OnjType("boolean")
    class SpecificObject(
        val keys: Map<String, OnjType>,
        val backingPsi: Map<String, OnjKeyValuePairPsi>,
        val mayHaveMoreKeys: Boolean
    ) : OnjType("{...}")
    class SpecificArray(
        val elements: List<OnjType>,
        val mayHaveMoreElements: Boolean
    ) : OnjType("[...]")

    class Custom(val name: String) : OnjType("custom($name)")

    fun isArray(): Boolean = this is SomeArray || this is SpecificArray
    fun isObject(): Boolean = this is SomeObject || this is SpecificObject
    fun isInt(): Boolean = this is SomeInt || this is SpecificInt
    fun isFloat(): Boolean = this is SomeFloat || this is SpecificFloat
    fun isString(): Boolean = this is SomeStr || this is SpecificStr
    fun isBool(): Boolean = this is SomeBool || this is SpecificBool
    fun isUnknown(): Boolean = this is Unknown
    fun isNull(): Boolean = this is Null
    fun isCustom(): Boolean = this is Custom

    fun isNumber(): Boolean = isFloat() || isInt()

    fun isSome(): Boolean = this is SomeStr ||
            this is SomeInt ||
            this is SomeFloat ||
            this is SomeBool ||
            this is SomeObject ||
            this is SomeArray ||
            this is Unknown ||
            this is Custom ||
            this is Null

    fun isSpecific(): Boolean = !isSome() || this is Null

    override fun toString(): String = printableName

    fun matchTypeToSchema(schema: OnjSchema): String? {
        if (isUnknown()) return null
        if (isNull()) {
            if (schema.nullable) return null
            return "null not allowed here"
        }
        return when (schema) {
            is OnjSchemaAny -> null
            is OnjSchemaFloat -> if (isFloat()) null else "Expected float, found: ${printableName}"
            is OnjSchemaInt -> if (isInt()) null else "Expected int, found: ${printableName}"
            is OnjSchemaString -> if (isString()) null else "Expected string, found: ${printableName}"
            is OnjSchemaBoolean -> if (isBool()) null else "Expected boolean, found: ${printableName}"
            // No deep matching performed here
            is OnjSchemaNamedObjectGroup -> if (isObject()) null else "Expected object, found: ${printableName}"
            is OnjSchemaObject -> if (isObject()) null else "Expected object, found: ${printableName}"
            is OnjSchemaArray -> if (isObject()) null else "Expected array, found: ${printableName}"
            is OnjSchemaCustomDataType -> if (this is Custom && name == schema.name) {
                null
            } else {
                "Expected custom(${schema.name}), found: ${printableName}"
            }
            else -> null
        }
    }
    
    companion object {

        fun fromString(string: String): OnjType = when (string) {
            "string" -> SomeStr
            "int" -> SomeInt
            "float" -> SomeFloat
            "boolean" -> SomeBool
            "object" -> SomeObject
            "array" -> SomeArray
            "*" -> Unknown
            else -> Custom(string)
        }
    }
}
