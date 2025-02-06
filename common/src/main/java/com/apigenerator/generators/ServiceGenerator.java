// common/src/main/java/com/apigenerator/generators/ServiceGenerator.java
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
public class ServiceGenerator implements CodeGenerator {

    private final Configuration freemarkerConfig;

    public ServiceGenerator() {
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        this.freemarkerConfig.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(), "templates"
        );
    }

    @Override
    @SneakyThrows
    public String generate(EntityModel entity, GeneratorConfig config) {
        log.info("Génération du Service pour l'entité: {}", entity.getClassName());

        // Génération de l'interface du service
        Template interfaceTemplate = freemarkerConfig.getTemplate("service-interface.ftl");
        Map<String, Object> interfaceModel = createTemplateModel(entity, config);
        StringWriter interfaceWriter = new StringWriter();
        interfaceTemplate.process(interfaceModel, interfaceWriter);

        // Génération de l'implémentation du service
        Template implTemplate = freemarkerConfig.getTemplate("service-impl.ftl");
        Map<String, Object> implModel = createTemplateModel(entity, config);
        StringWriter implWriter = new StringWriter();
        implTemplate.process(implModel, implWriter);

        // Retourner les deux fichiers
        return String.format("// Interface du service\n%s\n\n// Implémentation du service\n%s",
                interfaceWriter.toString(), implWriter.toString());
    }

    @Override
    public String getOutputPath(EntityModel entity, GeneratorConfig config) {
        return String.format("%s/service/%sService.java",
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

        return model;
    }
}