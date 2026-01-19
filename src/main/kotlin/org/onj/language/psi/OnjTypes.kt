package org.onj.language.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import org.onj.language.psi.impl.OnjAccessPsi
import org.onj.language.psi.impl.OnjArrayEntryPsi
import org.onj.language.psi.impl.OnjArrayPsi
import org.onj.language.psi.impl.OnjAsContextDependentKeywordPsi
import org.onj.language.psi.impl.OnjBinaryOperationPsi
import org.onj.language.psi.impl.OnjConversionPsi
import org.onj.language.psi.impl.OnjFilePsi
import org.onj.language.psi.impl.OnjFloatPsi
import org.onj.language.psi.impl.OnjFunctionCallPsi
import org.onj.language.psi.impl.OnjFunctionNamePsi
import org.onj.language.psi.impl.OnjGroupedValuePsi
import org.onj.language.psi.impl.OnjImportPathPsi
import org.onj.language.psi.impl.OnjImportStructurePsi
import org.onj.language.psi.impl.OnjInfixFunctionCallPsi
import org.onj.language.psi.impl.OnjIntPsi
import org.onj.language.psi.impl.OnjKeyPsi
import org.onj.language.psi.impl.OnjKeyValuePairPsi
import org.onj.language.psi.impl.OnjNamedObjectNamePsi
import org.onj.language.psi.impl.OnjNamedObjectPsi
import org.onj.language.psi.impl.OnjNegationPsi
import org.onj.language.psi.impl.OnjObjectPsi
import org.onj.language.psi.impl.OnjStringPsi
import org.onj.language.psi.impl.OnjTopLevelPsi
import org.onj.language.psi.impl.OnjTripleDotPsi
import org.onj.language.psi.impl.OnjUseStructurePsi
import org.onj.language.psi.impl.OnjVarStructurePsi
import org.onj.language.psi.impl.OnjVariableAccessorPsi
import org.onj.language.psi.impl.OnjVariableDeclNamePsi
import org.onj.language.psi.impl.OnjVariableUsePsi

object OnjTypes {

    @JvmField val FILE: IElementType = OnjElementType("FILE")
    @JvmField val TOP_LEVEL: IElementType = OnjElementType("TOP_LEVEL")
    @JvmField val INT_LITERAL: IElementType = OnjElementType("INT_LITERAL")
    @JvmField val FLOAT_LITERAL: IElementType = OnjElementType("FLOAT_LITERAL")
    @JvmField val STRING: IElementType = OnjElementType("STRING")
    @JvmField val IMPORT_STRUCTURE: IElementType = OnjElementType("IMPORT_STRUCTURE")
    @JvmField val USE_STRUCTURE: IElementType = OnjElementType("USE_STRUCTURE")
    @JvmField val VAR_STRUCTURE: IElementType = OnjElementType("VAR_STRUCTURE")
    @JvmField val AS_CONTEXT_DEPENDENT_KEYWORD: IElementType = OnjElementType("AS_CONTEXT_DEPENDENT_KEYWORD")
    @JvmField val GROUPED_VALUE: IElementType = OnjElementType("GROUPED_VALUE")
    @JvmField val ACCESS: IElementType = OnjElementType("ACCESS")
    @JvmField val VARIABLE_USE: IElementType = OnjElementType("VARIABLE_USE")
    @JvmField val NEGATION: IElementType = OnjElementType("NEGATION")
    @JvmField val CONVERSION: IElementType = OnjElementType("CONVERSION")
    @JvmField val BINARY_OPERATION: IElementType = OnjElementType("BINARY_OPERATION")
    @JvmField val INFIX_FUNCTION_CALL: IElementType = OnjElementType("INFIX_FUNCTION_CALL")
    @JvmField val TRIPLE_DOT: IElementType = OnjElementType("TRIPLE_DOT")
    @JvmField val KEY_VALUE_PAIR: IElementType = OnjElementType("KEY_VALUE_PAIR")
    @JvmField val OBJECT: IElementType = OnjElementType("OBJECT")
    @JvmField val ARRAY: IElementType = OnjElementType("ARRAY")
    @JvmField val NAMED_OBJECT: IElementType = OnjElementType("NAMED_OBJECT")
    @JvmField val FUNCTION_CALL: IElementType = OnjElementType("FUNCTION_CALL")

    @JvmField val KEY: IElementType = OnjElementType("KEY")
    @JvmField val FUNCTION_NAME: IElementType = OnjElementType("FUNCTION_NAME")
    @JvmField val VARIABLE_DECL_NAME: IElementType = OnjElementType("VARIABLE_DECL_NAME")
    @JvmField val NAMED_OBJECT_NAME: IElementType = OnjElementType("NAMED_OBJECT_NAME")
    @JvmField val VARIABLE_ACCESSOR: IElementType = OnjElementType("VARIABLE_ACCESSOR")
    @JvmField val ARRAY_ENTRY: IElementType = OnjElementType("ARRAY_ENTRY")
    @JvmField val IMPORT_PATH: IElementType = OnjElementType("IMPORT_PATH")

