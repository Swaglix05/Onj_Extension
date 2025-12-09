package org.onj.language

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import org.onj.language.psi.impl.OnjTypes

class NewOnjParser : PsiParser {

    override fun parse(
        root: IElementType,
        builder: PsiBuilder
    ): ASTNode {
        builder.setDebugMode(true)

        val fileMarker = builder.mark()
        parseTopLevel(builder)
        fileMarker.done(OnjTypes.FILE)

        return builder.treeBuilt
    }

    private fun parseTopLevel(builder: PsiBuilder) {
        var allowKeyValue = true
        while (!builder.eof()) {
//            println("top level: ${builder.next()}")
            val result = when {
                builder.nextIs(OnjTypes.IMPORT) -> parseImport(builder)
                builder.nextIs(OnjTypes.USE) -> parseUse(builder)
                builder.nextIs(OnjTypes.VAR) -> parseVar(builder)
                builder.nextIs(OnjTypes.DOT) -> {
                    if (!allowKeyValue) {
                        builder.error("Expected: comma")
                    }
                    val result = parseTripleDot(builder)
                    if (builder.nextIs(OnjTypes.COMMA)) {
                        builder.advanceLexer()
                        allowKeyValue = true
                    } else {
                        allowKeyValue = false
                    }
                    result
                }
                builder.nextIsOneOf(OnjTypes.IDENTIFIER, OnjTypes.STRING_BEGIN) -> {
                    if (!allowKeyValue) {
                        builder.error("Expected: comma")
                    }
                    val result = parseKeyValuePair(builder)
                    if (builder.nextIs(OnjTypes.COMMA)) {
                        builder.advanceLexer()
                        allowKeyValue = true
                    } else {
                        allowKeyValue = false
                    }
                    result
                }
                else -> {
                    builder.advanceLexer()
                    builder.error("Unexpected: '${builder.next()}'; Expected top level declaration")
                    false
                }
            }
            if (result) continue
            while (!builder.eof()) {
//                println("recover: ${builder.next()}")
                if (builder.nextIsOneOf(OnjTypes.IMPORT, OnjTypes.VAR, OnjTypes.USE, OnjTypes.IDENTIFIER)) {
                    break
                }
                if (builder.nextIsOneOf(OnjTypes.SEMICOLON, OnjTypes.COMMA)) {
                    builder.advanceLexer()
                    break
                }
                builder.advanceLexer()
            }
        }
    }

    private fun parseKeyValuePair(builder: PsiBuilder): Boolean {
        val begin = builder.mark()
        val keyBegin = builder.mark()
        if (builder.nextIs(OnjTypes.STRING_BEGIN)) {
            val result = parseString(builder)
            if (!result) {
                keyBegin.drop()
                begin.drop()
                return false
            }
        } else if (builder.nextIs(OnjTypes.IDENTIFIER)) {
            builder.advanceLexer()
        } else {
            builder.advanceLexer()
            builder.error("Expected: identifier")
            keyBegin.drop()
            begin.drop()
            return false
        }
        keyBegin.done(OnjTypes.KEY)
        if (builder.nextIsNot(OnjTypes.COLON)) {
            builder.advanceLexer()
            builder.error("Expected: colon")
            begin.drop()
            return false
        }
        builder.advanceLexer()
        val result = parseValue(builder)
        if (!result) {
            begin.drop()
            return false
        }
        begin.done(OnjTypes.KEY_VALUE_PAIR)
        return true
    }

    private fun parseTripleDot(builder: PsiBuilder): Boolean {
        val begin = builder.mark()
        repeat(3) {
            if (builder.nextIs(OnjTypes.DOT)) {
                builder.advanceLexer()
                return@repeat
            }
            builder.advanceLexer()
            builder.error("Expected: three dots")
            begin.drop()
            return false
        }
        val result = parseLiteral(builder)
        if (!result) {
            begin.drop()
            return false
        }
        begin.done(OnjTypes.TRIPLE_DOT)
        return true
    }

