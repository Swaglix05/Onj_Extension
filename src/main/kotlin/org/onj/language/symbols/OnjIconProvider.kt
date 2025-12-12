package org.onj.language.symbols

import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import org.onj.language.OnjIcons
import org.onj.language.psi.impl.OnjArrayPsi
import org.onj.language.psi.impl.OnjFilePsi
import org.onj.language.psi.impl.OnjKeyValuePairPsi
import org.onj.language.psi.impl.OnjObjectPsi
import javax.swing.Icon

class OnjIconProvider : IconProvider() {

    override fun getIcon(p0: PsiElement, p1: Int): Icon? = when (p0) {
        is OnjFilePsi -> OnjIcons.FILE
        is OnjObjectPsi -> OnjIcons.OBJECT
        is OnjArrayPsi -> OnjIcons.ARRAY
        is OnjKeyValuePairPsi -> OnjIcons.KEY
        else -> null
    }

}
