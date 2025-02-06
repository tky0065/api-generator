
package com.apigenerator.generators;

import com.apigenerator.core.GeneratorConfig;
import com.apigenerator.models.EntityModel;
import com.apigenerator.models.FieldModel;
import com.apigenerator.models.RelationshipType;
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
public class MapperGenerator implements CodeGenerator {

    private final Configuration freemarkerConfig;

    public MapperGenerator() {
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        this.freemarkerConfig.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(), "templates"
        );
    }

    @Override
    @SneakyThrows
    public String generate(EntityModel entity, GeneratorConfig config) {
        log.info("Génération du Mapper pour l'entité: {}", entity.getClassName());

        Template template = freemarkerConfig.getTemplate("mapper.ftl");
        Map<String, Object> model = createTemplateModel(entity, config);

        StringWriter writer = new StringWriter();
        template.process(model, writer);

        return writer.toString();
    }

    @Override
    public String getOutputPath(EntityModel entity, GeneratorConfig config) {
        return String.format("%s/service/mapper/%sMapper.java",
                entity.getPackageName().replace(".", "/"),
                entity.getClassName()
        );
    }

    private Map<String, Object> createTemplateModel(EntityModel entity, GeneratorConfig config) {
        Map<String, Object> model = new HashMap<>();
        model.put("entity", entity);
        model.put("config", config);

        // Obtenir les champs avec relations
        List<FieldModel> relationFields = entity.getFields().stream()
                .filter(f -> f.getRelationshipType() != null &&
                        f.getRelationshipType() != RelationshipType.NONE)
                .collect(Collectors.toList());
        model.put("relationFields", relationFields);

        // Obtenir les champs de type fichier
        List<FieldModel> fileFields = entity.getFields().stream()
                .filter(f -> isFileField(f))
                .collect(Collectors.toList());
        model.put("fileFields", fileFields);

        return model;
    }

    private boolean isFileField(FieldModel field) {
        return field.getType().equals("byte[]") ||
                field.getType().equals("Blob") ||
                field.getType().equals("File");
    }
}