    private fun parseVar(builder: PsiBuilder): Boolean {
        if (builder.nextIsNot(OnjTypes.VAR)) {
            builder.advanceLexer()
            builder.error("Unexpected: ${builder.next()}")
            return false
        }
        val varMark = builder.mark()
        builder.advanceLexer()
        val nameMarker = builder.mark()
        if (builder.nextIsNot(OnjTypes.IDENTIFIER)) {
            builder.advanceLexer()
            builder.error("Expected: identifier")
            nameMarker.drop()
            varMark.drop()
            return false
        }
        builder.advanceLexer()
        nameMarker.done(OnjTypes.VARIABLE_DECL_NAME)
        if (builder.nextIsNot(OnjTypes.EQUALS)) {
            builder.advanceLexer()
            builder.error("Expected: equals")
            varMark.drop()
            return false
        }
        builder.advanceLexer()
        val result = parseValue(builder)
        if (!result) {
            varMark.drop()
            return false
        }
        if (builder.nextIsNot(OnjTypes.SEMICOLON)) {
            builder.advanceLexer()
            builder.error("Expected: semicolon")
            varMark.done(OnjTypes.VAR_STRUCTURE)
            return false
        }
        builder.advanceLexer()
        varMark.done(OnjTypes.VAR_STRUCTURE)
        return true
    }

    private fun parseUse(builder: PsiBuilder): Boolean {
        if (builder.nextIsNot(OnjTypes.USE)) {
            builder.error("Unexpected: ${builder.next()}")
            builder.advanceLexer()
            return false
        }
        val useMark = builder.mark()
        builder.advanceLexer()
        if (builder.nextIsNot(OnjTypes.IDENTIFIER)) {
            builder.advanceLexer()
            builder.error("Expected: identifier")
            useMark.drop()
            return false
        }
        builder.advanceLexer()
        if (builder.nextIsNot(OnjTypes.SEMICOLON)) {
            builder.advanceLexer()
            builder.error("Expected: semicolon")
            useMark.done(OnjTypes.USE_STRUCTURE)
            return false
        }
        useMark.done(OnjTypes.USE_STRUCTURE)
        return true
    }

    private fun parseImport(builder: PsiBuilder): Boolean {
        if (builder.nextIsNot(OnjTypes.IMPORT)) {
            builder.error("Unexpected: ${builder.next()}")
            builder.advanceLexer()
            return false
        }
        val importMark = builder.mark()
        builder.advanceLexer()
        val result = parseLiteral(builder)
        if (!result) {
            importMark.drop()
            return false
        }
        if (builder.nextIsNot(OnjTypes.IDENTIFIER) && builder.tokenText == "as") {
            builder.advanceLexer()
            builder.error("Expected: 'as'")
            importMark.drop()
            return false
        }
        val asMark = builder.mark()
        builder.advanceLexer()
        asMark.done(OnjTypes.AS_CONTEXT_DEPENDENT_KEYWORD)
        val nameMarker = builder.mark()
        if (builder.nextIsNot(OnjTypes.IDENTIFIER)) {
            builder.advanceLexer()
            builder.error("Expected: identifier")
            nameMarker.drop()
            importMark.drop()
            return false
        }
        builder.advanceLexer()
        nameMarker.done(OnjTypes.VARIABLE_DECL_NAME)
        if (builder.nextIsNot(OnjTypes.SEMICOLON)) {
            builder.advanceLexer()
            builder.error("Expected: semicolon")
            importMark.done(OnjTypes.IMPORT_STRUCTURE)
            return false
        }
        builder.advanceLexer()
        importMark.done(OnjTypes.IMPORT_STRUCTURE)
        return true
    }

    private fun parseValue(builder: PsiBuilder): Boolean = parseInfixFunctionCall(builder)

