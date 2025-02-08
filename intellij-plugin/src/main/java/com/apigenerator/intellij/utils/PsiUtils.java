package com.apigenerator.intellij.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class PsiUtils {

    public static PsiClass findClass(String qualifiedName, Project project) {
        return JavaPsiFacade.getInstance(project)
                .findClass(qualifiedName, GlobalSearchScope.allScope(project));
    }

    public static String getPackageName(@NotNull PsiClass psiClass) {
        PsiFile containingFile = psiClass.getContainingFile();
        if (containingFile instanceof PsiJavaFile) {
            return ((PsiJavaFile) containingFile).getPackageName();
        }
        return "";
    }

    public static boolean hasAnnotation(PsiModifierListOwner element, String annotationName) {
        return element.getModifierList() != null &&
                element.getModifierList().findAnnotation(annotationName) != null;
    }

    public static String getAnnotationValue(PsiModifierListOwner element, String annotationName, String attribute) {
        if (element.getModifierList() == null) return null;

        PsiAnnotation annotation = element.getModifierList().findAnnotation(annotationName);
        if (annotation == null) return null;

        PsiAnnotationMemberValue value = annotation.findAttributeValue(attribute);
        if (value == null) return null;

        return value.getText().replace("\"", "");
    }

    public static void createPackageIfNotExists(String packageName, PsiDirectory baseDir) throws IncorrectOperationException {
        String[] parts = packageName.split("\\.");
        PsiDirectory current = baseDir;

        for (String part : parts) {
            PsiDirectory subDir = current.findSubdirectory(part);
            if (subDir == null) {
                current = current.createSubdirectory(part);
            } else {
                current = subDir;
            }
        }
    }
}