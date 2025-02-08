// intellij-plugin/src/main/java/com/apigenerator/intellij/config/PluginConfig.java
package com.apigenerator.intellij.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "ApiGeneratorSettings",
        storages = @Storage("api-generator.xml")
)
@Data
public class PluginConfig implements PersistentStateComponent<PluginConfig> {
    private String basePackage = "com.example";
    private boolean useJakartaEE = false;
    private boolean useLombok = true;
    private boolean useMapstruct = true;
    private boolean generateDtos = true;
    private boolean generateTests = true;
    private boolean generateLiquibase = true;
    private String authorName = "";

    @Override
    public @Nullable PluginConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PluginConfig state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}