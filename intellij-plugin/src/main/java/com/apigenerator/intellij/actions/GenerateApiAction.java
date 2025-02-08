// intellij-plugin/src/main/java/com/apigenerator/intellij/actions/GenerateApiAction.java
package com.apigenerator.intellij.actions;

import com.apigenerator.intellij.ui.GeneratorDialog;
import com.apigenerator.intellij.processor.GenerateApiProcessor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.actionSystem.DataKeys.PROJECT;
import static com.intellij.openapi.actionSystem.DataKeys.PSI_FILE;

public class GenerateApiAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getData(PROJECT);
        PsiFile psiFile = e.getData(PSI_FILE);

        boolean enabled = project != null &&
                psiFile instanceof PsiJavaFile &&
                hasEntityAnnotation((PsiJavaFile) psiFile);

        e.getPresentation().setEnabled(enabled);
        e.getPresentation().setVisible(enabled);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(PROJECT);
        PsiFile psiFile = e.getData(PSI_FILE);

        if (project == null || !(psiFile instanceof PsiJavaFile)) {
            return;
        }

        PsiJavaFile javaFile = (PsiJavaFile) psiFile;
        PsiClass[] classes = javaFile.getClasses();

        if (classes.length > 0 && hasEntityAnnotation(classes[0])) {
            showGeneratorDialog(project, classes[0]);
        }
    }

    private boolean hasEntityAnnotation(PsiJavaFile file) {
        return file.getClasses().length > 0 && hasEntityAnnotation(file.getClasses()[0]);
    }

    private boolean hasEntityAnnotation(PsiClass psiClass) {
        return psiClass.getModifierList() != null &&
                (psiClass.getModifierList().findAnnotation("javax.persistence.Entity") != null ||
                        psiClass.getModifierList().findAnnotation("jakarta.persistence.Entity") != null);
    }

    private void showGeneratorDialog(Project project, PsiClass psiClass) {
        GeneratorDialog dialog = new GeneratorDialog(project, psiClass);
        if (dialog.showAndGet()) {
            new GenerateApiProcessor(project, psiClass, dialog.getConfiguration()).generate();
        }
    }
}