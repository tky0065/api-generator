// common/src/main/java/com/apigenerator/generators/ControllerGenerator.java
package com.apigenerator.generators;

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
public class ControllerGenerator implements CodeGenerator {

    private final Configuration freemarkerConfig;

    public ControllerGenerator() {
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        this.freemarkerConfig.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(), "templates"
        );
    }

    @Override
    @SneakyThrows
    public String generate(EntityModel entity, GeneratorConfig config) {
        log.info("Génération du Controller pour l'entité: {}", entity.getClassName());

        Template template = freemarkerConfig.getTemplate("controller.ftl");
        Map<String, Object> model = createTemplateModel(entity, config);

        StringWriter writer = new StringWriter();
        template.process(model, writer);

        if (config.isGenerateFileUpload() && hasFileFields(entity)) {
            Template uploadTemplate = freemarkerConfig.getTemplate("file-controller.ftl");
            StringWriter uploadWriter = new StringWriter();
            uploadTemplate.process(model, uploadWriter);
            return writer.toString() + "\n\n" + uploadWriter.toString();
        }

        return writer.toString();
    }

    @Override
    public String getOutputPath(EntityModel entity, GeneratorConfig config) {
        return String.format("%s/web/rest/%sResource.java",
                entity.getPackageName().replace(".", "/"),
                entity.getClassName()
        );
    }

    private Map<String, Object> createTemplateModel(EntityModel entity, GeneratorConfig config) {
        Map<String, Object> model = new HashMap<>();
        model.put("entity", entity);
        model.put("config", config);

        // Identifier le champ ID
        FieldModel idField = entity.getFields().stream()
                .filter(FieldModel::isPrimary)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No primary key found in entity " + entity.getClassName()));
        model.put("idField", idField);

        // Obtenir les champs pour la recherche
        List<FieldModel> searchableFields = entity.getFields().stream()
                .filter(f -> !f.isPrimary() && f.getRelationshipType() == null)
                .collect(Collectors.toList());
        model.put("searchableFields", searchableFields);

        // Configuration de l'API
        model.put("apiPath", generateApiPath(entity.getClassName()));
        model.put("hasFileUpload", hasFileFields(entity));

        return model;
    }

    private String generateApiPath(String className) {
        return "/api/" + className.toLowerCase() + "s";
    }

    private boolean hasFileFields(EntityModel entity) {
        return entity.getFields().stream()
                .anyMatch(field -> field.getType().equals("byte[]") ||
                        field.getType().equals("Blob") ||
                        field.getType().equals("File"));
    }
}