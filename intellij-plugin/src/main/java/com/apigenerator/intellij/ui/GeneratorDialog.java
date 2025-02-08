// intellij-plugin/src/main/java/com/apigenerator/intellij/ui/GeneratorDialog.java
package com.apigenerator.intellij.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.apigenerator.core.GeneratorConfig;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class GeneratorDialog extends DialogWrapper {
    private final PsiClass psiClass;
    private JCheckBox generateDtosCheckBox;
    private JCheckBox generateMappersCheckBox;
    private JCheckBox generateTestsCheckBox;
    private JCheckBox useJakartaEECheckBox;
    private JCheckBox useLombokCheckBox;
    private JCheckBox useMapstructCheckBox;
    private JCheckBox generateFileUploadCheckBox;
    private JCheckBox generateLiquibaseCheckBox;

    public GeneratorDialog(Project project, PsiClass psiClass) {
        super(project);
        this.psiClass = psiClass;
        init();
        setTitle("Generate API Configuration");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new GridLayout(0, 1, 5, 5));

        generateDtosCheckBox = createCheckBox("Generate DTOs", true);
        generateMappersCheckBox = createCheckBox("Generate Mappers", true);
        generateTestsCheckBox = createCheckBox("Generate Tests", true);
        useJakartaEECheckBox = createCheckBox("Use Jakarta EE", false);
        useLombokCheckBox = createCheckBox("Use Lombok", true);
        useMapstructCheckBox = createCheckBox("Use MapStruct", true);
        generateFileUploadCheckBox = createCheckBox("Generate File Upload", false);
        generateLiquibaseCheckBox = createCheckBox("Generate Liquibase", true);

        dialogPanel.add(generateDtosCheckBox);
        dialogPanel.add(generateMappersCheckBox);
        dialogPanel.add(generateTestsCheckBox);
        dialogPanel.add(useJakartaEECheckBox);
        dialogPanel.add(useLombokCheckBox);
        dialogPanel.add(useMapstructCheckBox);
        dialogPanel.add(generateFileUploadCheckBox);
        dialogPanel.add(generateLiquibaseCheckBox);

        return dialogPanel;
    }

    private JCheckBox createCheckBox(String text, boolean selected) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setSelected(selected);
        return checkBox;
    }

    public GeneratorConfig getConfiguration() {
        return GeneratorConfig.builder()
                .generateDtos(generateDtosCheckBox.isSelected())
                .generateMappers(generateMappersCheckBox.isSelected())
                .generateTests(generateTestsCheckBox.isSelected())
                .useJakartaEE(useJakartaEECheckBox.isSelected())
                .useLombok(useLombokCheckBox.isSelected())
                .useMapstruct(useMapstructCheckBox.isSelected())
                .generateFileUpload(generateFileUploadCheckBox.isSelected())
                .generateLiquibase(generateLiquibaseCheckBox.isSelected())
                .build();
    }
}