
package com.apigenerator.generators;

import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.models.EntityModel;
import com.apigenerator.models.FieldModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class RepositoryGenerator implements CodeGenerator {

    private final Configuration freemarkerConfig;

    public RepositoryGenerator() {
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        this.freemarkerConfig.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(), "templates"
        );
    }

    @Override
    @SneakyThrows
    public String generate(EntityModel entity, GeneratorConfig config) {
        log.info("Génération du Repository pour l'entité: {}", entity.getClassName());

        Template template = freemarkerConfig.getTemplate("repository.ftl");
        Map<String, Object> model = new HashMap<>();
        model.put("entity", entity);
        model.put("config", config);
        model.put("searchMethods", generateSearchMethods(entity));

        // Trouver le type de la clé primaire
        Optional<FieldModel> primaryKeyField = entity.getFields().stream()
                .filter(FieldModel::isPrimary)
                .findFirst();

        primaryKeyField.ifPresent(field -> model.put("primaryKeyType", field.getType()));
        // Si aucun type de clé primaire n'est trouvé, on met "Long" par défaut.
        if(!primaryKeyField.isPresent()){
            model.put("primaryKeyType", "Long");
        }

        StringWriter writer = new StringWriter();
        template.process(model, writer);

        return writer.toString();
    }


    @Override
    public String getOutputPath(EntityModel entity, GeneratorConfig config) {
        return String.format("%s/repository/%sRepository.java",
                entity.getPackageName().replace(".", "/"),
                entity.getClassName()
        );
    }

    private List<String> generateSearchMethods(EntityModel entity) {
        List<String> methods = new ArrayList<>();

        // Génération des méthodes de recherche pour chaque champ unique
        for (FieldModel field : entity.getFields()) {
            if (field.isPrimary() || field.getRelationshipType() != null) {
                continue;
            }

            String methodName = "findBy" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            String returnType = String.format("Optional<%s>", entity.getClassName());
            String parameter = String.format("%s %s", field.getType(), field.getName());

            methods.add(String.format("%s %s(%s);", returnType, methodName, parameter));

            // Ajouter une méthode de recherche par liste de valeurs
            String listMethodName = "findBy" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1) + "In";
            String listParameter = String.format("List<%s> %sList", field.getType(), field.getName());

            methods.add(String.format("List<%s> %s(%s);", entity.getClassName(), listMethodName, listParameter));
        }

        // Ajouter des méthodes de recherche avancées
        methods.add(String.format("Page<%s> findAll(Specification<%s> spec, Pageable pageable);",
                entity.getClassName(), entity.getClassName()));

        return methods;
    }
}