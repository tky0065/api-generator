// intellij-plugin/src/main/java/com/apigenerator/intellij/generators/TestGenerator.java
package com.apigenerator.intellij.generators;

import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.models.EntityModel;
import com.apigenerator.models.FieldModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class TestGenerator {
    private final Configuration freemarkerConfig;

    public TestGenerator() {
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        this.freemarkerConfig.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(), "templates/test"
        );
    }

    @SneakyThrows
    public String generateRepositoryTest(EntityModel entity, GeneratorConfig config) {
        Template template = freemarkerConfig.getTemplate("repository-test.ftl");
        Map<String, Object> model = createBaseModel(entity, config);
        model.put("searchMethods", generateSearchMethods(entity));

        StringWriter writer = new StringWriter();
        template.process(model, writer);
        return writer.toString();
    }

    @SneakyThrows
    public String generateServiceTest(EntityModel entity, GeneratorConfig config) {
        Template template = freemarkerConfig.getTemplate("service-test.ftl");
        Map<String, Object> model = createBaseModel(entity, config);

        StringWriter writer = new StringWriter();
        template.process(model, writer);
        return writer.toString();
    }

    @SneakyThrows
    public String generateControllerTest(EntityModel entity, GeneratorConfig config) {
        Template template = freemarkerConfig.getTemplate("controller-test.ftl");
        Map<String, Object> model = createBaseModel(entity, config);
        model.put("hasFileUpload", hasFileFields(entity));

        StringWriter writer = new StringWriter();
        template.process(model, writer);
        return writer.toString();
    }

    private Map<String, Object> createBaseModel(EntityModel entity, GeneratorConfig config) {
        Map<String, Object> model = new HashMap<>();
        model.put("entity", entity);
        model.put("config", config);
        model.put("testData", generateTestData(entity));
        return model;
    }

    private Map<String, String> generateTestData(EntityModel entity) {
        Map<String, String> testData = new HashMap<>();
        for (FieldModel field : entity.getFields()) {
            testData.put(field.getName(), generateTestValue(field));
        }
        return testData;
    }

    private String generateTestValue(FieldModel field) {
        switch (field.getType()) {
            case "String":
                return "\"test" + field.getName() + "\"";
            case "Integer":
            case "int":
                return "1";
            case "Long":
            case "long":
                return "1L";
            case "Boolean":
            case "boolean":
                return "true";
            case "Double":
            case "double":
                return "1.0";
            case "Float":
            case "float":
                return "1.0f";
            case "BigDecimal":
                return "BigDecimal.ONE";
            case "LocalDate":
                return "LocalDate.now()";
            case "LocalDateTime":
                return "LocalDateTime.now()";
            case "byte[]":
                return "new byte[]{1}";
            default:
                return "null";
        }
    }

    private List<String> generateSearchMethods(EntityModel entity) {
        return entity.getFields().stream()
                .filter(f -> !f.isPrimary() && f.getRelationshipType() == null)
                .map(f -> String.format("findBy%s", capitalize(f.getName())))
                .collect(Collectors.toList());
    }

    private boolean hasFileFields(EntityModel entity) {
        return entity.getFields().stream()
                .anyMatch(f -> f.getType().equals("byte[]") ||
                        f.getType().equals("Blob") ||
                        f.getType().equals("File"));
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}