package org.onj.language.documentation

import com.intellij.icons.AllIcons
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.model.Pointer
import com.intellij.openapi.project.Project
import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.DocumentationTargetProvider
import com.intellij.platform.backend.documentation.PsiDocumentationTargetProvider
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementsAtOffsetUp
import org.jetbrains.annotations.Nls
import org.onj.language.OnjIcons
import org.onj.language.env.OnjFunctionModel
import org.onj.language.env.OnjVariableModel
import org.onj.language.psi.OnjFunctionLikePsiElement
import org.onj.language.psi.OnjPsiElementWithDocumentation
import org.onj.language.psi.impl.OnjFunctionNamePsi
import org.onj.language.psi.impl.OnjKeyPsi
import org.onj.language.psi.impl.OnjVarStructurePsi
import org.onj.language.psi.impl.OnjVariableAccessorPsi
import org.onj.language.psi.impl.OnjVariableDeclNamePsi
import org.onj.language.psi.impl.OnjVariableUsePsi

class OnjPsiDocumentationProvider : DocumentationTargetProvider {

    override fun documentationTargets(
        file: PsiFile,
        offset: Int
    ): List<out DocumentationTarget> {
        val element = file.findElementAt(offset)?.parent ?: return listOf()
        if (element is OnjVariableUsePsi) {
            val variable = element.reference.resolve()
            if (variable is OnjVariableDeclNamePsi) {
                return listOf(DocTargets.VariableDecl(variable))
            }
            val model = element.resolveGlobalVariable()
            if (model != null) return listOf(DocTargets.VariableModel(model, element.project))
            return listOf()
        }
        if (element is OnjKeyPsi) {
            return listOf(DocTargets.Key(element))
        }
        if (element is OnjVariableAccessorPsi) {
            val referenced = element.reference.resolve()
            if (referenced is OnjKeyPsi) return listOf(DocTargets.Key(referenced))
            return listOf()
        }
        if (element is OnjFunctionLikePsiElement) {
            val function = element.resolveFunction()
            if (function != null) return listOf(DocTargets.Function(function, element.project))
            return listOf()
        }
        if (element is OnjFunctionNamePsi) {
            val functionLike = element.parent as? OnjFunctionLikePsiElement ?: return listOf()
            val function = functionLike.resolveFunction()
            if (function != null) return listOf(DocTargets.Function(function, element.project))
        }
        return listOf()
    }
}

object DocTargets {

    fun presentationForVariable(name: String): TargetPresentation {
        return TargetPresentation
            .builder(name)
            .icon(AllIcons.Nodes.Variable)
            .presentation()
    }

    fun presentationForKey(name: String): TargetPresentation {
        return TargetPresentation
            .builder(name)
            .icon(OnjIcons.OBJECT)
            .presentation()
    }

    fun presentationForFunction(name: String): TargetPresentation {
        return TargetPresentation
            .builder(name)
            .icon(AllIcons.Nodes.Function)
            .presentation()
    }

    class Function(val function: OnjFunctionModel, val project: Project) : DocumentationTarget {

        override fun createPointer(): Pointer<out DocumentationTarget> = Pointer.hardPointer(this)

        override fun computePresentation(): TargetPresentation = presentationForFunction(function.name)

        override fun computeDocumentation(): DocumentationResult? = DocumentationResult.documentation(
            function.renderDoc(project)
        )
    }

    class Key(val key: OnjKeyPsi) : DocumentationTarget {

        override fun createPointer(): Pointer<out DocumentationTarget> = Pointer.hardPointer(this)

        override fun computePresentation(): TargetPresentation = presentationForKey(key.getKeyText(true))

        override fun computeDocumentation(): DocumentationResult? =
            key.renderDoc()?.let { DocumentationResult.documentation(it) }
    }

    class VariableModel(val variable: OnjVariableModel, val project: Project) : DocumentationTarget {

        override fun createPointer(): Pointer<out DocumentationTarget> = Pointer.hardPointer(this)

        override fun computePresentation(): TargetPresentation = presentationForVariable(variable.name)

        override fun computeDocumentation(): DocumentationResult? = DocumentationResult.documentation(
            variable.renderDoc(project)
        )
    }

    class VariableDecl(val variable: OnjVariableDeclNamePsi) : DocumentationTarget {

        override fun createPointer(): Pointer<out DocumentationTarget> = Pointer.hardPointer(this)

        override fun computePresentation(): TargetPresentation = presentationForVariable(variable.name ?: "")

        override fun computeDocumentation(): DocumentationResult? = variable
            .renderDoc()
            ?.let { DocumentationResult.documentation(it) }
    }

}
