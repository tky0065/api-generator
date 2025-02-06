// common/src/main/java/com/apigenerator/core/GeneratorConfig.java
package com.apigenerator.core;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class GeneratorConfig {
    private String basePackage;

    @Builder.Default
    private boolean generateDtos = true;

    @Builder.Default
    private boolean generateMappers = true;

    @Builder.Default
    private boolean generateTests = true;

    @Builder.Default
    private boolean useJakartaEE = false;

    @Builder.Default
    private boolean useLombok = true;

    @Builder.Default
    private boolean useMapstruct = true;

    @Builder.Default
    private boolean generateFileUpload = false;

    @Builder.Default
    private boolean generateLiquibase = true;

    @Builder.Default
    private String dtoSuffix = "DTO";

    @Builder.Default
    private String mapperSuffix = "Mapper";

    @Builder.Default
    private String serviceSuffix = "Service";

    @Builder.Default
    private String repositorySuffix = "Repository";


    public boolean isGenerateFileUpload() {
        return generateFileUpload;
    }

    public void setGenerateFileUpload(boolean generateFileUpload) {
        this.generateFileUpload = generateFileUpload;
    }
}