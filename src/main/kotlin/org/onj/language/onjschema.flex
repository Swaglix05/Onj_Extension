// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.onj.language;

import com.intellij.psi.tree.IElementType;
import org.onj.language.psi2.OnjSchemaTypes;
import com.intellij.psi.TokenType;

%%
%public
%class OnjSchemaLexer
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

STRING = "string"
FLOAT = "float"
INT = "int"
BOOLEAN = "boolean"

IDENTIFIER = [\p{L}_]+[\p{L}_0-9]*

COLON = ":"
COMMA = ","
STAR = "*"
DOT = "."
DOLLAR = "$"
SEMICOLON = ";"
EQUALS = "="
R_BRACE = "}"
L_BRACE = "{"
R_BRACKET = "]"
L_BRACKET = "["
R_PAREN = ")"
L_PAREN = "("

%state STRING_DOUBLE_QUOTE
%state STRING_SINGLE_QUOTE

%%

<YYINITIAL> ({CRLF}|{WHITE_SPACE})+                            { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<YYINITIAL> {LINE_COMMENT}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.LINE_COMMENT; }
<YYINITIAL> {BLOCK_COMMENT}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.BLOCK_COMMENT; }

<YYINITIAL> {STRING}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.STRING; }
<YYINITIAL> {FLOAT}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.FLOAT; }
<YYINITIAL> {INT}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.INT; }
<YYINITIAL> {BOOLEAN}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.BOOLEAN; }

<YYINITIAL> {IMPORT}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.IMPORT; }
<YYINITIAL> {USE}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.USE; }
<YYINITIAL> {VAR}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.VAR; }
<YYINITIAL> {IDENTIFIER}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.IDENTIFIER; }
<YYINITIAL> {COLON}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.COLON; }
<YYINITIAL> {COMMA}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.COMMA; }
<YYINITIAL> {STAR}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.STAR; }
<YYINITIAL> {DOT}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.DOT; }
<YYINITIAL> {DOLLAR}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.DOLLAR; }
<YYINITIAL> {SEMICOLON}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.SEMICOLON; }
<YYINITIAL> {EQUALS}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.EQUALS; }
<YYINITIAL> {R_BRACE}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.R_BRACE; }
<YYINITIAL> {L_BRACE}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.L_BRACE; }
<YYINITIAL> {R_BRACKET}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.R_BRACKET; }
<YYINITIAL> {L_BRACKET}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.L_BRACKET; }
<YYINITIAL> {R_PAREN}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.R_PAREN; }
<YYINITIAL> {L_PAREN}                                     { yybegin(YYINITIAL); return OnjSchemaTypes.L_PAREN; }

[^]                                                         { return TokenType.BAD_CHARACTER; }