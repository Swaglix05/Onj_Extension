package org.onj.language.psi

import com.intellij.psi.tree.IElementType;
import org.onj.language.language.OnjLanguage
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

class OnjTokenType(@NonNls debugName: String) :
    IElementType(debugName, OnjLanguage) {
    override fun toString(): String {
        return "OnjTokenType." + super.toString()
    }
}
