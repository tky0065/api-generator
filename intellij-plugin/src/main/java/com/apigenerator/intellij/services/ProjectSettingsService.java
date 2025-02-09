package com.apigenerator.intellij.services;

import com.intellij.openapi.project.Project;
import com.apigenerator.intellij.config.PluginConfig;
import com.apigenerator.core.GeneratorConfig;
import org.jetbrains.annotations.NotNull;

public class ProjectSettingsService {
    private final Project project;
    private final PluginConfig pluginConfig;

    public ProjectSettingsService(@NotNull Project project) {
        this.project = project;
        this.pluginConfig = project.getComponent(PluginConfig.class);
    }

    @NotNull
    public GeneratorConfig createGeneratorConfig() {
        return GeneratorConfig.builder()
                .basePackage(pluginConfig.getBasePackage())
                .useJakartaEE(pluginConfig.isUseJakartaEE())
                .useLombok(pluginConfig.isUseLombok())
                .useMapstruct(pluginConfig.isUseMapstruct())
                .generateDtos(pluginConfig.isGenerateDtos())
                .generateTests(pluginConfig.isGenerateTests())
                .generateLiquibase(pluginConfig.isGenerateLiquibase())
                .build();
    }
}