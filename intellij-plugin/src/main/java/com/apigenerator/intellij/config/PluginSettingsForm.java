// intellij-plugin/src/main/java/com/apigenerator/intellij/config/PluginSettingsForm.java
package com.apigenerator.intellij.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PluginSettingsForm implements Configurable {
    private JPanel mainPanel;
    private JTextField basePackageField;
    private JCheckBox useJakartaEECheckBox;
    private JCheckBox useLombokCheckBox;
    private JCheckBox useMapstructCheckBox;
    private JCheckBox generateDtosCheckBox;
    private JCheckBox generateTestsCheckBox;
    private JCheckBox generateLiquibaseCheckBox;
    private JTextField authorNameField;
    private final PluginConfig settings;

    public PluginSettingsForm(PluginConfig settings) {
        this.settings = settings;
        initComponents();
    }

    private void initComponents() {
        mainPanel = new JPanel();
        basePackageField = new JTextField(settings.getBasePackage());
        useJakartaEECheckBox = new JCheckBox("Use Jakarta EE", settings.isUseJakartaEE());
        useLombokCheckBox = new JCheckBox("Use Lombok", settings.isUseLombok());
        useMapstructCheckBox = new JCheckBox("Use MapStruct", settings.isUseMapstruct());
        generateDtosCheckBox = new JCheckBox("Generate DTOs", settings.isGenerateDtos());
        generateTestsCheckBox = new JCheckBox("Generate Tests", settings.isGenerateTests());
        generateLiquibaseCheckBox = new JCheckBox("Generate Liquibase", settings.isGenerateLiquibase());
        authorNameField = new JTextField(settings.getAuthorName());

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(createLabeledComponent("Base Package:", basePackageField));
        mainPanel.add(useJakartaEECheckBox);
        mainPanel.add(useLombokCheckBox);
        mainPanel.add(useMapstructCheckBox);
        mainPanel.add(generateDtosCheckBox);
        mainPanel.add(generateTestsCheckBox);
        mainPanel.add(generateLiquibaseCheckBox);
        mainPanel.add(createLabeledComponent("Author Name:", authorNameField));
    }

    private JPanel createLabeledComponent(String label, JComponent component) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(new JLabel(label));
        panel.add(Box.createHorizontalStrut(10));
        panel.add(component);
        return panel;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "API Generator Settings";
    }

    @Override
    public @Nullable Icon getIcon() {
        return null;
    }

    @Override
    public @Nullable String getHelpTopic() {
        return "";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        return !settings.getBasePackage().equals(basePackageField.getText()) ||
                settings.isUseJakartaEE() != useJakartaEECheckBox.isSelected() ||
                settings.isUseLombok() != useLombokCheckBox.isSelected() ||
                settings.isUseMapstruct() != useMapstructCheckBox.isSelected() ||
                settings.isGenerateDtos() != generateDtosCheckBox.isSelected() ||
                settings.isGenerateTests() != generateTestsCheckBox.isSelected() ||
                settings.isGenerateLiquibase() != generateLiquibaseCheckBox.isSelected() ||
                !settings.getAuthorName().equals(authorNameField.getText());
    }

    @Override
    public void apply() {
        settings.setBasePackage(basePackageField.getText());
        settings.setUseJakartaEE(useJakartaEECheckBox.isSelected());
        settings.setUseLombok(useLombokCheckBox.isSelected());
        settings.setUseMapstruct(useMapstructCheckBox.isSelected());
        settings.setGenerateDtos(generateDtosCheckBox.isSelected());
        settings.setGenerateTests(generateTestsCheckBox.isSelected());
        settings.setGenerateLiquibase(generateLiquibaseCheckBox.isSelected());
        settings.setAuthorName(authorNameField.getText());
    }

    @Override
    public void reset() {
        basePackageField.setText(settings.getBasePackage());
        useJakartaEECheckBox.setSelected(settings.isUseJakartaEE());
        useLombokCheckBox.setSelected(settings.isUseLombok());
        useMapstructCheckBox.setSelected(settings.isUseMapstruct());
        generateDtosCheckBox.setSelected(settings.isGenerateDtos());
        generateTestsCheckBox.setSelected(settings.isGenerateTests());
        generateLiquibaseCheckBox.setSelected(settings.isGenerateLiquibase());
        authorNameField.setText(settings.getAuthorName());
    }

    @Override
    public void disposeUIResources() {

    }
}