package org.onj.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.find.findUsages.FindUsagesHandler
import com.intellij.find.findUsages.FindUsagesManager
import com.intellij.find.findUsages.FindUsagesOptions
import com.intellij.lang.ASTNode
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.TextRange
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.SearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import org.jetbrains.annotations.Unmodifiable
import org.onj.language.env.OnjVariableModel
import org.onj.language.psi.OnjRenamableReference
import org.onj.language.psi.OnjTypes
import org.onj.language.reference.OnjFindUsagesProvider
import org.onj.language.reference.OnjPsiReferenceBySymbolReferenceWrapper
import org.onj.language.rename.OnjElementFactory
import org.onj.language.symbols.OnjVariableSymbol
import org.onj.language.symbols.OnjVariableSymbolReference
import org.onj.language.typeResolution.OnjType
import org.onj.language.typeResolution.OnjTypeResolvablePsi
import org.onj.language.utils.Utils
import org.onj.language.utils.Utils.findInstance
import java.util.Collections

class OnjVariableUsePsi(
    node: ASTNode
) : ASTWrapperPsiElement(node), NavigatablePsiElement,
    OnjRenamableReference, PsiReference, OnjTypeResolvablePsi {

    private val reference = OnjPsiReferenceBySymbolReferenceWrapper(this)

    override fun resolveTypeSimple(): OnjType {
        val name = name
        if (name == "null") return OnjType.Null
        val target = reference.resolve()
        if (target is OnjVariableDeclNamePsi) {
            val parent = target.parent
            if (parent is OnjImportStructurePsi) return OnjType.SomeObject
            if (parent !is OnjVarStructurePsi) return OnjType.Unknown
            return parent.simpleDeclarationType()
        }
        return resolveGlobalVariable()?.type ?: OnjType.Unknown
    }

    override fun resolveTypeFull(): OnjType {
        if (name == "null") return OnjType.Null
        val target = reference.resolve()
        if (target is OnjVariableDeclNamePsi) {
            val parent = target.parent
            if (parent is OnjImportStructurePsi) return OnjType.SomeObject
            if (parent !is OnjVarStructurePsi) return OnjType.Unknown
            return parent.fullDeclarationType()
        }
        return resolveGlobalVariable()?.type ?: OnjType.Unknown
    }

    fun evaluateSeeThrough(): OnjTypeResolvablePsi? {
        val symbol = OnjVariableSymbolReference(this)
            .resolveReference()
            .firstOrNull()
            as? OnjVariableSymbol
            ?: return null
        val declaration = symbol.psiElement.parent
        if (declaration !is OnjVarStructurePsi) return null
        val query = ReferencesSearch.search(symbol.psiElement, containingFile.useScope)
        val amount = query.count()
        if (amount != 1) return null
        val declaredElement = declaration.children.findInstance<OnjTypeResolvablePsi>() ?: return null
        return declaredElement
    }

    fun resolveGlobalVariable(): OnjVariableModel? {
        val env = Utils.findEnvFile(project)?.getEnvironmentModel() ?: return null
        val topLevel = containingFile.children.findInstance<OnjTopLevelPsi>() ?: return null
        val includedNamespaces = topLevel.findUsedNamespaces()
        val name = name
        includedNamespaces.forEach { namespaceName ->
            val namespace = env.namespaces[namespaceName] ?: return@forEach
            val variable = namespace.variables[name] ?: return@forEach
            return variable
        }
        return null
    }

    override fun rename(newName: String) {
        val oldIdentifier = node.findChildByType(OnjTypes.IDENTIFIER) ?: return
        val newIdentifier = OnjElementFactory.createOnjIdentifier(project, newName)
        node.replaceChild(oldIdentifier, newIdentifier.node)
    }

    override fun getName(): String? = text

    override fun getOwnReferences(): @Unmodifiable Collection<out PsiSymbolReference> {
        return Collections.singletonList(OnjVariableSymbolReference(this))
    }

    override fun getReference(): PsiReference {
        return reference
    }

    override fun getElement(): PsiElement = reference.element

    override fun getRangeInElement(): TextRange = reference.rangeInElement

    override fun resolve(): PsiElement? = reference.resolve()

    override fun getCanonicalText(): @NlsSafe String = reference.canonicalText

    override fun handleElementRename(newElementName: String): PsiElement? = reference.handleElementRename(newElementName)

    override fun bindToElement(element: PsiElement): PsiElement? = reference.bindToElement(element)

    override fun isReferenceTo(element: PsiElement): Boolean = reference.isReferenceTo(element)

    override fun isSoft(): Boolean = reference.isSoft
}
