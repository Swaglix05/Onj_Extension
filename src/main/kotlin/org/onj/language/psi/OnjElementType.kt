package org.onj.language.psi

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.NotNull
import org.onj.language.language.OnjLanguage

class OnjElementType(@NonNls debugName: String) : IElementType(debugName, OnjLanguage)
