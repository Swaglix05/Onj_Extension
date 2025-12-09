package org.onj.language.psi;

import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.model.psi.PsiSymbolReference;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
//import org.onj.language.OnjVariableSymbolReference;
//import org.onj.language.psi2.OnjTypes;
//import org.onj.language.psi2.OnjVariableExpression;
//import org.onj.language.reference.OnjElementFactory;
//
//import javax.swing.*;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.LinkedList;
//
//public class OnjPsiImplUtils {
//
//    public static @NotNull Collection<? extends @NotNull PsiSymbolReference> getOwnReferences(OnjVariableExpression element) {
//        ArrayList<PsiSymbolReference> toRet = new ArrayList<>();
//        toRet.add(new OnjVariableSymbolReference(element));
//        return toRet;
//    }
//
//    /**
//     * Attempts to collect any comment elements above the element
//     */
//    public static @NotNull String findDocumentationComment(OnjDocumentableElement property) {
//        LinkedList<String> result = new LinkedList<>();
//        PsiElement element = property.getPrevSibling();
//        while (element instanceof PsiComment || element instanceof PsiWhiteSpace) {
//            if (element instanceof PsiComment) {
//                String commentText = element.getText().replaceFirst("[!# *]+", "");
//                result.add(commentText);
//            }
//            element = element.getPrevSibling();
//        }
//        return StringUtil.join(Lists.reverse(result), "\n ");
//    }
//
//
//    private final static IElementType[] CHILDREN_WITH_NAME_TYPES = new IElementType[]{
//            OnjTypes.FUNCTION_NAME,
//            OnjTypes.NAMED_OBJECT_NAME,
//            OnjTypes.VARIABLE_DECLARATION_NAME,
//    };
//
//    public static String getName(OnjNamedElement element) {
//        for (IElementType type : CHILDREN_WITH_NAME_TYPES) {
//            ASTNode valueNode = element.getNode().findChildByType(type);
//            if (valueNode != null) {
//                return valueNode.getText();
//            }
//        }
//        ASTNode valueNode = element.getNode().findChildByType(OnjTypes.IDENTIFIER);
//        return valueNode != null ? valueNode.getText() : null;
//    }
//
//
//    public static PsiElement setName(OnjNamedElement element, String newName) {
//        for (IElementType type : CHILDREN_WITH_NAME_TYPES) {
//            ASTNode cur = element.getNode().findChildByType(type);
//            if (cur instanceof OnjNamedElement) {
//                setName((OnjNamedElement) cur, newName);
//                return element;
//            }
//        }
//        ASTNode keyNode = element.getNode().findChildByType(OnjTypes.IDENTIFIER);
//        if (keyNode != null) {
//            OnjNamedElement property = OnjElementFactory.INSTANCE.createOnjVariableStructure(element.getProject(), newName);
//            ASTNode newKeyNode = property.getFirstChild().getNode();
//            element.getNode().replaceChild(keyNode, newKeyNode);
//        }
//        return element;
//    }
//
//    public static PsiElement getNameIdentifier(OnjNamedElement element) {
//        for (IElementType type : CHILDREN_WITH_NAME_TYPES) {
//            ASTNode cur = element.getNode().findChildByType(type);
//            if (cur != null) {
//                return cur.getPsi();
//            }
//        }
//        ASTNode keyNode = element.getNode().findChildByType(OnjTypes.IDENTIFIER);
//        return keyNode != null ? keyNode.getPsi() : null;
//    }
//
//    public static ItemPresentation getPresentation(final OnjNamedElement element) {
//        return new ItemPresentation() {
//            @Nullable
//            @Override
//            public String getPresentableText() {
//                return element.getText();
//            }
//
//            @Nullable
//            @Override
//            public String getLocationString() {
//                PsiFile containingFile = element.getContainingFile();
//                return containingFile == null ? null : containingFile.getName();
//            }
//
//            @Override
//            public Icon getIcon(boolean unused) {
//                return element.getIcon(0);
//            }
//        };
//    }

//
//    public static PsiReference getReference(PsiElement o) {
//        System.out.println("trying to get reference");
//        PsiReference [] references = o.getReferences();
//        return references.length == 0 ? null : references[0];
//    }
//
//    public static  PsiReference[] getReferences(PsiElement o) {
//        System.out.println("getReferences is called on element $name");
//        return ReferenceProvidersRegistry.getReferencesFromProviders(o);
//    }
//
//}
//override fun getReference(): PsiReference? {
//println("trying to get reference")
//val references = getReferences()
//        return if (references.isEmpty()) null else references[0]
//        }
//
////    override fun getOwnReferences(): Collection<out PsiSymbolReference> {
////        return super.getOwnReferences()
////    }
//
//override fun getReferences(): Array<PsiReference> {
//    println("getReferences is called on element $name")
//    return ReferenceProvidersRegistry.getReferencesFromProviders(this);
//}