    private fun parseInfixFunctionCall(builder: PsiBuilder): Boolean {
        val begin = builder.mark()
        val result = parseTerm(builder)
        if (!result) {
            begin.drop()
            return false
        }
        var currentMarker = begin
        while (builder.nextIs(OnjTypes.IDENTIFIER)) {
            val nameMarker = builder.mark()
            builder.advanceLexer()
            nameMarker.done(OnjTypes.FUNCTION_NAME)
            val result = parseTerm(builder)
            if (!result) {
                currentMarker.drop()
                return false
            }
            currentMarker.done(OnjTypes.INFIX_FUNCTION_CALL)
            currentMarker = currentMarker.precede()
        }
        currentMarker.drop()
        return true
    }

    private fun parseTerm(builder: PsiBuilder): Boolean {
        val begin = builder.mark()
        val result = parseFactor(builder)
        if (!result) {
            begin.drop()
            return false
        }
        var currentMarker = begin
        while (builder.nextIsOneOf(OnjTypes.PLUS, OnjTypes.MINUS)) {
            builder.advanceLexer()
            val result = parseFactor(builder)
            if (!result) {
                currentMarker.drop()
                return false
            }
            currentMarker.done(OnjTypes.BINARY_OPERATION)
            currentMarker = currentMarker.precede()
        }
        currentMarker.drop()
        return true
    }

    private fun parseFactor(builder: PsiBuilder): Boolean {
        val begin = builder.mark()
        val result = parseTypeConversion(builder)
        if (!result) {
            begin.drop()
            return false
        }
        var currentMarker = begin
        while (builder.nextIsOneOf(OnjTypes.STAR, OnjTypes.DIV)) {
            builder.advanceLexer()
            val result = parseTypeConversion(builder)
            if (!result) {
                currentMarker.drop()
                return false
            }
            currentMarker.done(OnjTypes.BINARY_OPERATION)
            currentMarker = currentMarker.precede()
        }
        currentMarker.drop()
        return true
    }

    private fun parseTypeConversion(builder: PsiBuilder): Boolean {
        val begin = builder.mark()
        val result = parseNegation(builder)
        if (!result) {
            begin.drop()
            return false
        }
        var currentMarker = begin
        while (builder.nextIs(OnjTypes.HASH)) {
            builder.advanceLexer()
            val nameMarker = builder.mark()
            if (builder.nextIsNot(OnjTypes.IDENTIFIER)) {
                builder.advanceLexer()
                builder.error("Expected identifier after conversion")
                nameMarker.drop()
                currentMarker.drop()
                return false
            }
            builder.advanceLexer()
            nameMarker.done(OnjTypes.CONVERSION_NAME)
            currentMarker.done(OnjTypes.CONVERSION)
            currentMarker = currentMarker.precede()
        }
        currentMarker.drop()
        return true
    }

    private fun parseNegation(builder: PsiBuilder): Boolean {
        if (builder.nextIsNot(OnjTypes.MINUS)) return parseVariableAccess(builder)
        val begin = builder.mark()
        builder.advanceLexer()
        val result = parseNegation(builder)
        if (!result) {
            begin.drop()
            return false
        }
        begin.done(OnjTypes.NEGATION)
        return true
    }

    private fun parseVariableAccess(builder: PsiBuilder): Boolean {
        val begin = builder.mark()
        val result = parseLiteral(builder)
        if (!result) {
            begin.drop()
            return false
        }
        var currentMarker = begin
        while (builder.nextIs(OnjTypes.DOT)) {
            builder.advanceLexer()
            val accessorMarker = builder.mark()
            val result = parseLiteral(builder)
            if (!result) {
                accessorMarker.drop()
                currentMarker.drop()
                return false
            }
            accessorMarker.done(OnjTypes.VARIABLE_ACCESSOR)
            currentMarker.done(OnjTypes.ACCESS)
            currentMarker = currentMarker.precede()
        }
        currentMarker.drop()
        return true
    }

