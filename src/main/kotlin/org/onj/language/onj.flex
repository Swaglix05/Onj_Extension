// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.onj.language;

import com.intellij.psi.tree.IElementType;
import org.onj.language.psi2.OnjTypes;
import com.intellij.psi.TokenType;

%%
%public
%class OnjLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=\R
WHITE_SPACE = [\ \n\t\f]+?

LINE_COMMENT = "//".*?{CRLF}
BLOCK_COMMENT = "/*"(.|{CRLF})*?"*/"

IMPORT = "import"
USE = "use"
VAR = "var"

IDENTIFIER = [\p{L}_]+[\p{L}_0-9]*

INTEGER = ("0b"[10_]+)|("0o"[0-8_]+)|("0x"[0-9a-fA-F_]+)|([0-9_]+)
FLOAT = [0-9_]+"."[0-9_]+

COLON = ":"
COMMA = ","
PLUS = "+"
MINUS = "-"
STAR = "*"
DIV = "/"
DOT = "."
DOLLAR = "$"
SEMICOLON = ";"
EQUALS = "="
HASH = "#"
R_BRACE = "}"
L_BRACE = "{"
R_BRACKET = "]"
L_BRACKET = "["
R_PAREN = ")"
L_PAREN = "("

DOUBLE_QUOTE = \"
SINGLE_QUOTE = \'

STRING_CONTENT = [^\"\'\\\r]*?
//STRING_CONTENT = [^\"\'\\]*?
ESCAPE_SEQUENCE = \\n|\\r|\\t|\\\"|\\\'|\\\\
INVALID_ESCAPE = \\.

%state STRING_DOUBLE_QUOTE
%state STRING_SINGLE_QUOTE

%%

<YYINITIAL> ({CRLF}|{WHITE_SPACE})+                            { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<YYINITIAL> {LINE_COMMENT}                                     { yybegin(YYINITIAL); return OnjTypes.LINE_COMMENT; }
<YYINITIAL> {BLOCK_COMMENT}                                     { yybegin(YYINITIAL); return OnjTypes.BLOCK_COMMENT; }

<YYINITIAL> {IMPORT}                                     { yybegin(YYINITIAL); return OnjTypes.IMPORT; }
<YYINITIAL> {USE}                                     { yybegin(YYINITIAL); return OnjTypes.USE; }
<YYINITIAL> {VAR}                                     { yybegin(YYINITIAL); return OnjTypes.VAR; }

<YYINITIAL> {IDENTIFIER}                                     { yybegin(YYINITIAL); return OnjTypes.IDENTIFIER; }
<YYINITIAL> {INTEGER}                                     { yybegin(YYINITIAL); return OnjTypes.INTEGER; }
<YYINITIAL> {FLOAT}                                     { yybegin(YYINITIAL); return OnjTypes.FLOAT; }

<YYINITIAL> {COLON}                                     { yybegin(YYINITIAL); return OnjTypes.COLON; }
<YYINITIAL> {COMMA}                                     { yybegin(YYINITIAL); return OnjTypes.COMMA; }
<YYINITIAL> {PLUS}                                     { yybegin(YYINITIAL); return OnjTypes.PLUS; }
<YYINITIAL> {MINUS}                                     { yybegin(YYINITIAL); return OnjTypes.MINUS; }
<YYINITIAL> {STAR}                                     { yybegin(YYINITIAL); return OnjTypes.STAR; }
<YYINITIAL> {DIV}                                     { yybegin(YYINITIAL); return OnjTypes.DIV; }
<YYINITIAL> {DOT}                                     { yybegin(YYINITIAL); return OnjTypes.DOT; }
<YYINITIAL> {DOLLAR}                                     { yybegin(YYINITIAL); return OnjTypes.DOLLAR; }
<YYINITIAL> {SEMICOLON}                                     { yybegin(YYINITIAL); return OnjTypes.SEMICOLON; }
<YYINITIAL> {EQUALS}                                     { yybegin(YYINITIAL); return OnjTypes.EQUALS; }
<YYINITIAL> {HASH}                                     { yybegin(YYINITIAL); return OnjTypes.HASH; }
<YYINITIAL> {R_BRACE}                                     { yybegin(YYINITIAL); return OnjTypes.R_BRACE; }
<YYINITIAL> {L_BRACE}                                     { yybegin(YYINITIAL); return OnjTypes.L_BRACE; }
<YYINITIAL> {R_BRACKET}                                     { yybegin(YYINITIAL); return OnjTypes.R_BRACKET; }
<YYINITIAL> {L_BRACKET}                                     { yybegin(YYINITIAL); return OnjTypes.L_BRACKET; }
<YYINITIAL> {R_PAREN}                                     { yybegin(YYINITIAL); return OnjTypes.R_PAREN; }
<YYINITIAL> {L_PAREN}                                     { yybegin(YYINITIAL); return OnjTypes.L_PAREN; }

<YYINITIAL> {DOUBLE_QUOTE}                                     { yybegin(STRING_DOUBLE_QUOTE); return OnjTypes.STRING_BEGIN; }
<YYINITIAL> {SINGLE_QUOTE}                                     { yybegin(STRING_SINGLE_QUOTE); return OnjTypes.STRING_BEGIN; }

<STRING_DOUBLE_QUOTE> {
    {DOUBLE_QUOTE}   { yybegin(YYINITIAL); return OnjTypes.STRING_END; }
    {ESCAPE_SEQUENCE} { yybegin(STRING_DOUBLE_QUOTE); return OnjTypes.STRING_ESCAPE; }
    {INVALID_ESCAPE} { yybegin(STRING_DOUBLE_QUOTE); return OnjTypes.INVALID_STRING_ESCAPE; }
    {SINGLE_QUOTE} { yybegin(STRING_DOUBLE_QUOTE); return OnjTypes.STRING_PART; }
    {STRING_CONTENT} { yybegin(STRING_DOUBLE_QUOTE); return OnjTypes.STRING_PART; }
}

<STRING_SINGLE_QUOTE> {
    {SINGLE_QUOTE}   { yybegin(YYINITIAL); return OnjTypes.STRING_END; }
    {ESCAPE_SEQUENCE} { yybegin(STRING_SINGLE_QUOTE); return OnjTypes.STRING_ESCAPE; }
    {INVALID_ESCAPE} { yybegin(STRING_SINGLE_QUOTE); return OnjTypes.INVALID_STRING_ESCAPE; }
    {DOUBLE_QUOTE} { yybegin(STRING_SINGLE_QUOTE); return OnjTypes.STRING_PART; }
    {STRING_CONTENT} { yybegin(STRING_SINGLE_QUOTE); return OnjTypes.STRING_PART; }
}

[^]                                                         { return TokenType.BAD_CHARACTER; }