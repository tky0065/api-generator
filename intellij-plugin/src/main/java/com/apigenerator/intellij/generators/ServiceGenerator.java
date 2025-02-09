package com.apigenerator.intellij.generators;

import com.intellij.openapi.project.Project;
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
public class ServiceGenerator extends CodeGenerator {
    private final Configuration freemarkerConfig;

    public ServiceGenerator(Project project) {
        super(project);
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        this.freemarkerConfig.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(), "templates"
        );
    }

    public void generateService(EntityModel entity, GeneratorConfig config) {
        try {
            // Génération de l'interface du service
            String interfaceContent = generateServiceInterface(entity, config);
            String serviceName = entity.getClassName() + "Service";
            String packageName = entity.getPackageName() + ".service";
            createJavaFile(packageName, serviceName, interfaceContent);

            // Génération de l'implémentation
            String implContent = generateServiceImpl(entity, config);
            String implName = entity.getClassName() + "ServiceImpl";
            String implPackage = packageName + ".impl";
            createJavaFile(implPackage, implName, implContent);

            log.info("Generated service and implementation for {}", entity.getClassName());
        } catch (Exception e) {
            log.error("Error generating service for " + entity.getClassName(), e);
            throw new RuntimeException("Failed to generate service", e);
        }
    }

    private String generateServiceInterface(EntityModel entity, GeneratorConfig config) throws Exception {
        Template template = freemarkerConfig.getTemplate("service-interface.ftl");
        Map<String, Object> model = createModel(entity, config);

        StringWriter writer = new StringWriter();
        template.process(model, writer);
        return writer.toString();
    }

    private String generateServiceImpl(EntityModel entity, GeneratorConfig config) throws Exception {
        Template template = freemarkerConfig.getTemplate("service-impl.ftl");
        Map<String, Object> model = createModel(entity, config);

        StringWriter writer = new StringWriter();
        template.process(model, writer);
        return writer.toString();
    }

    private Map<String, Object> createModel(EntityModel entity, GeneratorConfig config) {
        Map<String, Object> model = new HashMap<>();
        model.put("entity", entity);
        model.put("config", config);
        model.put("idType", getIdType(entity));
        model.put("hasFileFields", hasFileFields(entity));
        return model;
    }

    private String getIdType(EntityModel entity) {
        return entity.getFields().stream()
                .filter(FieldModel::isPrimary)
                .findFirst()
                .map(FieldModel::getType)
                .orElse("Long");
    }

    private boolean hasFileFields(EntityModel entity) {
        return entity.getFields().stream()
                .anyMatch(field ->
                        field.getType().equals("byte[]") ||
                                field.getType().equals("Blob") ||
                                field.getType().equals("File")
                );
    }
}