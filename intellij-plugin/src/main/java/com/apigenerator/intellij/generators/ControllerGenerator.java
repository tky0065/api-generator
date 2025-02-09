package com.apigenerator.intellij.generators;// common/src/main/java/com/apigenerator/generators/ControllerGenerator.java


import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.models.EntityModel;
import com.apigenerator.models.FieldModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ControllerGenerator {
    private final Configuration freemarkerConfig;

    public ControllerGenerator() {
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        this.freemarkerConfig.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(), "templates"
        );
    }

    public String generate(EntityModel entity, GeneratorConfig config) {
        try {
            Template template = freemarkerConfig.getTemplate("controller.ftl");
            Map<String, Object> model = createModel(entity, config);

            StringWriter writer = new StringWriter();
            template.process(model, writer);

            return writer.toString();
        } catch (Exception e) {
            log.error("Error generating controller for " + entity.getClassName(), e);
            throw new RuntimeException("Failed to generate controller", e);
        }
    }

    public String getOutputPath(EntityModel entity, GeneratorConfig config) {
        return String.format("%s/web/rest/%sResource.java",
                entity.getPackageName().replace(".", "/"),
                entity.getClassName()
        );
    }

    private Map<String, Object> createModel(EntityModel entity, GeneratorConfig config) {
        Map<String, Object> model = new HashMap<>();
        model.put("entity", entity);
        model.put("config", config);
        model.put("hasFileUpload", hasFileFields(entity));
        model.put("idType", getIdType(entity));
        model.put("apiPath", generateApiPath(entity.getClassName()));
        return model;
    }

    private boolean hasFileFields(EntityModel entity) {
        return entity.getFields().stream()
                .anyMatch(field ->
                        field.getType().equals("byte[]") ||
                                field.getType().equals("Blob") ||
                                field.getType().equals("File")
                );
    }

    private String getIdType(EntityModel entity) {
        return entity.getFields().stream()
                .filter(FieldModel::isPrimary)
                .findFirst()
                .map(FieldModel::getType)
                .orElse("Long");
    }

    private String generateApiPath(String className) {
        return "/api/" + className.toLowerCase() + "s";
    }
}