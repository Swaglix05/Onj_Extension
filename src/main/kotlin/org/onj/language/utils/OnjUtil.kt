package org.onj.language.utils

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiSearchHelper
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import org.onj.language.OnjFileType
import org.onj.language.psi.OnjFile
//import org.onj.language.psi.OnjObjectEntry
//import org.onj.language.psi.OnjString
//import org.onj.language.psi.OnjTypes
//import org.onj.language.psi.OnjNamedElement
//
//object OnjUtil {
//
//    fun findNamedElementDeclarations(
//        project: Project,
//        key: String,
//        curFile: OnjFile?,
//        checkWithinFile: (PsiElement) -> Boolean = { it is OnjNamedElement && it.elementType == OnjTypes.VARIABLE_DECLARATION_NAME }
//    ): List<OnjNamedElement> {
//        if (curFile != null) {
//            val def: List<OnjNamedElement> =
//                getChildrenRecursiveOf(curFile)
//                    .filter { checkWithinFile(it) }
//                    .filter { it.name == key }
//            if (def.isNotEmpty()) return def
//        }
//
//        val result: MutableList<OnjNamedElement> = mutableListOf()
//        val files = findAffectedFilesWithKey(project, key, curFile)
//        for (onjFile in files) {
//            val properties: List<OnjNamedElement> = getChildrenRecursiveOf(onjFile)
//            result.addAll(properties.filter { it is OnjObjectEntry }.filter { it.name == key })
//        }
//        return result
//    }
//
//    fun getChildrenRecursiveOf(
//        element: PsiElement,
//        checkChildrenOfType: Boolean = false,
//    ): List<OnjNamedElement> {
//        val res = mutableListOf<OnjNamedElement>()
//        if (element is OnjNamedElement) return listOf(element)
//        element.children.forEach { res.addAll(getChildrenRecursiveOf(it, checkChildrenOfType)) }
//        return res
//    }
//
//    fun findNamedElementDeclarations(project: Project): List<OnjNamedElement> {
//        val result: MutableList<OnjNamedElement> = mutableListOf()
//        val virtualFiles = FileTypeIndex.getFiles(OnjFileType, GlobalSearchScope.allScope(project))
//        for (virtualFile in virtualFiles) {
//            val onjFile = PsiManager.getInstance(project).findFile(virtualFile) as OnjFile?
//            if (onjFile == null) continue
//            result.addAll(getChildrenRecursiveOf(onjFile))
//        }
//        return result
//    }
//
//
//    /**
//     * first are the files, that have no "as ..." after the import, second are the files with the "as ...", important for accessing stuff
//     */
//    fun findAffectedFilesWithKey(
//        project: Project,
//        word: String,
//        curFile: OnjFile?,
//    ): List<OnjFile> {
//        val files = PsiSearchHelper
//            .getInstance(project)
//            .findFilesWithPlainTextWords(word)
//            .filterIsInstance<OnjFile>()
////        GlobalSearchScope.everythingScope(project)
////        FileTypeIndex.getFiles(OnjFileType, GlobalSearchScope.everythingScope(project))
//        if (curFile == null) return files
//        val imports = getImports(curFile)
//        return files.filter { it2 -> imports.any { it2.virtualFile.canonicalPath?.endsWith(it) == true } }
//    }
//
//    private fun getImports(file: OnjFile): MutableList<String> {
//        val res: MutableList<String> = mutableListOf()
//        var curTopLevel = file.firstChild
//        while (curTopLevel != null) {
//            if (curTopLevel.firstChild.elementType == OnjTypes.IMPORT_STRUCTURE) {
//                val curElement = PsiTreeUtil.getChildOfType<OnjString>(curTopLevel.firstChild, OnjString::class.java)
//                if (curElement != null) {
//                    res.add(curElement.text.substring(1, curElement.text.length - 1))
//                }
//            }
//            curTopLevel = curTopLevel.nextSibling
//        }
//        return res
//    }
//}