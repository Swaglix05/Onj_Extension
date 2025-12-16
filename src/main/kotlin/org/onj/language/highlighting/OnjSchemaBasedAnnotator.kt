package org.onj.language.highlighting

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.tree.util.children
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.elementType
import onj.schema.LiteralOnjSchemaArray
import onj.schema.OnjSchema
import onj.schema.OnjSchemaAny
import onj.schema.OnjSchemaArray
import onj.schema.OnjSchemaBoolean
import onj.schema.OnjSchemaFloat
import onj.schema.OnjSchemaInt
import onj.schema.OnjSchemaNamedObject
import onj.schema.OnjSchemaNamedObjectGroup
import onj.schema.OnjSchemaObject
import onj.schema.OnjSchemaString
import onj.schema.TypeBasedOnjSchemaArray
import org.onj.language.language.OnjSchemaFile
import org.onj.language.language.OnjSchemaFileType
import org.onj.language.psi.OnjTokenSets
import org.onj.language.psi.OnjTypes
import org.onj.language.psi.impl.OnjArrayEntryPsi
import org.onj.language.psi.impl.OnjKeyValuePairPsi
import org.onj.language.psi.impl.OnjNamedObjectPsi
import org.onj.language.psi.impl.OnjTopLevelPsi
import org.onj.language.psi.impl.OnjTripleDotPsi
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils
import org.onj.language.utils.Utils.findInstance
import kotlin.io.path.Path
import kotlin.io.path.pathString

class OnjSchemaBasedAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is OnjTopLevelPsi) annotateTopLevel(element, holder)
//        val file = element.containingFile
//        println(file)
//        if (file !is OnjSchemaFile) return
//        println(file.getParsedSchema())
    }

    private fun annotateTopLevel(element: OnjTopLevelPsi, holder: AnnotationHolder) {
        println("annotatooooooor")
        val schemaPath = element.findSchemaComment() ?: return
        val root = Utils.findContainingContentRoot(element.containingFile) ?: return
        val path = root.resolve(Path(schemaPath))
        val virtualFile = element.containingFile.virtualFile.fileSystem.findFileByPath(path.pathString)
        if (virtualFile == null || !virtualFile.exists() || virtualFile.fileType != OnjSchemaFileType) {
            return
        }
        val psiFile = virtualFile.findPsiFile(element.project) as? OnjSchemaFile ?: return
        val (schema, namedObjects) = psiFile.getParsed() ?: return
        println(schema)
        if (schema !is OnjSchemaObject) return
        matchObjectLike(element, schema, namedObjects, holder)
    }

    private fun matchObjectLike(obj: PsiElement, schema: OnjSchema, namedObjects: MutableMap<String, List<OnjSchemaNamedObject>>, holder: AnnotationHolder) {
        if (schema is OnjSchemaAny) return
        if (schema !is OnjSchemaObject) {
            annotation("Expected ${typeNameForOnjSchema(schema)}", obj, holder)
            return
        }
        println("---------------------")
        println("match obj:")
        println(schema.keys)
        println(schema.optionalKeys)
        println("---------------------")
        val missingHighlightElement = obj
            .node
            .children()
            .findLast { node -> !TokenSet.WHITE_SPACE.contains(node.elementType) }?.psi ?: return

        val seenKeys = mutableSetOf<String>()
        obj
            .children
            .filterIsInstance<OnjKeyValuePairPsi>()
            .forEach { pairPsi ->
                val key = pairPsi.getKey().getKeyText(false)
                seenKeys.add(key)
                val valueSchema = schema.keys[key]
                    ?: schema.optionalKeys[key]
                if (valueSchema == null && !schema.allowsAdditional) {
                    annotation("Unknown key", pairPsi.getKey(), holder)
                }
                if (valueSchema == null) return@forEach
                matchValue(pairPsi.getValue() ?: return@forEach, valueSchema, namedObjects, holder)
            }
        var mayHaveAdditionalKeys = false
        obj
            .children
            .filterIsInstance<OnjTripleDotPsi>()
            .forEach { tripleDotPsi ->
                val toInclude = tripleDotPsi.children.findInstance<OnjTypeResolvablePsi>()?.resolveTypeFull()
                if (toInclude !is OnjType.SpecificObject) return
                if (toInclude.mayHaveMoreKeys) mayHaveAdditionalKeys = true
                toInclude.keys.forEach { (key, type) ->
                    val valueSchema = schema.keys[key]
                        ?: schema.optionalKeys[key]
                    seenKeys.add(key)
                    if (valueSchema == null && !schema.allowsAdditional) {
                        annotation("Unknown key '$key' included here", tripleDotPsi, holder)
                    }
                    if (valueSchema == null) return@forEach
                    val matchResult = matchTypeToSchema(type, valueSchema) ?: return@forEach
                    annotation("Type mismatch in included key '$key': $matchResult", tripleDotPsi, holder)
                }
            }
        if (mayHaveAdditionalKeys) return
        schema.keys.forEach { (key, _) ->
            if (key in seenKeys) return@forEach
            annotation("Missing key: '$key'", missingHighlightElement, holder)
        }
    }

    private fun matchValue(
        value: PsiElement,
        schema: OnjSchema,
        namedObjects: MutableMap<String, List<OnjSchemaNamedObject>>,
        holder: AnnotationHolder
    ) {
        if (schema is OnjSchemaAny) return
        when (value.elementType) {
            OnjTypes.OBJECT -> matchObjectLike(value, schema, namedObjects, holder)
            OnjTypes.ARRAY -> matchArray(value, schema, namedObjects, holder)
            OnjTypes.NAMED_OBJECT -> matchNamedObject(value as OnjNamedObjectPsi, schema, namedObjects, holder)
            else if (value is OnjTypeResolvablePsi) -> matchSimple(value, schema, holder)
        }
    }

    private fun matchTypeToSchema(type: OnjType, schema: OnjSchema): String? {
        return when (schema) {
            is OnjSchemaAny -> null
            is OnjSchemaFloat -> if (type.isFloat()) null else "Expected float, found: ${type.printableName}"
            is OnjSchemaInt -> if (type.isInt()) null else "Expected int, found: ${type.printableName}"
            is OnjSchemaString -> if (type.isString()) null else "Expected string, found: ${type.printableName}"
            is OnjSchemaBoolean -> if (type.isBool()) null else "Expected boolean, found: ${type.printableName}"
            // No deep matching performed here
            is OnjSchemaNamedObjectGroup -> if (type.isObject()) null else "Expected object, found: ${type.printableName}"
            is OnjSchemaObject -> if (type.isObject()) null else "Expected object, found: ${type.printableName}"
            is OnjSchemaArray -> if (type.isObject()) null else "Expected array, found: ${type.printableName}"
            else -> null
        }
    }

    private fun matchSimple(value: OnjTypeResolvablePsi, schema: OnjSchema, holder: AnnotationHolder) {
        val type = value.resolveTypeSimple()
        val result = matchTypeToSchema(type, schema) ?: return
        annotation(result, value, holder)
    }

    private fun matchNamedObject(
        obj: OnjNamedObjectPsi,
        schema: OnjSchema,
        namedObjects: MutableMap<String, List<OnjSchemaNamedObject>>,
        holder: AnnotationHolder
    ) {
        if (schema is OnjSchemaAny) return
        if (schema !is OnjSchemaNamedObjectGroup) {
            annotation("Expected ${typeNameForOnjSchema(schema)}", obj, holder)
            return
        }
        val objectOptions = namedObjects[schema.name] ?: return
        val name = obj.name
        val namedObject = objectOptions.find { it.name == name }
        if (namedObject == null) {
            annotation("name '$name' not in group '${schema.name}'", obj.getNameIdentifier() ?: return, holder)
            return
        }
        matchObjectLike(obj, namedObject.obj, namedObjects, holder)
    }

    private fun matchArray(
        arr: PsiElement,
        schema: OnjSchema,
        namedObjects: MutableMap<String, List<OnjSchemaNamedObject>>,
        holder: AnnotationHolder
    ) {
        if (schema is OnjSchemaAny) return
        if (schema !is OnjSchemaArray) {
            annotation("Expected ${typeNameForOnjSchema(schema)}", arr, holder)
            return
        }
        if (schema is TypeBasedOnjSchemaArray) matchTypeBasedArray(arr, schema, namedObjects, holder)
        if (schema is LiteralOnjSchemaArray) matchLiteralArray(arr, schema, namedObjects, holder)
    }

    private fun matchLiteralArray(
        arr: PsiElement,
        schema: LiteralOnjSchemaArray,
        namedObjects: MutableMap<String, List<OnjSchemaNamedObject>>,
        holder: AnnotationHolder
    ) {
        var index = 0
        arr
            .children
            .forEach { entryPsi ->
                if (entryPsi is OnjArrayEntryPsi) {
                    val value = entryPsi.children.findInstance<OnjTypeResolvablePsi>() ?: return@forEach
                    val valueSchema = schema.schemas.getOrNull(index)
                    if (valueSchema == null) {
                        annotation("Array to long", value, holder)
                        return@forEach
                    }
                    matchValue(value, valueSchema, namedObjects, holder)
                    index++
                } else if (entryPsi is OnjTripleDotPsi) {
                    val toIncludePsi = entryPsi.children.findInstance<OnjTypeResolvablePsi>() ?: return@forEach
                    val includeType = toIncludePsi.resolveTypeFull()
                    if (includeType !is OnjType.SpecificArray) return
                    if (includeType.mayHaveMoreElements) return
                    includeType.elements.forEach { enty ->
                        val valueSchema = schema.schemas.getOrNull(index)
                        if (valueSchema == null) {
                            annotation("Too many keys are included here", entryPsi, holder)
                            return@forEach
                        }
                        val result = matchTypeToSchema(enty, valueSchema) ?: return@forEach
                        annotation("Element here has mismatched type: $result", entryPsi, holder)
                    }
                }
            }
    }

    private fun matchTypeBasedArray(
        arr: PsiElement,
        schema: TypeBasedOnjSchemaArray,
        namedObjects: MutableMap<String, List<OnjSchemaNamedObject>>,
        holder: AnnotationHolder
    ) {
        var size = 0
        var sizeUnknown = false
        arr
            .children
            .filterIsInstance<OnjArrayEntryPsi>()
            .forEach { element ->
                val entry = element.children.findInstance<OnjTypeResolvablePsi>() ?: return@forEach
                size++
                matchValue(entry, schema.type, namedObjects, holder)
            }
        arr
            .children
            .filterIsInstance<OnjTripleDotPsi>()
            .forEach { tripleDotPsi ->
                val toInclude = tripleDotPsi.children.findInstance<OnjTypeResolvablePsi>() ?: return@forEach
                val includeType = toInclude.resolveTypeFull()
                if (includeType !is OnjType.SpecificArray) return
                if (includeType.mayHaveMoreElements) sizeUnknown = true
                includeType.elements.forEach { type ->
                    val result = matchTypeToSchema(type, schema.type) ?: return@forEach
                    annotation("value included here has mismatched type: $result", tripleDotPsi, holder)
                }
                size += includeType.elements.size
            }
        if (sizeUnknown) return
        if (schema.size == null || size == schema.size) return
        annotation("array length mismatch: expected ${schema.size}, actual $size", arr, holder)
    }

    private fun typeNameForOnjSchema(schema: OnjSchema): String = when (schema) {
        is OnjSchemaInt -> "int"
        is OnjSchemaFloat -> "float"
        is OnjSchemaBoolean -> "boolean"
        is OnjSchemaString -> "string"
        is OnjSchemaArray -> "array"
        is OnjSchemaObject -> "object"
        is OnjSchemaAny -> "any"
        is OnjSchemaNamedObjectGroup -> "named object"
        else -> ""
    }

    fun annotation(message: String, element: PsiElement, holder: AnnotationHolder) {
        holder
            .newAnnotation(HighlightSeverity.ERROR, message)
            .range(element)
            .highlightType(ProblemHighlightType.GENERIC_ERROR)
            .create()
    }

}