    @JvmField val BLOCK_COMMENT: IElementType = OnjTokenType("BLOCK_COMMENT")
    @JvmField val COLON: IElementType = OnjTokenType("COLON")
    @JvmField val COMMA: IElementType = OnjTokenType("COMMA")
    @JvmField val DIV: IElementType = OnjTokenType("DIV")
    @JvmField val DOLLAR: IElementType = OnjTokenType("DOLLAR")
    @JvmField val DOT: IElementType = OnjTokenType("DOT")
    @JvmField val EQUALS: IElementType = OnjTokenType("EQUALS")
    @JvmField val FLOAT: IElementType = OnjTokenType("FLOAT")
    @JvmField val HASH: IElementType = OnjTokenType("HASH")
    @JvmField val IDENTIFIER: IElementType = OnjTokenType("IDENTIFIER")
    @JvmField val IMPORT: IElementType = OnjTokenType("IMPORT")
    @JvmField val INTEGER: IElementType = OnjTokenType("INTEGER")
    @JvmField val INVALID_STRING_ESCAPE: IElementType = OnjTokenType("INVALID_STRING_ESCAPE")
    @JvmField val LINE_COMMENT: IElementType = OnjTokenType("LINE_COMMENT")
    @JvmField val L_BRACE: IElementType = OnjTokenType("L_BRACE")
    @JvmField val L_BRACKET: IElementType = OnjTokenType("L_BRACKET")
    @JvmField val L_PAREN: IElementType = OnjTokenType("L_PAREN")
    @JvmField val MINUS: IElementType = OnjTokenType("MINUS")
    @JvmField val PLUS: IElementType = OnjTokenType("PLUS")
    @JvmField val R_BRACE: IElementType = OnjTokenType("R_BRACE")
    @JvmField val R_BRACKET: IElementType = OnjTokenType("R_BRACKET")
    @JvmField val R_PAREN: IElementType = OnjTokenType("R_PAREN")
    @JvmField val SEMICOLON: IElementType = OnjTokenType("SEMICOLON")
    @JvmField val STAR: IElementType = OnjTokenType("STAR")
    @JvmField val STRING_BEGIN: IElementType = OnjTokenType("STRING_BEGIN")
    @JvmField val STRING_END: IElementType = OnjTokenType("STRING_END")
    @JvmField val STRING_ESCAPE: IElementType = OnjTokenType("STRING_ESCAPE")
    @JvmField val STRING_PART: IElementType = OnjTokenType("STRING_PART")
    @JvmField val USE: IElementType = OnjTokenType("USE")
    @JvmField val VAR: IElementType = OnjTokenType("VAR")

    fun createElement(node: ASTNode?): PsiElement {
        if (node == null) throw RuntimeException("node is null")
        return when (node.elementType) {
            STRING -> OnjStringPsi(node)
            INT_LITERAL -> OnjIntPsi(node)
            FLOAT_LITERAL -> OnjFloatPsi(node)
            FILE -> OnjFilePsi(node)
            TOP_LEVEL -> OnjTopLevelPsi(node)
            IMPORT_STRUCTURE -> OnjImportStructurePsi(node)
            AS_CONTEXT_DEPENDENT_KEYWORD -> OnjAsContextDependentKeywordPsi(node)
            USE_STRUCTURE -> OnjUseStructurePsi(node)
            VAR_STRUCTURE -> OnjVarStructurePsi(node)
            GROUPED_VALUE -> OnjGroupedValuePsi(node)
            ACCESS -> OnjAccessPsi(node)
            VARIABLE_USE -> OnjVariableUsePsi(node)
            NEGATION -> OnjNegationPsi(node)
            CONVERSION -> OnjConversionPsi(node)
            BINARY_OPERATION -> OnjBinaryOperationPsi(node)
            INFIX_FUNCTION_CALL -> OnjInfixFunctionCallPsi(node)
            TRIPLE_DOT -> OnjTripleDotPsi(node)
            KEY_VALUE_PAIR -> OnjKeyValuePairPsi(node)
            OBJECT -> OnjObjectPsi(node)
            ARRAY -> OnjArrayPsi(node)
            NAMED_OBJECT -> OnjNamedObjectPsi(node)
            FUNCTION_CALL -> OnjFunctionCallPsi(node)
            KEY -> OnjKeyPsi(node)
            FUNCTION_NAME -> OnjFunctionNamePsi(node)
            VARIABLE_DECL_NAME -> OnjVariableDeclNamePsi(node)
            NAMED_OBJECT_NAME -> OnjNamedObjectNamePsi(node)
            VARIABLE_ACCESSOR -> OnjVariableAccessorPsi(node)
            ARRAY_ENTRY -> OnjArrayEntryPsi(node)
            IMPORT_PATH -> OnjImportPathPsi(node)
            else -> throw RuntimeException("unknown node type ${node.elementType}")
        }
    }

}