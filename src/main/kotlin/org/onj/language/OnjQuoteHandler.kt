package org.onj.language

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import org.onj.language.psi.OnjTypes

class OnjQuoteHandler : SimpleTokenSetQuoteHandler(OnjTypes.STRING_BEGIN, OnjTypes.STRING_END)
