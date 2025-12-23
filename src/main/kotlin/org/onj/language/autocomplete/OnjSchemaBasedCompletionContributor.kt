package org.onj.language.autocomplete

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import onj.schema.LiteralOnjSchemaArray
import onj.schema.OnjSchemaObject
import onj.schema.TypeBasedOnjSchemaArray
import org.jetbrains.annotations.Unmodifiable
import org.onj.language.OnjIcons
import org.onj.language.language.OnjSchemaFile
import org.onj.language.language.OnjSchemaFileType
import org.onj.language.psi.OnjTypes
import org.onj.language.psi.impl.OnjArrayEntryPsi
import org.onj.language.psi.impl.OnjKeyValuePairPsi
import org.onj.language.psi.impl.OnjObjectPsi
import org.onj.language.psi.impl.OnjTopLevelPsi
import org.onj.language.rename.OnjElementFactory
import org.onj.language.typeResolution.OnjType
import org.onj.language.utils.Utils
import org.onj.language.utils.Utils.findInstance
import java.util.Stack
import kotlin.io.path.Path
import kotlin.io.path.pathString

class OnjSchemaBasedCompletionContributor : CompletionContributor() {

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns
                .psiElement(OnjTypes.IDENTIFIER)
                .withParent(
                    PlatformPatterns
                        .psiElement(OnjTypes.KEY)
                        .withParent(PlatformPatterns.psiElement(OnjTypes.KEY_VALUE_PAIR))
                ),
            OnjSchemaBasedCompletionProvider(true)
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns
                .psiElement(OnjTypes.IDENTIFIER)
                .withParent(
                    PlatformPatterns
                        .psiElement(OnjTypes.KEY)
                        .withParent(PlatformPatterns.psiElement(OnjTypes.OBJECT))
                ),
            OnjSchemaBasedCompletionProvider(false)
        )
    }

}

class OnjSchemaBasedCompletionProvider(
    private val inKeyValuePair: Boolean
) : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val topLevel = parameters
            .originalFile
            .children
            .findInstance<OnjTopLevelPsi>()
            ?: return
        val schemaPath = topLevel.findSchemaPath() ?: return
        val root = Utils.findContainingContentRoot(parameters.originalFile) ?: return
        val combinedPath = root.resolve(Path(schemaPath))
        val virtualFile = parameters
            .originalFile
            .virtualFile
            .fileSystem
            .findFileByPath(combinedPath.pathString)
        if (virtualFile == null || !virtualFile.exists() || virtualFile.fileType != OnjSchemaFileType) {
            return
        }
        val psiFile = virtualFile.findPsiFile(parameters.originalFile.project) as? OnjSchemaFile ?: return
        val schema = psiFile.getParsedSchema() ?: return

        val position = parameters.originalPosition ?: return
        val path = Stack<PsiElement>()
        var curElement: PsiElement? = position
        var firstObject: OnjObjectPsi? = null
        while (curElement != null) {
            if (curElement is OnjObjectPsi) firstObject = curElement
            if (curElement is OnjTopLevelPsi) break
            if (curElement is OnjKeyValuePairPsi || curElement is OnjArrayEntryPsi) {
                path.push(curElement)
            }
            curElement = curElement.parent
        }

        var curSchema = schema
        while (true) {
            if (inKeyValuePair) {
                if (path.size <= 1) break
            } else {
                if (path.isEmpty()) break
            }
            when (val element = path.pop()) {
                is OnjKeyValuePairPsi -> {
                    if (schema !is OnjSchemaObject) return
                    val key = element.getKey().getKeyText(false)
                    curSchema = schema.keys[key] ?: schema.optionalKeys[key] ?: return
                }
                is OnjArrayEntryPsi -> {
                    when (curSchema) {
                        is TypeBasedOnjSchemaArray -> curSchema = curSchema.type
                        is LiteralOnjSchemaArray -> {
                            val index = element.getIndexInArray()
                            curSchema = curSchema.schemas.getOrNull(index) ?: return
                        }
                        else -> return
                    }
                }
                else -> continue
            }
        }
        if (curSchema !is OnjSchemaObject) return

        val definedKeys = if (firstObject == null) {
            topLevel
                .children
                .filterIsInstance<OnjKeyValuePairPsi>()
                .map { it.getKey().getKeyText(false) }
                .toSet()
        } else {
            val type = firstObject.resolveTypeFull()
            if (!type.isObject() || !type.isSpecific()) {
                setOf()
            } else {
                type as OnjType.SpecificObject
                type.keys.keys.toSet()
            }
        }

        curSchema
            .keys
            .forEach { (name, _) ->
                val isDefined = name in definedKeys
                val element = PrioritizedLookupElement.withPriority(
                    OnjSchemaBasedLookupElement(name, isDefined, false),
                    if (isDefined) 0.0 else 1.5
                )
                result.consume(element)
            }
        curSchema
            .optionalKeys
            .forEach { (name, _) ->
                val isDefined = name in definedKeys
                val element = PrioritizedLookupElement.withPriority(
                    OnjSchemaBasedLookupElement(name, isDefined, true),
                    if (isDefined) 0.0 else 1.0
                )
                result.consume(element)
            }
    }
}

class OnjSchemaBasedLookupElement(
    val keyName: String,
    val alreadyDefined: Boolean,
    val isOptional: Boolean
) : LookupElement() {

    private val toComplete: String = if (OnjElementFactory.identifierPattern.matches(keyName)) {
        keyName
    } else {
        val newName = keyName
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            .replace("\"", "\\\"")
            .replace("'", "\\'")
            .replace("\\", "\\\\")
        "\"$newName\""
    }

    override fun getLookupString(): String = toComplete

    override fun getAllLookupStrings(): @Unmodifiable Set<String?> {
        return setOf(toComplete, keyName)
    }

    override fun renderElement(presentation: LookupElementPresentation) {
        presentation.icon = OnjIcons.SCHEMA_FILE
        if (isOptional) {
            presentation.itemText = "$keyName?"
            presentation.isItemTextBold = false
        } else {
            presentation.itemText = keyName
            presentation.isItemTextBold = true
        }
    }
}
