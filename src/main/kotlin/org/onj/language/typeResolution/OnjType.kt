package org.onj.language.typeResolution

sealed class OnjType {

    object SomeStr : OnjType()
    object SomeInt : OnjType()
    object SomeFloat : OnjType()
    object SomeBool : OnjType()
    object SomeObject : OnjType()
    object SomeArray : OnjType()
    object Unknown : OnjType()
    object Null : OnjType()

    class SpecificStr(val value: String) : OnjType()
    class SpecificInt(val value: Long) : OnjType()
    class SpecificFloat(val value: Double) : OnjType()
    class SpecificBool(val value: Boolean) : OnjType()
    class SpecificObject(val keys: Map<String, OnjType>) : OnjType()
    class SpecificArray(val elements: List<OnjType>) : OnjType()


    fun isArray(): Boolean = this is SomeArray || this is SpecificArray
    fun isObject(): Boolean = this is SomeObject || this is SpecificObject
    fun isInt(): Boolean = this is SomeInt || this is SpecificInt
    fun isFloat(): Boolean = this is SomeFloat || this is SpecificFloat
    fun isString(): Boolean = this is SomeStr || this is SpecificStr
    fun isBool(): Boolean = this is SomeBool || this is SpecificBool
    fun isUnknown(): Boolean = this is Unknown

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
}
