package org.onj.language.autocomplete

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.icons.AllIcons
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.elementType
import com.intellij.util.ProcessingContext
import com.intellij.util.getValue
import org.jetbrains.annotations.Unmodifiable
import org.onj.language.psi.OnjTypes
import org.onj.language.psi.impl.OnjAccessPsi
import org.onj.language.psi.impl.OnjImportStructurePsi
import org.onj.language.psi.impl.OnjKeyPsi
import org.onj.language.psi.impl.OnjKeyValuePairPsi
import org.onj.language.psi.impl.OnjVariableDeclNamePsi
import org.onj.language.rename.OnjElementFactory
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils.findInstance

class OnjAccessCompletionContributor : CompletionContributor() {

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.or(
                PlatformPatterns
                    .psiElement(OnjTypes.IDENTIFIER)
                    .and(
                        PlatformPatterns
                            .psiElement()
                            .withParent(PlatformPatterns.psiElement(OnjTypes.VARIABLE_ACCESSOR))
                    ),
            ),
            OnjAccessCompletionProvider()
        )
    }

}


class OnjAccessCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val element = parameters.position
        val accessElement = element.parent?.parent as? OnjAccessPsi ?: return
        val toAccess = accessElement
            .children
            .findInstance<OnjTypeResolvablePsi>()
            ?.resolveTypeFull()
            ?: return
        if (!toAccess.isSpecific() || !toAccess.isObject()) return
        toAccess as OnjType.SpecificObject
        toAccess.backingPsi.forEach { (key, psi) ->
            val keyPsi = psi.getKey()
            val lookupElement = OnjAccessLookupElement(key, psi, keyPsi)
            result.consume(lookupElement)
        }
    }

}

class OnjAccessLookupElement(
    val key: String,
    val backingKeyValuePsi: OnjKeyValuePairPsi,
    val backingKeyPsi: OnjKeyPsi,
) : LookupElement() {

    val insertableString: String

    init {
        val keyIsIdentifier = OnjElementFactory.identifierPattern.matches(key)
        insertableString = if (keyIsIdentifier) {
            key
        } else {
            val newName = key
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("\\", "\\\\")
            "\"$newName\""
        }
    }

    override fun getLookupString(): String = insertableString

    override fun getAllLookupStrings(): @Unmodifiable Set<String?> {
        return setOf(lookupString, key)
    }

    override fun getPsiElement(): PsiElement = backingKeyPsi

    override fun renderElement(presentation: LookupElementPresentation) {
        presentation.itemText = lookupString
        val type = backingKeyValuePsi.getValue()?.resolveTypeSimple()
        presentation.typeText = type?.printableName ?: "*"
    }

}