    private fun parseLiteral(builder: PsiBuilder): Boolean {
        val result = when {
            builder.nextIs(OnjTypes.STRING_BEGIN) -> parseString(builder)
            builder.nextIs(OnjTypes.INTEGER) -> {
                val intMark = builder.mark()
                builder.advanceLexer()
                intMark.done(OnjTypes.INT_LITERAL)
                true
            }
            builder.nextIs(OnjTypes.FLOAT) -> {
                val floatMark = builder.mark()
                builder.advanceLexer()
                floatMark.done(OnjTypes.FLOAT_LITERAL)
                true
            }
            builder.nextIs(OnjTypes.IDENTIFIER) -> {
                val varMark = builder.mark()
                builder.advanceLexer()
                if (builder.nextIs(OnjTypes.L_PAREN)) {
                    parseFunctionCall(varMark, builder)
                } else {
                    varMark.done(OnjTypes.VARIABLE_USE)
                    true
                }
            }
            builder.nextIs(OnjTypes.L_BRACE) -> {
                parseObject(builder)
                true
            }
            builder.nextIs(OnjTypes.L_BRACKET) -> {
                parseArray(builder)
                true
            }
            builder.nextIs(OnjTypes.DOLLAR) -> parseNamedObject(builder)
            builder.nextIs(OnjTypes.L_PAREN) -> {
                val groupMark = builder.mark()
                builder.advanceLexer()
                val result = parseValue(builder)
                if (!result) {
                    groupMark.drop()
                    false
                } else {
                    if (builder.nextIs(OnjTypes.R_PAREN)) {
                        builder.advanceLexer()
                        groupMark.done(OnjTypes.GROUPED_VALUE)
                        true
                    } else {
                        builder.advanceLexer()
                        builder.error("Expected: closing paren")
                        groupMark.drop()
                        false
                    }
                }
            }
            else -> {
                val last = builder.next()
                builder.error("Unexpected Token: '$last'; expected literal")
                builder.advanceLexer()
                false
            }
        }
        return result
    }

    private fun parseFunctionCall(mark: PsiBuilder.Marker, builder: PsiBuilder): Boolean {
        val begin = mark.precede()
        mark.done(OnjTypes.FUNCTION_NAME)
        if (builder.nextIsNot(OnjTypes.L_PAREN)) {
            builder.advanceLexer()
            builder.error("Expected: left paren")
            begin.drop()
            return false
        }
        builder.advanceLexer()
        while (!builder.eof()) {
            if (builder.nextIs(OnjTypes.R_PAREN)) {
                builder.advanceLexer()
                break
            }
            val result = parseValue(builder)
            if (!result) {
                while (!builder.eof()) {
                    if (builder.nextIsOneOf(OnjTypes.COMMA, OnjTypes.R_PAREN)) break
                    builder.advanceLexer()
                }
            }
            if (builder.nextIsNot(OnjTypes.COMMA) && builder.nextIsNot(OnjTypes.R_PAREN)) {
                builder.error("Expected comma after parameter")
            }
            if (builder.nextIs(OnjTypes.COMMA)) builder.advanceLexer()
            if (builder.eof()) builder.error("Missing closing paren for function call")
        }
        begin.done(OnjTypes.FUNCTION_CALL)
        return true
    }

    private fun parseNamedObject(builder: PsiBuilder): Boolean {
        if (builder.nextIsNot(OnjTypes.DOLLAR)) {
            builder.advanceLexer()
            builder.error("Expected: dollar")
            return false
        }
        val begin = builder.mark()
        builder.advanceLexer()
        val nameMarker = builder.mark()
        if (builder.nextIsNot(OnjTypes.IDENTIFIER)) {
            builder.advanceLexer()
            builder.error("Expected: name of named object")
            nameMarker.drop()
            begin.drop()
            return false
        }
        builder.advanceLexer()
        nameMarker.done(OnjTypes.NAMED_OBJECT_NAME)
        parseObject(builder)
        begin.done(OnjTypes.NAMED_OBJECT)
        return true
    }

