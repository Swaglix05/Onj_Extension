package org.onj.language.typeResolution

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


    fun isArray(): Boolean = this is SomeArray || this is SpecificArray
    fun isObject(): Boolean = this is SomeObject || this is SpecificObject
    fun isInt(): Boolean = this is SomeInt || this is SpecificInt
    fun isFloat(): Boolean = this is SomeFloat || this is SpecificFloat
    fun isString(): Boolean = this is SomeStr || this is SpecificStr
    fun isBool(): Boolean = this is SomeBool || this is SpecificBool
    fun isUnknown(): Boolean = this is Unknown
    fun isNull(): Boolean = this is Null

    fun isNumber(): Boolean = isFloat() || isInt()

    fun isSome(): Boolean = this is SomeStr ||
            this is SomeInt ||
            this is SomeFloat ||
            this is SomeBool ||
            this is SomeObject ||
            this is SomeArray ||
            this is Unknown ||
            this is Null

    fun isSpecific(): Boolean = !isSome() || this is Null

    override fun toString(): String = printableName
}
