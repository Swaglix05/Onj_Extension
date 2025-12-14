@file:Suppress("UnstableApiUsage")

package org.onj.language.symbols

import com.intellij.model.SingleTargetReference
import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.onj.language.psi.impl.OnjVariableAccessorPsi
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi

class OnjKeySymbolReference(
    val accessElement: OnjTypeResolvablePsi,
    val referencingElement: OnjVariableAccessorPsi
) : SingleTargetReference(), PsiSymbolReference {

    override fun resolveSingleTarget(): Symbol? {
        val accessorType = referencingElement.getAccessValue()
        if (!accessorType.isString() || !accessorType.isSpecific()) return null
        accessorType as OnjType.SpecificStr
        val accessType = accessElement.resolveTypeFull()
        if (!accessType.isObject() || !accessType.isSpecific()) return null
        accessType as OnjType.SpecificObject
        return accessType.backingPsi[accessorType.value]?.getKey()?.symbol
    }

    override fun getElement(): PsiElement = referencingElement

    override fun getRangeInElement(): TextRange = TextRange(0, referencingElement.textLength)
}