    private fun parseObject(builder: PsiBuilder) {
        if (builder.nextIsNot(OnjTypes.L_BRACE)) {
            builder.advanceLexer()
            builder.error("Expected: opening brace")
            return
        }
        val begin = builder.mark()
        builder.advanceLexer()
        while (!builder.eof()) {
            if (builder.nextIs(OnjTypes.R_BRACE)) {
                builder.advanceLexer()
                break
            }
            val result = when {
                builder.nextIs(OnjTypes.DOT) -> parseTripleDot(builder)
                builder.nextIsOneOf(OnjTypes.IDENTIFIER, OnjTypes.STRING_BEGIN) -> parseKeyValuePair(builder)
                else -> {
                    builder.error("Unexpected: '${builder.next()}'; Expected object enty")
                    builder.advanceLexer()
                    false
                }
            }
            if (!result) {
                while (!builder.eof()) {
                    if (builder.nextIsOneOf(OnjTypes.COMMA, OnjTypes.R_BRACE, OnjTypes.IDENTIFIER)) break
                    builder.advanceLexer()
                }
            }
            if (builder.nextIsNot(OnjTypes.COMMA) && builder.nextIsNot(OnjTypes.R_BRACE)) {
                builder.error("Expected comma after key-value pair")
            }
            if (builder.nextIs(OnjTypes.COMMA)) builder.advanceLexer()
            if (builder.eof()) builder.error("Missing closing brace for object")
        }
        begin.done(OnjTypes.OBJECT)
    }

    private fun parseArray(builder: PsiBuilder) {
        if (builder.nextIsNot(OnjTypes.L_BRACKET)) {
            builder.advanceLexer()
            builder.error("Expected: opening bracket")
            return
        }
        val begin = builder.mark()
        builder.advanceLexer()
        while (!builder.eof()) {
            if (builder.nextIs(OnjTypes.R_BRACKET)) {
                builder.advanceLexer()
                break
            }
            val result = when {
                builder.nextIs(OnjTypes.DOT) -> parseTripleDot(builder)
                else -> parseValue(builder)
            }
            if (!result) {
                while (!builder.eof()) {
                    if (builder.nextIsOneOf(OnjTypes.COMMA, OnjTypes.R_BRACKET)) break
                    builder.advanceLexer()
                }
            }
            if (builder.nextIsNot(OnjTypes.COMMA) && builder.nextIsNot(OnjTypes.R_BRACKET)) {
                builder.error("Expected comma after value")
            }
            if (builder.nextIs(OnjTypes.COMMA)) builder.advanceLexer()
            if (builder.eof()) builder.error("Missing closing bracket for array")
        }
        begin.done(OnjTypes.ARRAY)
    }

    private fun parseString(builder: PsiBuilder): Boolean {
        if (builder.nextIsNot(OnjTypes.STRING_BEGIN)) {
            builder.advanceLexer()
            builder.error("Unexpected: ${builder.next()}")
            return false
        }
        val strMark = builder.mark()
        builder.advanceLexer()
        while (builder.nextIsNot(OnjTypes.STRING_END)) {
            if (!builder.nextIsOneOf(OnjTypes.STRING_ESCAPE, OnjTypes.STRING_PART, OnjTypes.INVALID_STRING_ESCAPE)) {
                builder.error("Malformed string")
                strMark.done(OnjTypes.STRING)
                return false
            }
            builder.advanceLexer()
        }
        builder.advanceLexer()
        strMark.done(OnjTypes.STRING)
        return true
    }

    private fun PsiBuilder.next() = tokenType

    private fun PsiBuilder.nextIs(type: IElementType) = next() == type
    private fun PsiBuilder.nextIsNot(type: IElementType) = next() != type

    private fun PsiBuilder.nextIsOneOf(vararg types: IElementType): Boolean {
        val type = tokenType
        types.forEach { if (it == type) return true }
        return false
    }
}
