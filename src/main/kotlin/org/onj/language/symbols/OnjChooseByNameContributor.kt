package org.onj.language.symbols

import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.navigation.NavigationItem
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Processor
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.indexing.FindSymbolParameters
import com.intellij.util.indexing.IdFilter
//import org.onj.language.psi.OnjNamedElement
//import org.onj.language.utils.OnjUtil
//
//class OnjChooseByNameContributor : ChooseByNameContributorEx {
//    override fun processNames(
//        processor: Processor<in String>,
//        scope: GlobalSearchScope,
//        id: IdFilter?
//    ) {
//        val project = scope.project ?: return
//        ContainerUtil.process(
//            ContainerUtil.map(
//                OnjUtil.findNamedElementDeclarations(project),
//                OnjNamedElement::getName
//            ), processor
//        )
//    }
//
//    override fun processElementsWithName(
//        name: String,
//        processor: Processor<in NavigationItem>,
//        params: FindSymbolParameters
//    ) {
//        ContainerUtil.process(
//            ContainerUtil.map(
//                OnjUtil.findNamedElementDeclarations(params.project, name, null),
//                { it as NavigationItem }), processor
//        )
//    }
//
//}