package com.apigenerator.intellij.generators;

import com.intellij.openapi.project.Project;
import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.models.EntityModel;
import com.apigenerator.models.FieldModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
public class RepositoryGenerator extends CodeGenerator {
    private final Configuration freemarkerConfig;

    public RepositoryGenerator(Project project) {
        super(project);
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        this.freemarkerConfig.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(), "templates"
        );
    }
    public void generateRepository(EntityModel entity, GeneratorConfig config) {
        try {
            String content = generateRepositoryContent(entity, config);
            String repositoryName = entity.getClassName() + "Repository";
            String packageName = entity.getPackageName() + ".repository";

            createJavaFile(packageName, repositoryName, content);
            log.info("Generated repository for {}", entity.getClassName());
        } catch (Exception e) {
            log.error("Error generating repository for " + entity.getClassName(), e);
            throw new RuntimeException("Failed to generate repository", e);
        }
    }

    private String generateRepositoryContent(EntityModel entity, GeneratorConfig config) throws Exception {
        Template template = freemarkerConfig.getTemplate("repository.ftl");
        Map<String, Object> model = createModel(entity, config);

        StringWriter writer = new StringWriter();
        template.process(model, writer);
        return writer.toString();
    }
    private Map<String, Object> createModel(EntityModel entity, GeneratorConfig config) {
        Map<String, Object> model = new HashMap<>();
        model.put("entity", entity);
        model.put("config", config);
        model.put("searchMethods", generateSearchMethods(entity));
        return model;
    }
    private List<String> generateSearchMethods(EntityModel entity) {
        List<String> methods = new ArrayList<>();

        // Méthodes de recherche par champs simples
        entity.getFields().stream()
                .filter(field -> !field.isPrimary() && field.getRelationshipType() == null)
                .forEach(field -> {
                    methods.add(generateFindByMethod(entity, field));
                    methods.add(generateFindByInMethod(entity, field));
                });

        // Méthodes de recherche par relations
        entity.getFields().stream()
                .filter(field -> field.getRelationshipType() != null)
                .forEach(field -> methods.add(generateFindByRelationMethod(entity, field)));

        return methods;
    }

    private String generateFindByMethod(EntityModel entity, FieldModel field) {
        return String.format("Optional<%s> findBy%s(%s %s)",
                entity.getClassName(),
                capitalize(field.getName()),
                field.getType(),
                field.getName()
        );
    }

    private String generateFindByInMethod(EntityModel entity, FieldModel field) {
        return String.format("List<%s> findBy%sIn(List<%s> %sList)",
                entity.getClassName(),
                capitalize(field.getName()),
                field.getType(),
                field.getName()
        );
    }

    private String generateFindByRelationMethod(EntityModel entity, FieldModel field) {
        String returnType = field.getRelationshipType().isToMany() ?
                "List<" + entity.getClassName() + ">" :
                "Optional<" + entity.getClassName() + ">";

        return String.format("%s findBy%s(%s %s)",
                returnType,
                capitalize(field.getName()),
                field.getTargetEntity(),
                field.getName()
        );
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}