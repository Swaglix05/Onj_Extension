package org.onj.language.formatter

import com.intellij.formatting.*
import com.intellij.psi.codeStyle.CodeStyleSettings
import org.onj.language.language.OnjLanguage
import org.onj.language.psi.OnjTypes

class OnjFormattingModelBuilder : FormattingModelBuilder {

    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val codeStyleSettings = formattingContext.codeStyleSettings

        val spacing = createSpacingBuilder(codeStyleSettings)

        return FormattingModelProvider.createFormattingModelForPsiFile(
            formattingContext.containingFile,
            OnjBlock(
                spacing,
                formattingContext.node,
                Wrap.createWrap(WrapType.NONE, false),
                null,
                Indent.getNoneIndent()
            ),
            codeStyleSettings
        )
    }

    private fun createSpacingBuilder(codeStyleSettings: CodeStyleSettings): SpacingBuilder {
        val spacingBuilder = SpacingBuilder(codeStyleSettings, OnjLanguage)
        spacingBuilder
            .aroundInside(OnjTypes.PLUS, OnjTypes.BINARY_OPERATION).spaceIf(true)
            .aroundInside(OnjTypes.MINUS, OnjTypes.BINARY_OPERATION).spaceIf(true)
            .aroundInside(OnjTypes.STAR, OnjTypes.BINARY_OPERATION).spaceIf(true)
            .aroundInside(OnjTypes.DIV, OnjTypes.BINARY_OPERATION).spaceIf(true)
            .afterInside(OnjTypes.MINUS, OnjTypes.NEGATION).spaceIf(false)
            .after(OnjTypes.COLON).spaceIf(true)
            .before(OnjTypes.COLON).spaceIf(false)
            .aroundInside(OnjTypes.DOT, OnjTypes.TRIPLE_DOT).spaceIf(false)
            .after(OnjTypes.DOLLAR).spaceIf(false)
            .after(OnjTypes.NAMED_OBJECT_NAME).spaceIf(true)
            .aroundInside(OnjTypes.FUNCTION_NAME, OnjTypes.INFIX_FUNCTION_CALL).spaceIf(true)
            .afterInside(OnjTypes.FUNCTION_NAME, OnjTypes.FUNCTION_CALL).spaceIf(false)
            .around(OnjTypes.HASH).spaceIf(false)
            .after(OnjTypes.L_PAREN).spaceIf(false)
            .before(OnjTypes.R_PAREN).spaceIf(false)
            .after(OnjTypes.IMPORT).spaceIf(true)
            .before(OnjTypes.AS_CONTEXT_DEPENDENT_KEYWORD).spaceIf(true)
            .before(OnjTypes.SEMICOLON).spaceIf(false)
            .around(OnjTypes.EQUALS).spaceIf(true)
            .before(OnjTypes.COMMA).spaceIf(false)
            .after(OnjTypes.COMMA).spaceIf(true)
        return spacingBuilder
    }


}
