// intellij-plugin/src/main/java/com/apigenerator/intellij/config/PluginConfig.java
package com.apigenerator.intellij.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@State(
        name = "APIGeneratorPluginSettings",
        storages = @Storage(id = "", file = "api-generator-settings.xml")
)
public class PluginConfig implements PersistentStateComponent<PluginConfig> {
    private String basePackage = "com.example";
    private boolean useJakartaEE = false;
    private boolean useLombok = true;
    private boolean useMapstruct = true;
    private boolean generateDtos = true;
    private boolean generateTests = true;
    private boolean generateLiquibase = true;
    private String authorName = "";

    public static PluginConfig getInstance(@NotNull Project project) {
        return project.getComponent(PluginConfig.class);
    }

    @Override
    public @Nullable PluginConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PluginConfig state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}