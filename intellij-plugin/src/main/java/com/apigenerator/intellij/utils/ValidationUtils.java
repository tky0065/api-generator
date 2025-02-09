package com.apigenerator.intellij.utils;

import com.apigenerator.intellij.utils.PsiUtils;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;

public class ValidationUtils {

    public static boolean isValidEntity(@NotNull PsiClass psiClass) {
        // Vérifier si la classe a l'annotation @Entity
        boolean hasEntityAnnotation = PsiUtils.hasAnnotation(psiClass, "javax.persistence.Entity") ||
                PsiUtils.hasAnnotation(psiClass, "jakarta.persistence.Entity");

        // Vérifier si la classe a un ID
        boolean hasId = false;
        for (PsiField field : psiClass.getAllFields()) {
            if (PsiUtils.hasAnnotation(field, "javax.persistence.Id") ||
                    PsiUtils.hasAnnotation(field, "jakarta.persistence.Id")) {
                hasId = true;
                break;
            }
        }

        return hasEntityAnnotation && hasId;
    }

    public static String validatePackageName(@NotNull String packageName) {
        if (packageName.isEmpty()) {
            return "Package name cannot be empty";
        }
        if (!packageName.matches("^[a-z][a-z0-9]*(\\.[a-z][a-z0-9]*)*$")) {
            return "Invalid package name format";
        }
        return null;
    }

    public static String validateClassName(@NotNull String className) {
        if (className.isEmpty()) {
            return "Class name cannot be empty";
        }
        if (!className.matches("^[A-Z][a-zA-Z0-9]*$")) {
            return "Invalid class name format";
        }
        return null;
    }
}